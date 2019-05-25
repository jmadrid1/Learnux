package com.example.jovel.learnux.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jovel.learnux.R;
import com.example.jovel.learnux.model.Video;
import com.example.jovel.learnux.utils.OnVideoClickListener;

/**
 * Created by Jovel on 2/7/2018.
 */

public class VideoHolder extends RecyclerView.ViewHolder {

    private TextView mTitle;
    private TextView mDescription;
    private TextView mDuration;
    private ImageView mThumbnail;

    public VideoHolder(View view) {
        super(view);

        mTitle = view.findViewById(R.id.row_title);
        mDescription = view.findViewById(R.id.row_description);
        mDuration = view.findViewById(R.id.row_duration);

        mThumbnail = view.findViewById(R.id.row_thumbnail);
    }

    public void bindVideo(final Video video, final OnVideoClickListener onVideoClickListener){

        mTitle.setText(video.getTitle());
        mDescription.setText(video.getDescription());
        mDuration.setText(video.getDuration());

        Glide.with(mThumbnail.getContext())
                .load(video.getThumbnail())
                .into(mThumbnail);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVideoClickListener.onVideoClicked(video);
            }
        });
    }
}
