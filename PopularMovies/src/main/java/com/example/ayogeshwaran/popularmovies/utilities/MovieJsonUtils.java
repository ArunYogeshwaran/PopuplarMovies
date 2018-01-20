package com.example.ayogeshwaran.popularmovies.utilities;

import android.util.Log;

import com.example.ayogeshwaran.popularmovies.Movies;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayogeshwaran on 09/12/17.
 */

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getName();

    private static final String RESULTS = "results";

    private static final String REVIEWS_KEY = "content";

    private static final String VIDEOS_KEY = "key";

    public static List<Movies> getMoviesListFromURL(URL moviesRequestUrl)
            throws JSONException {
        String jsonMovieResponse = null;
        try {
            jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
        } catch (IOException ex) {
            Log.e(TAG, "Unexpected exception when fetching movies from" +
                    moviesRequestUrl.toString() + ": " + ex.getMessage());
            ex.printStackTrace();
        }

        JSONObject movieJson = new JSONObject(jsonMovieResponse);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        Type listType = new TypeToken<List<Movies>>(){}.getType();

        return new Gson().fromJson(movieArray.toString(), listType);
    }

    public static List<String> getReviewsListFromURL(URL moviesRequestUrl) throws JSONException {
        List<String> reviewsList = new ArrayList<>();

        String jsonMovieResponse = null;
        try {
            jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
        } catch (IOException ex) {
            Log.e(TAG, "Unexpected exception when fetching reviews from" +
                    moviesRequestUrl.toString() + ": " + ex.getMessage());
            ex.printStackTrace();
        }

        JSONObject reviewJSON = new JSONObject(jsonMovieResponse);
        JSONArray reviewsArray = reviewJSON.getJSONArray(RESULTS);

        // Iterate the loop
        for (int i = 0; i < reviewsArray.length(); i++) {
            // get value with the NODE key
            JSONObject obj = reviewsArray.getJSONObject(i);
            String review =  obj.getString(REVIEWS_KEY);

            try {
                reviewsList.add(review);
            } catch (Exception ex) {
                String message = ex.getMessage();
                ex.printStackTrace();
            }
        }

        return reviewsList;
    }

    public static List<String> getVideosListFromURL(URL moviesRequestUrl) throws JSONException {
        List<String> videosList = new ArrayList<>();

        String jsonMovieResponse = null;
        try {
            jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
        } catch (IOException ex) {
            Log.e(TAG, "Unexpected exception when fetching videos from" +
                    moviesRequestUrl.toString() + ": " + ex.getMessage());
            ex.printStackTrace();
        }

        JSONObject videoJSON = new JSONObject(jsonMovieResponse);
        JSONArray videosArray = videoJSON.getJSONArray(RESULTS);

        // Iterate the loop
        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject obj = videosArray.getJSONObject(i);
            String videoKey =  obj.getString(VIDEOS_KEY);

            try {
                String youtubeUrl = NetworkUtils.buildYoutubeURL(videoKey);
                videosList.add(youtubeUrl);
            } catch (Exception ex) {
                String message = ex.getMessage();
                ex.printStackTrace();
            }
        }

        return videosList;
    }

}
