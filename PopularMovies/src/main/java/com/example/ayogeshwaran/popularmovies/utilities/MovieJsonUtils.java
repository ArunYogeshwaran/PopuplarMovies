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
import java.util.List;

/**
 * Created by ayogeshwaran on 09/12/17.
 */

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getName();

    public static List<Movies> getMoviesListFromURL(URL moviesRequestUrl)
            throws JSONException {
        final String RESULTS = "results";

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
}
