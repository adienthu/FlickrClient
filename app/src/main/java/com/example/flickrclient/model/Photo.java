package com.example.flickrclient.model;

public class Photo {
    private String mTitle;
    private String mUrl;

    public Photo(String title, String url) {
        mTitle = title;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }
}
