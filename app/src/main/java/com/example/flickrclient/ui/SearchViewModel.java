package com.example.flickrclient.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.flickrclient.model.Photo;
import com.example.flickrclient.service.search.SearchApiRequest;
import com.example.flickrclient.service.search.SearchApiResponse;
import com.example.flickrclient.service.search.SearchTask;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel implements SearchTask.Callback {

    enum SearchStatus
    {
        IDLE,
        RUNNING,
        ERROR
    }

    private static final int NUM_RESULTS_PER_PAGE = 20;

    private SearchTask mSearchTask;
    private String mSearchQuery;
    private MutableLiveData<SearchStatus> mSearchStatus;
    private MutableLiveData<List<Photo>> mPhotos;
    private int mNumPagesRemaining;

    public SearchViewModel(@NonNull Application application) {
        super(application);
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
        if (query!=null && !query.isEmpty() && !query.equalsIgnoreCase(mSearchQuery))
        {
            mSearchQuery = query;
            mSearchStatus.setValue(SearchStatus.RUNNING);
            if (mSearchTask != null && (mSearchTask.getStatus()==AsyncTask.Status.PENDING || mSearchTask.getStatus()==AsyncTask.Status.RUNNING))
            {
                mSearchTask.cancel(true);
            }
            mSearchTask = new SearchTask(this);
            SearchApiRequest request = new SearchApiRequest(query, 1, NUM_RESULTS_PER_PAGE);
            mSearchTask.execute(request);
        }
    }

    @Override
    public void onSearchCompleted(SearchApiResponse response) {
        if (response.getStatus() == SearchApiResponse.Status.SUCCESS)
        {
            if (response.getPage() == 1) {
                List<Photo> photos = new ArrayList<>(response.getPhotos());
                mPhotos.setValue(photos);
            } else if (response.getPage() > 1) {
                List<Photo> photos = mPhotos.getValue();
                if (photos != null)
                {
                    photos.addAll(response.getPhotos());
                    mPhotos.setValue(photos);
                }
            }

            mSearchStatus.setValue(SearchStatus.IDLE);
        }else if (response.getStatus() == SearchApiResponse.Status.ERROR) {
            mSearchStatus.setValue(SearchStatus.ERROR);
        }else {
            mSearchStatus.setValue(SearchStatus.IDLE);
        }

    }
}
