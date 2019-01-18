package com.example.flickrclient.service.factory;

import com.example.flickrclient.service.search.SearchExecutor;
import com.example.flickrclient.service.search.SearchTask;

public class ServiceFactory implements Factory{
    @Override
    public SearchExecutor createSearchTask(SearchExecutor.Callback callback) {
        return new SearchTask(callback);
    }
}
