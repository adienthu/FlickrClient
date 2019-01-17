package com.example.flickrclient.service.search;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class SearchTask extends AsyncTask<SearchApiRequest, Void, SearchApiResponse> {

    public interface Callback
    {
        void onSearchCompleted(SearchApiResponse response);
    }

    private WeakReference<Callback> mCallback;

    public SearchTask(Callback callback)
    {
        mCallback = new WeakReference<>(callback);
    }

    @Override
    protected SearchApiResponse doInBackground(SearchApiRequest... searchApiRequests) {
        return null;
    }
}
