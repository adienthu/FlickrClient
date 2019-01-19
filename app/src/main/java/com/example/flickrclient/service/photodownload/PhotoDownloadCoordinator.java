package com.example.flickrclient.service.photodownload;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoDownloadCoordinator implements PhotoDownloader {
    private static final String LOG_TAG = "PhotoDownloader";

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
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(mPhotoUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buf = new byte[1024];
                int count;
                while ((count = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, count);
                }

                return new PhotoDownloadResponse(mPhotoUrl, PhotoDownloadResponse.Status.SUCCESS, outputStream.toByteArray());
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error fetching photos ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return new PhotoDownloadResponse(mPhotoUrl, PhotoDownloadResponse.Status.ERROR, null);
        }
    }
}
