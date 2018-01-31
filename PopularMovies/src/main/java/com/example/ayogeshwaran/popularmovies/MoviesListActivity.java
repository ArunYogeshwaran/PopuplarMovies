package com.example.ayogeshwaran.popularmovies;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.ayogeshwaran.popularmovies.data.MoviesContract;
import com.example.ayogeshwaran.popularmovies.utilities.MovieJsonUtils;
import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesListActivity extends AppCompatActivity implements
        MoviesAdapter.IMoviesAdapterClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_MOVIE_LOADER = 33;

    private static final String LIST_STATE_KEY = "list_state_key";

    private static final String POPULAR = NetworkUtils.POPULAR;

    private static final String TOP_RATED = NetworkUtils.TOP_RATED;

    private static final String FAVORITES = "favorites";

    private static String CURRENT_VIEW = NetworkUtils.POPULAR;

    private final String TAG = MoviesListActivity.class.getSimpleName();

    @BindView(R.id.recyclerview_movies)
    RecyclerView moviesRecyclerView;

    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.error_textview)
    TextView mErrorTextView;

    private MoviesAdapter mMoviesAdapter;

    private List<Movies> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        if (savedInstanceState == null) {
            loadMovies();
        } else {
            if (NetworkUtils.isOnline(this)) {
                mMovies = savedInstanceState.getParcelableArrayList(LIST_STATE_KEY);
                if (mMovies != null) {
                    showMovieListView();
                    mMoviesAdapter.setMoviesData(mMovies);
                } else {
                    loadMovies();
                }
            } else {
                savedInstanceState.putParcelableArrayList(LIST_STATE_KEY, null);
                showNoInternetMessage();
            }
        }

        this.registerReceiver(mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        // Save list state
        bundle.putParcelableArrayList(LIST_STATE_KEY,
                (ArrayList<? extends Parcelable>) mMovies);

    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mConnReceiver);
        } catch (Exception e) {
            Log.i(TAG, "Exception - unregistering network broadcast receiver");
        }
        super.onDestroy();
    }

    private final BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {
                if (moviesRecyclerView.getVisibility() == View.INVISIBLE) {
                    loadMoviesList(NetworkUtils.POPULAR);
                }
            } else {
                if (mErrorTextView.getVisibility() == View.INVISIBLE) {
                    Toast.makeText(getApplicationContext(), R.string.connection_lost,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void initViews() {
        setContentView(R.layout.activity_movies_list);
        ButterKnife.bind(this);

        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),getSpanCount(getApplicationContext()));

        moviesRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter = new MoviesAdapter(this, this);

        moviesRecyclerView.setAdapter(mMoviesAdapter);

        moviesRecyclerView.setHasFixedSize(true);
    }

    private void loadMovies() {
        showLoading();

        if (!CURRENT_VIEW.equalsIgnoreCase(FAVORITES)) {
            loadMoviesList(CURRENT_VIEW);
        } else {
            getFavoritesFromDb();
        }
    }

    private void loadMoviesList(String sortOrder) {
        if (NetworkUtils.isOnline(this)) {
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
            showNoInternetMessage();
        }
    }

    private void showNoInternetMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setText(getString(R.string.check_network_connection));
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        moviesRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMovieListView() {
        moviesRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
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
                CURRENT_VIEW = POPULAR;
                loadMoviesList(NetworkUtils.POPULAR);
                return true;
            case R.id.highest_rated:
                CURRENT_VIEW = TOP_RATED;
                loadMoviesList(NetworkUtils.TOP_RATED);
                return true;
            case R.id.favorite:
                CURRENT_VIEW = FAVORITES;
                getFavoritesFromDb();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavoritesFromDb() {
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }

    @Override
    public void onClick(int adapterPosition) {
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra("parcel_data", mMovies.get(adapterPosition));
        startActivity(movieDetailsIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri = MoviesContract.MoviesEntry.CONTENT_URI;

        return new CursorLoader(this,
                contentUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!CURRENT_VIEW.equalsIgnoreCase(FAVORITES)) {
            return;
        }

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (cursor.getCount() != 0) {
            setMoviesFromCursor(cursor);
            showMovieListView();
            mMoviesAdapter.setMoviesData(mMovies);
        } else {
            moviesRecyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mErrorTextView.setText(getString(R.string.no_favorites));
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.setMoviesData(null);
    }

    private void setMoviesFromCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            if (mMovies != null) {
                mMovies.clear();
            }

            do {
                Movies movie = new Movies();
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));
                movie.setId(cursor.getInt(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)));
                movie.setVoteCount(cursor.getInt(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT)));
                movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));
                movie.setVideo(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_VIDEO))));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_TITLE)));
                movie.setPopularity(cursor.getDouble(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_POPULARITY)));
                movie.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH)));
                movie.setAdult(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_ADULT))));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(
                        MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));

                if (mMovies == null) {
                    mMovies = new ArrayList<Movies>();
                }
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected List<Movies> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sortOrder = params[0];

            URL moviesRequestUrl = NetworkUtils.buildURL(sortOrder, getBaseContext());

            try {
                mMovies = MovieJsonUtils.getMoviesListFromURL(moviesRequestUrl);
            } catch (JSONException ex) {
                Log.e(TAG, "doInBackground: Unexpected exception :" + ex.getMessage());
                ex.printStackTrace();
            }

            return mMovies;
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

    private static int getSpanCount(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        float dpWidth = context.getResources().getDisplayMetrics().widthPixels / density;
        return Math.round(dpWidth / 200);
    }
}
