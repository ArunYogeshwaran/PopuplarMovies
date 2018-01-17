package com.example.ayogeshwaran.popularmovies;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ayogeshwaran.popularmovies.utilities.MovieJsonUtils;
import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsListActivity extends AppCompatActivity {

    private static final String TAG = ReviewsListActivity.class.getSimpleName();

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
        intiViews();
    }

    private void intiViews() {
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),1);

        reviewsRecyclerView.setLayoutManager(gridLayoutManager);

        mReviewsAdapter = new ReviewsAdapter(this);

        reviewsRecyclerView.setAdapter(mReviewsAdapter);

        reviewsRecyclerView.setHasFixedSize(true);

        showLoading();

        loadReviewsList();
    }

    private void loadReviewsList() {
        if (isNetworkConnected()) {
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
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
        reviewsRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
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
