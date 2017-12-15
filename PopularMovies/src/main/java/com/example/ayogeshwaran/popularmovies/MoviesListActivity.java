package com.example.ayogeshwaran.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayogeshwaran.popularmovies.utilities.MovieJsonUtils;
import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

public class MoviesListActivity extends AppCompatActivity implements
        MoviesAdapter.IMoviesAdapterClickHandler {
    private final String TAG = MoviesListActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private ProgressBar mLoadingIndicator;

    private TextView mNetworkConnectionTextView;

    private MoviesAdapter mMoviesAdapter;

    private List<Movies> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        intiViews();
    }

    private void intiViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mNetworkConnectionTextView = (TextView) findViewById(R.id.network_connection_textview);

        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),2);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mRecyclerView.setHasFixedSize(true);

        showLoading();

        loadMoviesList(NetworkUtils.POPULAR);
    }

    private void loadMoviesList(String sortOrder) {
        if (isNetworkConnected()) {
            showMovieListView();

            switch (sortOrder) {
                case NetworkUtils.POPULAR:
                    new FetchMoviesTask().execute(NetworkUtils.POPULAR);
                    break;
                case NetworkUtils.TOP_RATED:
                    new FetchMoviesTask().execute(NetworkUtils.TOP_RATED);
                    break;
                 default:
                     new FetchMoviesTask().execute(NetworkUtils.POPULAR);
                     break;
            }
        } else {
            mNetworkConnectionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMovieListView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNetworkConnectionTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.most_popular:
                loadMoviesList(NetworkUtils.POPULAR);
                return true;
            case R.id.highest_rated:
                loadMoviesList(NetworkUtils.TOP_RATED);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int adapterPosition) {
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra("parcel_data", movies.get(adapterPosition));
        startActivity(movieDetailsIntent);
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movies> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sortOrder = params[0];

            URL moviesRequestUrl = NetworkUtils.buildURL(sortOrder, getBaseContext());

            try {
                movies = MovieJsonUtils.getMoviesListFromURL(moviesRequestUrl);
            } catch (JSONException ex) {
                Log.e(TAG, "doInBackground: Unexpected exception :" + ex.getMessage());
                ex.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovieListView();
                mMoviesAdapter.setMoviesData(movies);
            }
        }
    }

    private boolean isNetworkConnected() {
        if (NetworkUtils.isOnline(getBaseContext())) {
            return true;
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            return false;
        }
    }

}
