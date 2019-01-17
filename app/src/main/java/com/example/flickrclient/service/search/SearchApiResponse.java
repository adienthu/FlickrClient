package com.example.flickrclient.service.search;

import com.example.flickrclient.model.Photo;

import java.util.List;

public class SearchApiResponse {
    public enum Status
    {
        SUCCESS,
        ERROR,
        CANCELLED
    }

    private Status mStatus;
    private List<Photo> mPhotos;
    private int mPage;
    private int mNumPagesRemaining;

    public SearchApiResponse(Status status, List<Photo> photos, int page, int numPagesRemaining) {
        this.mStatus = status;
        this.mPhotos = photos;
        this.mPage = page;
        this.mNumPagesRemaining = numPagesRemaining;
    }

    public Status getStatus() {
        return mStatus;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public int getPage() {
        return mPage;
    }

    public int getNumPagesRemaining() {
        return mNumPagesRemaining;
    }
}
