package com.example.flickrclient.service.search;

public interface SearchExecutor {
    void executeRequest(SearchApiRequest request);
    void cancel();

    interface Callback
    {
        void onSearchCompleted(SearchApiResponse response);
    }
}
