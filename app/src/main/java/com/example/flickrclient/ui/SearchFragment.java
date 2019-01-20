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
import android.widget.Button;
import android.widget.ProgressBar;

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
    private View mProgressView;
    private View mErrorView;
    private Button mRetryButton;

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

            mViewModel.getSearchStatus().observe(this, new Observer<SearchViewModel.SearchStatus>() {
                @Override
                public void onChanged(@Nullable SearchViewModel.SearchStatus searchStatus) {
                    if (searchStatus == SearchViewModel.SearchStatus.IDLE) {
                        mProgressView.setVisibility(View.GONE);
                        mErrorView.setVisibility(View.GONE);
                    }
                    else if(searchStatus == SearchViewModel.SearchStatus.RUNNING && mViewModel.getCurrentPage()==0) {
                        mProgressView.setVisibility(View.VISIBLE);
                        mErrorView.setVisibility(View.GONE);
                    }
                    else if(searchStatus == SearchViewModel.SearchStatus.ERROR) {
                        if (mViewModel.getCurrentPage() == 0) {
                            mErrorView.setVisibility(View.VISIBLE);
                        }
                        mProgressView.setVisibility(View.GONE);
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

        mProgressView = root.findViewById(R.id.search_progress_view);
        mErrorView = root.findViewById(R.id.error_view);
        mRetryButton = mErrorView.findViewById(R.id.retry_button);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewModel != null)
                    mViewModel.retry();
            }
        });
        return root;
    }

    @Override
    public void onReachedEndOfList() {
        if (mViewModel != null) {
            mViewModel.fetchNextPage();
        }
    }
}
