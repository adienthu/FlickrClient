package com.example.flickrclient.service.search;

public class SearchApiRequest {

    private String mQueryString;
    private int mPage;
    private int mNumResultsPerPage;

    public SearchApiRequest(String queryString, int page, int numResultsPerPage) {
        mQueryString = queryString;
        mPage = page;
        mNumResultsPerPage = numResultsPerPage;
    }

    public String getQueryString() {
        return mQueryString;
    }

    public int getPage() {
        return mPage;
    }
}
