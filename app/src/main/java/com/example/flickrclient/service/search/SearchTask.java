package com.example.flickrclient.service.search;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.flickrclient.model.Photo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

public class SearchTask extends AsyncTask<SearchApiRequest, Void, SearchApiResponse> implements SearchExecutor {

    private static final String LOG_TAG = "SearchTask";

    private WeakReference<SearchExecutor.Callback> mCallbackRef;

    public SearchTask(SearchExecutor.Callback callback)
    {
        mCallbackRef = new WeakReference<>(callback);
    }

    @Override
    protected SearchApiResponse doInBackground(SearchApiRequest... searchApiRequests) {
        String json = fetchJson(searchApiRequests[0]);
        if (json == null)
            return new SearchApiResponse(SearchApiResponse.Status.ERROR, Collections.<Photo>emptyList(), 0, 0);

        Log.d(LOG_TAG, "Response json - " + json);

        try {
            SearchResultsJsonParser parser = new SearchResultsJsonParser();
            return parser.parseJson(json);
        } catch (Exception e) {
            return new SearchApiResponse(SearchApiResponse.Status.ERROR, Collections.<Photo>emptyList(), 0, 0);
        }
    }

    private String fetchJson(SearchApiRequest searchApiRequest) {
        final String BASE_URL = "https://api.flickr.com/services/rest/?";
        final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("method", "flickr.photos.search")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("text", searchApiRequest.getQueryString())
                .appendQueryParameter("page",String.valueOf(searchApiRequest.getPage()))
                .appendQueryParameter("per_page", String.valueOf(searchApiRequest.getNumResultsPerPage()))
                .appendQueryParameter("safe_search","1")
                .appendQueryParameter("nojsoncallback", "1")
                .build();

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString;

        try {
            URL url = new URL(uri.toString());

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonString = buffer.toString();
            return jsonString;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fetching photos ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onCancelled(SearchApiResponse searchApiResponse) {
        super.onCancelled(searchApiResponse);
        SearchApiResponse response = new SearchApiResponse(SearchApiResponse.Status.CANCELLED, Collections.<Photo>emptyList(), 0, 0);
        SearchExecutor.Callback callback = mCallbackRef.get();
        if (callback != null) {
            callback.onSearchCompleted(response);
        }
    }

    @Override
    protected void onPostExecute(SearchApiResponse searchApiResponse) {
        SearchExecutor.Callback callback = mCallbackRef.get();
        if (callback != null) {
            callback.onSearchCompleted(searchApiResponse);
        }
    }

    @Override
    public void executeRequest(SearchApiRequest request) {
        execute(request);
    }

    @Override
    public void cancel() {
        if (getStatus()==Status.PENDING || getStatus()==Status.RUNNING)
        {
            cancel(true);
        }
    }
}
