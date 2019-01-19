package com.example.flickrclient.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flickrclient.R;
import com.example.flickrclient.model.Photo;
import com.example.flickrclient.service.factory.FactoryLocator;
import com.example.flickrclient.service.photodownload.PhotoDownloadResponse;
import com.example.flickrclient.service.photodownload.PhotoDownloader;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>
{
    private static final String LOG_TAG = "SearchAdapter";

    private List<Photo> mPhotos;
    private ExecutorService mTaskExecutor = Executors.newCachedThreadPool();

    public SearchAdapter(List<Photo> photos) {
        mPhotos = photos;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_cell, viewGroup, false);
        return new SearchViewHolder(itemView, mTaskExecutor);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {
        Photo photo = mPhotos.get(i);
        searchViewHolder.mTitleView.setText(photo.getTitle());
        searchViewHolder.bindPhoto(photo.getUrl());
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void refreshList(List<Photo> photos) {
        mPhotos = photos;
        notifyDataSetChanged();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements PhotoDownloader.Callback, PhotoDecodeTask.Callback
    {
        private String mPhotoUrl;
        private ImageView mPhotoView;
        TextView mTitleView;
        View mItemView;
        WeakReference<ExecutorService> mDecodeTaskExecutor;

        public SearchViewHolder(@NonNull View itemView, ExecutorService decodeTaskExecutor) {
            super(itemView);
            mItemView = itemView;
            mPhotoView = itemView.findViewById(R.id.photo_view);
            mTitleView = itemView.findViewById(R.id.title_view);
            mDecodeTaskExecutor = new WeakReference<>(decodeTaskExecutor);
        }

        public void bindPhoto(String photoUrl)
        {
            mPhotoUrl = photoUrl;
            mPhotoView.setImageBitmap(null);
            FactoryLocator.getFactory().createPhotoDownloader().download(photoUrl, this);
        }

        public int convertDpToPixel(float dp){
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return Math.round(px);
        }

        @Override
        public void onPhotoDownloaded(PhotoDownloadResponse response) {
            if (response.getStatus() == PhotoDownloadResponse.Status.SUCCESS && response.getPhotoUrl().equals(mPhotoUrl))
            {
                // decode
                int reqWidth = mPhotoView.getWidth();
                if (reqWidth == 0) {
//                    Log.d(LOG_TAG, "photoView width is 0");
                    reqWidth = convertDpToPixel(240);
                }
                int reqHeight = mPhotoView.getHeight();
                if (reqHeight == 0) {
//                    Log.d(LOG_TAG, "photoView height is 0");
                    float heightInDp = mPhotoView.getResources().getDimension(R.dimen.photo_height);
                    reqHeight = convertDpToPixel(heightInDp);
                }
                PhotoDecodeTask.PhotoDecodeCallable callable = new PhotoDecodeTask.PhotoDecodeCallable(mPhotoUrl, response.getBytes(), reqWidth, reqHeight);
                PhotoDecodeTask decodeTask = new PhotoDecodeTask(callable, this);
                ExecutorService executor = mDecodeTaskExecutor.get();
                if (executor != null) {
                    executor.submit(decodeTask);
                }
            }
        }

        @Override
        public void onPhotoDecodeCompleted(PhotoDecodeTask.Result result) {
            if (result != null && result.getPhotoUrl().equals(mPhotoUrl)) {
                mPhotoView.setImageBitmap(result.getBitmap());
            }
        }
    }
}
