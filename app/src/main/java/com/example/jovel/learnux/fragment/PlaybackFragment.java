package com.example.jovel.learnux.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jovel.learnux.R;
import com.example.jovel.learnux.config.Config;
import com.example.jovel.learnux.utils.OnVideoExitListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class PlaybackFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private static final String VIDEO_ARG = "id";

    private YouTubePlayer mPlayer;
    private OnVideoExitListener mOnVideoExitListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mOnVideoExitListener = (OnVideoExitListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_playback, container, false);

        YouTubePlayerSupportFragment frag = new YouTubePlayerSupportFragment();
        frag.initialize(Config.YOUTUBE_API, this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frag_container, frag).commit();

        return v;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

        mPlayer = player;
        mPlayer.setFullscreen(false);
        mPlayer.setShowFullscreenButton(false);

        if(!wasRestored) {
            playVideo();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (!youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(),1).show();
        }
    }

    public void pauseVideo() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    public void playVideo(){
        mPlayer.loadVideo(getArguments().getString(VIDEO_ARG));
        mPlayer.play();
    }
}
