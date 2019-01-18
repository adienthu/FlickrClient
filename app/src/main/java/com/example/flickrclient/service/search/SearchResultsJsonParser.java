package com.example.flickrclient.service.search;

import com.example.flickrclient.model.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultsJsonParser {
    private static final String PHOTOS = "photos";
    private static final String PAGE = "page";
    private static final String PAGES = "pages";
    private static final String PHOTO = "photo";
    private static final String PHOTO_ID = "id";
    private static final String PHOTO_FARM = "farm";
    private static final String PHOTO_SERVER = "server";
    private static final String PHOTO_SECRET = "secret";
    private static final String PHOTO_TITLE = "title";

    public SearchApiResponse parseJson(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONObject photosObj = root.getJSONObject(PHOTOS);
        int page = photosObj.getInt(PAGE);
        int numPagesRemaining = photosObj.getInt(PAGES) - page;

        JSONArray photoArr = photosObj.getJSONArray(PHOTO);
        List<Photo> photoList = new ArrayList<>();
        for (int i = 0; i < photoArr.length(); i++) {
            JSONObject photoObj = photoArr.getJSONObject(i);
            String photoId = photoObj.getString(PHOTO_ID);
            int photoFarm = photoObj.getInt(PHOTO_FARM);
            String photoServer = photoObj.getString(PHOTO_SERVER);
            String photoSecret = photoObj.getString(PHOTO_SECRET);
            String photoUrl = String.format(Locale.ENGLISH, "http://farm%d.static.flickr.com/%s/%s_%s.jpg", photoFarm, photoServer, photoId, photoSecret);

            String title = photoObj.getString(PHOTO_TITLE);
            Photo photo = new Photo(title, photoUrl);
            photoList.add(photo);
        }

        return new SearchApiResponse(SearchApiResponse.Status.SUCCESS, photoList, page, numPagesRemaining);
    }
}
