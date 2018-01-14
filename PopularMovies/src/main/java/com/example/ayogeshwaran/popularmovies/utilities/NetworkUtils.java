package com.example.ayogeshwaran.popularmovies.utilities;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ayogeshwaran on 22/11/17.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String MOVIE_POSTER_SIZE_W185 = "w185";

    private static final String API_KEY = "api_key";

    public static final String POPULAR = "popular";

    public static final String TOP_RATED = "top_rated";

    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String MOVIE_DB_VIDEO_URL = "https://api.themoviedb.org/3/movie/{id}/videos";

    private static final String MOVIE_DB_REVIEWS_URL = "https://api.themoviedb.org/3/movie/{id}/reviews";

    public static URL buildURL(String sortOrder, Context context) {
        Uri builtUri = null;
        switch (sortOrder) {
            case POPULAR:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(POPULAR)
                        .appendQueryParameter(API_KEY, getMetadata(context))
                        .build();
                break;
            case TOP_RATED:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(TOP_RATED)
                        .appendQueryParameter(API_KEY, getMetadata(context))
                        .build();
                break;
            default:

                break;
        }

        URL moviesQueryUrl = null;
        try {
            if (builtUri != null) {
                moviesQueryUrl = new URL(builtUri.toString());
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        return moviesQueryUrl;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try {
                InputStream in = httpURLConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (Exception ex) {
                Log.e(TAG, "Exception occurred when reading response stream" + ex.toString());
                return ex.toString();
            } finally {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    public static String getFullPosterUrl(String posterPath) {
        Uri fullUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                .appendPath(MOVIE_POSTER_SIZE_W185)
                .appendPath("/" + posterPath)
                .build();

        URL fullUrl = null;
        try {
            fullUrl = new URL(fullUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String result = null;
        try {
            if (fullUrl != null) {
                result = java.net.URLDecoder.decode(fullUrl.toString(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();

    }

    private static String getMetadata(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(API_KEY);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getMetadata: Key not found in manifest");
            return null;
        }
        return null;
    }
}
