package com.example.flickrclient.ui;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PhotoDecodeTask extends FutureTask<Bitmap> {
    private final WeakReference<Callback> mCallbackRef;

    public PhotoDecodeTask(@NonNull Callable<Bitmap> callable, Callback callback) {
        super(callable);
        mCallbackRef = new WeakReference<>(callback);
    }

    protected void done() {
        final PhotoDecodeTask task = this;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PhotoDecodeTask.Callback cb = mCallbackRef.get();
                if (cb != null) {
                    try {
                        cb.onPhotoDecodeCompleted(task.get());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        cb.onPhotoDecodeCompleted(null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        cb.onPhotoDecodeCompleted(null);
                    }
                }
            }
        });
    }

    interface Callback {
        void onPhotoDecodeCompleted(Bitmap bitmap);
    }

    static class PhotoDecodeCallable implements Callable<Bitmap> {
        private final byte[] mBytes;
        private final int mReqWidth;
        private final int mReqHeigth;

        public PhotoDecodeCallable(byte[] bytes, int reqWidth, int reqHeight) {
            mBytes = bytes;
            mReqWidth = reqWidth;
            mReqHeigth = reqHeight;
        }

        @Override
        public Bitmap call() throws Exception {
            return null;
        }
    }
}
