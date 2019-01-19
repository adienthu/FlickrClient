package com.example.flickrclient.service.photodownload;

public interface PhotoDownloader {
    interface Callback {
        void onPhotoDownloaded(PhotoDownloadResponse response);
    }
    void download(String photoUrl, PhotoDownloader.Callback callback);
}
