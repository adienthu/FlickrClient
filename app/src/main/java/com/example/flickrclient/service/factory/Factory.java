package com.example.flickrclient.service.factory;

import com.example.flickrclient.service.photodownload.PhotoDownloader;
import com.example.flickrclient.service.search.SearchExecutor;

public interface Factory {
    SearchExecutor createSearchTask(SearchExecutor.Callback callback);
    PhotoDownloader createPhotoDownloader();
}
