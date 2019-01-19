package com.example.flickrclient.service.photodownload;

public class PhotoDownloadResponse {
    public enum Status
    {
        SUCCESS,
        ERROR
    }

    private String mPhotoUrl;
    private Status mStatus;
    private byte[] mBytes;

    public PhotoDownloadResponse(String photoUrl, Status status, byte[] bytes) {
        mPhotoUrl = photoUrl;
        mStatus = status;
        mBytes = bytes;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public Status getStatus() {
        return mStatus;
    }

    public byte[] getBytes() {
        return mBytes;
    }
}
