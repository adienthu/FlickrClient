package com.example.flickrclient.service.photodownload;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoDownloadCoordinator implements PhotoDownloader {
    private ExecutorService mTaskExecutor = Executors.newCachedThreadPool();

    @Override
    public void download(String photoUrl, Callback callback) {
        DownloadPhotoTask task = new DownloadPhotoTask(new PhotoDownloadCallable(photoUrl), callback);
        mTaskExecutor.submit(task);
    }

    static class PhotoDownloadCallable implements Callable<PhotoDownloadResponse> {
        private final String mPhotoUrl;

        public PhotoDownloadCallable(String photoUrl) {
            mPhotoUrl = photoUrl;
        }

        @Override
        public PhotoDownloadResponse call() throws Exception {
            return null;
        }
    }
}
