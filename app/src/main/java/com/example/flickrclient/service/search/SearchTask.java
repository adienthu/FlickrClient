package com.example.flickrclient.service.search;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

public class SearchTask extends AsyncTask<SearchApiRequest, Void, SearchApiResponse> {

    private WeakReference<SearchExecutor.Callback> mCallback;

    public SearchTask(SearchExecutor.Callback callback)
    {
        mCallback = new WeakReference<>(callback);
    }

    @Override
    protected SearchApiResponse doInBackground(SearchApiRequest... searchApiRequests) {
        return null;
    }
}
