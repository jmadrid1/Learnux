package com.example.jovel.learnux.model;


public class Video {

    private String mId;
    private String mTitle;
    private String mCategory;
    private String mDescription;
    private String mThumbnail;
    private String mDuration;

    public Video(){
    }

    public String getId(){
        return mId;
    }

    public void setId(String id){
        this.mId = id;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public String getCategory(){ return mCategory; }

    public void setCategory(String category){
        mCategory = category;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

    public String getThumbnail(){
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail){
        mThumbnail = thumbnail;
    }

    public String getDuration(){
        return mDuration;
    }

    public void setDuration(String duration){
        mDuration = duration;
    }

}
