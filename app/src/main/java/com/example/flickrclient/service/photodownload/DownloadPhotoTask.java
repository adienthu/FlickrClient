package com.example.flickrclient.service.photodownload;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class DownloadPhotoTask extends FutureTask<PhotoDownloadResponse> {
    private final WeakReference<PhotoDownloader.Callback> mCallbackRef;

    public DownloadPhotoTask(@NonNull Callable<PhotoDownloadResponse> callable, PhotoDownloader.Callback callback) {
        super(callable);
        mCallbackRef = new WeakReference<>(callback);
    }

    @Override
    protected void done() {
        final DownloadPhotoTask task = this;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PhotoDownloader.Callback cb = mCallbackRef.get();
                if (cb != null) {
                    try {
                        cb.onPhotoDownloaded(task.get());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        cb.onPhotoDownloaded(new PhotoDownloadResponse("", PhotoDownloadResponse.Status.ERROR,null));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        cb.onPhotoDownloaded(new PhotoDownloadResponse("", PhotoDownloadResponse.Status.ERROR,null));
                    }
                }
            }
        });
    }
}
