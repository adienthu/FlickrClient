package com.example.flickrclient.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flickrclient.R;
import com.example.flickrclient.model.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment implements SearchAdapter.Callback {

    private static final int NUM_COLUMNS = 3;

    private SearchViewModel mViewModel;
    private RecyclerView mPhotoList;
    private SearchAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new SearchAdapter(Collections.<Photo>emptyList(), this);
        if (getActivity() != null)
        {
            mViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
            mViewModel.getPhotos().observe(this, new Observer<List<Photo>>() {
                @Override
                public void onChanged(@Nullable List<Photo> photos) {
                    if (photos != null) {
                        if (mViewModel.getCurrentPage() == 1) {
                            mAdapter.refreshList(photos);
                        }else if(mViewModel.getCurrentPage() > 1) {
                            int currentCount = mAdapter.getItemCount();
                            int numNewPhotos = photos.size() - currentCount;
                            if (numNewPhotos > 0) {
                                List<Photo> newPhotos = photos.subList(currentCount-1, (currentCount-1) + numNewPhotos);
                                mAdapter.addPhotos(currentCount, newPhotos);
                            }
                        }
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        mPhotoList = root.findViewById(R.id.search_result_list);
        mPhotoList.setLayoutManager(new GridLayoutManager(getContext(), NUM_COLUMNS));
        mPhotoList.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onReachedEndOfList() {
        if (mViewModel != null) {
            mViewModel.fetchNextPage();
        }
    }
}
