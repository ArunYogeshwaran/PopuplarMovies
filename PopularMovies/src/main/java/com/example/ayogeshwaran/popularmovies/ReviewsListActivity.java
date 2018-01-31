package com.example.ayogeshwaran.popularmovies;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayogeshwaran.popularmovies.utilities.MovieJsonUtils;
import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsListActivity extends AppCompatActivity {

    private static final String TAG = ReviewsListActivity.class.getSimpleName();

    private static final String LIST_STATE_KEY = "list_state_key";

    @BindView(R.id.reviewsRecyclerview)
    RecyclerView reviewsRecyclerView;

    @BindView(R.id.reviews_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.reviews_error_textview)
    TextView mErrorTextView;

    private ReviewsAdapter mReviewsAdapter;

    private Movies movie;

    private List<String> mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_list);
        ButterKnife.bind(this);

        movie = getIntent().getParcelableExtra("parcel_data");
        initViews();

        if (savedInstanceState == null) {
            loadReviewsList();
        } else {
            mReviews = savedInstanceState.getStringArrayList(LIST_STATE_KEY);
            if (mReviews != null) {
                if (mReviews.size() != 0) {
                    showReviewsListView();
                    mReviewsAdapter.setReviewsData(mReviews);
                } else {
                    mErrorTextView.setVisibility(View.VISIBLE);
                    mErrorTextView.setText(getString(R.string.no_reviews));
                }
            } else {
                loadReviewsList();
            }
        }

        this.registerReceiver(mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        // Save list state
        bundle.putStringArrayList(LIST_STATE_KEY, (ArrayList<String>) mReviews);

    }

    private final BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {
                if (reviewsRecyclerView.getVisibility() == View.INVISIBLE) {
                    loadReviewsList();
                }
            } else {
                if (mErrorTextView.getVisibility() == View.INVISIBLE) {
                    Toast.makeText(getApplicationContext(), R.string.connection_lost,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mConnReceiver);
        } catch (Exception e) {
            Log.i(TAG, "Exception - unregistering network broadcast receiver");
        }
        super.onDestroy();
    }

    private void initViews() {
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),1);

        reviewsRecyclerView.setLayoutManager(gridLayoutManager);

        mReviewsAdapter = new ReviewsAdapter(this);

        reviewsRecyclerView.setAdapter(mReviewsAdapter);

        reviewsRecyclerView.setHasFixedSize(true);
    }

    private void loadReviewsList() {
        if (NetworkUtils.isOnline(this)) {
            showReviewsListView();

            new ReviewsListActivity.FetchReviewsTask().execute(movie.getId());
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            reviewsRecyclerView.setVisibility(View.INVISIBLE);
            mErrorTextView.setText(getString(R.string.check_network_connection));
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchReviewsTask extends AsyncTask<Integer, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected List<String> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            Integer movieID = params[0];

            URL reviewsUrl = NetworkUtils.getReviewsUrl(movieID, getApplicationContext());

            try {
                mReviews = MovieJsonUtils.getReviewsListFromURL(reviewsUrl);
            }
            catch (JSONException ex) {
                Log.e(TAG, "doInBackground: Unexpected exception :" + ex.getMessage());
                ex.printStackTrace();
            }

            return mReviews;
        }

        @Override
        protected void onPostExecute(List<String> reviews) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (mReviews != null && mReviews.size() != 0) {
                showReviewsListView();
                mReviewsAdapter.setReviewsData(reviews);
            } else {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.no_reviews));
            }
        }
    }

    private void showReviewsListView() {
        reviewsRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        reviewsRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }
}
