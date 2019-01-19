package com.example.flickrclient.service.factory;

import com.example.flickrclient.service.photodownload.PhotoDownloadCoordinator;
import com.example.flickrclient.service.photodownload.PhotoDownloader;
import com.example.flickrclient.service.search.SearchExecutor;
import com.example.flickrclient.service.search.SearchTask;

public class ServiceFactory implements Factory{
    private PhotoDownloader mPhotoDownloader;

    @Override
    public SearchExecutor createSearchTask(SearchExecutor.Callback callback) {
        return new SearchTask(callback);
    }

    @Override
    public PhotoDownloader createPhotoDownloader() {
        if (mPhotoDownloader == null)
            mPhotoDownloader = new PhotoDownloadCoordinator();
        return mPhotoDownloader;
    }
}
