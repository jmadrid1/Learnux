package com.example.jovel.learnux.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jovel.learnux.R;
import com.example.jovel.learnux.adapter.VideoAdapter;
import com.example.jovel.learnux.fragment.PlaybackFragment;
import com.example.jovel.learnux.model.Video;
import com.example.jovel.learnux.utils.OnVideoClickListener;
import com.example.jovel.learnux.utils.OnVideoExitListener;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.jovel.learnux.constants.Constants.*;

public class MainActivity extends AppCompatActivity
        implements OnVideoClickListener, OnVideoExitListener {

    private static final String VIDEO_ARG = "id";

    private DatabaseReference mDatabase;
    private List<Video> mVideos;

    private VideoAdapter mAdapter;
    private PlaybackFragment mPlaybackFragment;
    private ImageView mEmptyList;
    private TextView mEmptyListText;

    private Boolean mBasics = false;
    private Boolean mNetworking = false;
    private Boolean mSecurity = false;
    private Boolean mStorage = false;
    private Boolean mSystem = false;

    private SwitchCompat mBasicsSwitch;
    private SwitchCompat mNetworkingSwitch;
    private SwitchCompat mSecuritySwitch;
    private SwitchCompat mStorageSwitch;
    private SwitchCompat mSystemSwitch;
    private YouTubePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlaybackFragment = new PlaybackFragment();
        Bundle args = new Bundle();
        mPlaybackFragment.setArguments(args);

        mEmptyList = (ImageView)findViewById(R.id.main_empty_list);
        mEmptyList.setImageResource(R.drawable.ic_empty_list);

        mEmptyListText = (TextView)findViewById(R.id.main_empty_list_text);
        mEmptyListText.setText(R.string.text_empty_list_message);

        mVideos = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference(ALL_VIDEOS);
        getVideosFromFireDB();

        mAdapter = new VideoAdapter(this, mVideos);

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_videos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        armFilterSwitches(navigationView);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

            if (mPlaybackFragment.isVisible()) {
                drawer.closeDrawer(GravityCompat.START);
            }else {
                onVideoExit();
            }
        }else{
            if(mPlaybackFragment.isVisible()){
                onVideoExit();
            }else{
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onVideoClicked(Video video) {

        mPlaybackFragment.getArguments().putString(VIDEO_ARG, video.getId());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (!mPlaybackFragment.isAdded()) {
            ft.add(R.id.frag_video_container, mPlaybackFragment).commit();
        } else {
            ft.show(mPlaybackFragment).commit();
            mPlaybackFragment.playVideo();
        }
    }

    @Override
    public void onVideoExit() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(mPlaybackFragment).commit();
        mPlaybackFragment.pauseVideo();
    }

    /**
     * Checks for network connectivity
     * @return
     */
    private boolean hasNetworkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void attemptVideoCollection(){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getVideosFromFireDB();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 5000);
    }

    private void showNoNetworkConnectionDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_no_connection)
                .setMessage(R.string.dialog_message_no_connection)
                .setNeutralButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        attemptVideoCollection();
                    }
                })
                .show();
    }

    /**
     * Inflates our filter switches for filtering videos
     * @param navigationView
     */
    private void armFilterSwitches(NavigationView navigationView){

        mBasicsSwitch = (SwitchCompat)navigationView.getMenu().getItem(0).getActionView().findViewById(R.id.drawer_switch);
        mBasicsSwitch.setChecked(mBasics);
        mBasicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mBasics = isChecked;
                mBasicsSwitch.setChecked(mBasics);
                getVideosFromFireDB();
            }
        });

        mNetworkingSwitch = (SwitchCompat)navigationView.getMenu().getItem(1).getActionView().findViewById(R.id.drawer_switch);
        mNetworkingSwitch.setChecked(mNetworking);
        mNetworkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mNetworking = isChecked;
                mNetworkingSwitch.setChecked(mNetworking);
                getVideosFromFireDB();
            }
        });

        mSecuritySwitch = (SwitchCompat)navigationView.getMenu().getItem(2).getActionView().findViewById(R.id.drawer_switch);
        mSecuritySwitch.setChecked(mSecurity);
        mSecuritySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mSecurity = isChecked;
                mSecuritySwitch.setChecked(mSecurity);
                getVideosFromFireDB();
            }
        });

        mStorageSwitch = (SwitchCompat)navigationView.getMenu().getItem(3).getActionView().findViewById(R.id.drawer_switch);
        mStorageSwitch.setChecked(mStorage);
        mStorageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mStorage = isChecked;
                mStorageSwitch.setChecked(mStorage);
                getVideosFromFireDB();
            }
        });

        mSystemSwitch = (SwitchCompat)navigationView.getMenu().getItem(4).getActionView().findViewById(R.id.drawer_switch);
        mSystemSwitch.setChecked(mSystem);
        mSystemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mSystem = isChecked;
                mSystemSwitch.setChecked(mSystem);
                getVideosFromFireDB();
            }
        });
    }

    /**
     * Presents our prepared/filtered videos based on Switch values
     * @param videos
     * @return
     */
    private List<Video> prepVideos(List<Video> videos) {

        if (!mBasics && !mNetworking && !mSecurity && !mStorage && !mSystem) {
            return videos;
        }

        List<Video> filteredContent = new ArrayList<>();
        for (Video filteredVideo : videos) {

            String category = filteredVideo.getCategory();

            switch (category){
                case BASICS_CATEGORY:
                    if(mBasics)
                        filteredContent.add(filteredVideo);
                    break;

                case NETWORKING_CATEGORY:
                    if(mNetworking)
                        filteredContent.add(filteredVideo);
                    break;

                case SECURITY_CATEGORY:
                    if(mSecurity)
                        filteredContent.add(filteredVideo);
                    break;

                case STORAGE_CATEGORY:
                    if(mStorage)
                        filteredContent.add(filteredVideo);
                    break;

                case SYSTEM_CATEGORY:
                    if(mSystem)
                        filteredContent.add(filteredVideo);
                    break;
            }
        }

        return filteredContent;
    }

    /**
     * Iterates through the videos from Firebase and puts each video in the array
     * @param iterable
     * @return
     */
    private static ArrayList<Video> createVideoSet(Iterable <DataSnapshot> iterable) {
        ArrayList<Video> videos = new ArrayList<>();

        for (DataSnapshot item : iterable) {
            videos.add(item.getValue(Video.class));
        }
        return videos;
    }

    /**
     * Populates mVideos array with the Firebase videos
     */
    private void getVideosFromFireDB(){

        if (!hasNetworkConnection()){
            showNoNetworkConnectionDialog();
            mEmptyList.setVisibility(View.VISIBLE);
            mEmptyListText.setVisibility(View.VISIBLE);
        }else{
            mEmptyList.setVisibility(View.INVISIBLE);
            mEmptyListText.setVisibility(View.INVISIBLE);
        }

        mDatabase.child(ALL_VIDEOS);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Video> fireVideos = createVideoSet(dataSnapshot.getChildren());

                mVideos.clear();
                mVideos.addAll(prepVideos(fireVideos)); //filters videos here

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
