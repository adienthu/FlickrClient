package com.example.flickrclient.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class PhotoDecodeTask extends FutureTask<PhotoDecodeTask.Result> {
    private static final String LOG_TAG = "PhotoDecodeTask";

    private final WeakReference<Callback> mCallbackRef;

    public PhotoDecodeTask(@NonNull Callable<Result> callable, Callback callback) {
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
        void onPhotoDecodeCompleted(Result result);
    }

    static class PhotoDecodeCallable implements Callable<Result> {
        private final String mPhotoUrl;
        private final byte[] mBytes;
        private final int mReqWidth;
        private final int mReqHeigth;

        public PhotoDecodeCallable(String photoUrl, byte[] bytes, int reqWidth, int reqHeight) {
            mPhotoUrl = photoUrl;
            mBytes = bytes;
            mReqWidth = reqWidth;
            mReqHeigth = reqHeight;
        }

        @Override
        public Result call() throws Exception {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, mReqWidth, mReqHeigth);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length, options);
            return new Result(mPhotoUrl, bitmap);
        }

        private static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
//            Log.d(LOG_TAG, "ReqWidth - " + reqWidth);
//            Log.d(LOG_TAG, "ReqHeight - " + reqHeight);
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }

    static class Result {
        private String mPhotoUrl;
        private Bitmap mBitmap;

        public Result(String photoUrl, Bitmap bitmap) {
            mPhotoUrl = photoUrl;
            mBitmap = bitmap;
        }

        public String getPhotoUrl() {
            return mPhotoUrl;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }
    }
}
