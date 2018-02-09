package com.example.jovel.learnux.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jovel.learnux.R;
import com.example.jovel.learnux.model.Video;
import com.example.jovel.learnux.utils.OnVideoClickListener;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {

    private Context mContext;
    private List<Video> mVideos;
    private OnVideoClickListener mOnVideoClickListener;

    public VideoAdapter(Context context, List<Video> videos){
        mContext = context;
        mVideos = videos;
        mOnVideoClickListener = (OnVideoClickListener)context;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_row_video, parent, false);

        return new VideoHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        Video video = mVideos.get(position);

        holder.bindVideo(video, mOnVideoClickListener);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }
}