package com.example.flickrclient.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.flickrclient.model.Photo;
import com.example.flickrclient.service.factory.FactoryLocator;
import com.example.flickrclient.service.search.SearchApiRequest;
import com.example.flickrclient.service.search.SearchApiResponse;
import com.example.flickrclient.service.search.SearchExecutor;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel implements SearchExecutor.Callback {

    public enum SearchStatus
    {
        IDLE,
        RUNNING,
        ERROR
    }

    private static final int NUM_RESULTS_PER_PAGE = 20;

    private SearchExecutor mSearchExecutor;
    private String mSearchQuery;
    private MutableLiveData<SearchStatus> mSearchStatus;
    private MutableLiveData<List<Photo>> mPhotos;
    private int mCurrentPage;
    private int mNumPagesRemaining;

    public SearchViewModel() {
        mPhotos = new MutableLiveData<>();
        mSearchStatus = new MutableLiveData<>();
        mSearchStatus.setValue(SearchStatus.IDLE);
    }

    public MutableLiveData<List<Photo>> getPhotos() {
        return mPhotos;
    }

    public MutableLiveData<SearchStatus> getSearchStatus() {
        return mSearchStatus;
    }

    public void search(String query)
    {
        if (query!=null && !query.isEmpty())
        {
            mSearchQuery = query;
            mCurrentPage = 0;
            mNumPagesRemaining = 0;
            mSearchStatus.setValue(SearchStatus.RUNNING);
            if (mSearchExecutor != null)
            {
                mSearchExecutor.cancel();
            }
            mSearchExecutor = FactoryLocator.getFactory().createSearchTask(this);
            SearchApiRequest request = new SearchApiRequest(query, 1, NUM_RESULTS_PER_PAGE);
            mSearchExecutor.executeRequest(request);
        }
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void retry() {
        if (mCurrentPage == 0) {
            search(mSearchQuery);
        }else {
            fetchNextPage();
        }
    }

    public void fetchNextPage()
    {
        if (mSearchStatus.getValue() != SearchStatus.RUNNING && mNumPagesRemaining > 0)
        {
            mSearchStatus.setValue(SearchStatus.RUNNING);
            SearchApiRequest request = new SearchApiRequest(mSearchQuery, mCurrentPage+1, NUM_RESULTS_PER_PAGE);
            mSearchExecutor = FactoryLocator.getFactory().createSearchTask(this);
            mSearchExecutor.executeRequest(request);
        }
    }

    @Override
    public void onSearchCompleted(SearchApiResponse response) {
        if (response.getStatus() == SearchApiResponse.Status.SUCCESS)
        {
            if (response.getPage() == 1) {
                mCurrentPage = response.getPage();
                List<Photo> photos = new ArrayList<>(response.getPhotos());
                mPhotos.setValue(photos);
            } else if (response.getPage() > 1) {
                List<Photo> photos = mPhotos.getValue();
                if (photos != null)
                {
                    mCurrentPage = response.getPage();
                    photos.addAll(response.getPhotos());
                    mPhotos.setValue(photos);
                }
            }

            mNumPagesRemaining = response.getNumPagesRemaining();
            mSearchStatus.setValue(SearchStatus.IDLE);
        }else if (response.getStatus() == SearchApiResponse.Status.ERROR) {
            mSearchStatus.setValue(SearchStatus.ERROR);
        }else {
            mSearchStatus.setValue(SearchStatus.IDLE);
        }
    }
}
