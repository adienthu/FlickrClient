package com.example.flickrclient.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flickrclient.R;
import com.example.flickrclient.model.Photo;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>
{
    private List<Photo> mPhotos;

    public SearchAdapter(List<Photo> photos) {
        mPhotos = photos;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_cell, viewGroup, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i) {
        Photo photo = mPhotos.get(i);
        searchViewHolder.mTitleView.setText(photo.getTitle());
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void refreshList(List<Photo> photos) {
        mPhotos = photos;
    }

    class SearchViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mPhotoView;
        TextView mTitleView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhotoView = itemView.findViewById(R.id.photo_view);
            mTitleView = itemView.findViewById(R.id.title_view);
        }
    }
}
