package com.example.ayogeshwaran.popularmovies;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosListActivity extends AppCompatActivity implements
        VideosAdapter.IVideosAdapterClickHandler {

    @BindView(R.id.videosRecyclerview)
    RecyclerView videosRecyclerView;

    @BindView(R.id.videos_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.videos_error_textview)
    TextView mErrorTextView;

    private Movies movie;

    private VideosAdapter mVideosAdapter;

    private List<String> mVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_list);
        ButterKnife.bind(this);

        movie = getIntent().getParcelableExtra("parcel_data");

        initViews();

        this.registerReceiver(mConnReceiver, new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private final BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {
                if (videosRecyclerView.getVisibility() == View.INVISIBLE) {
                    loadVideosList();
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
            Log.i(this.getClass().getSimpleName(),
                    "Exception - unregistering network broadcast receiver");
        }
        super.onDestroy();
    }

    private void initViews() {
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(),1);

        videosRecyclerView.setLayoutManager(gridLayoutManager);

        mVideosAdapter = new VideosAdapter(this, this);

        videosRecyclerView.setAdapter(mVideosAdapter);

        videosRecyclerView.setHasFixedSize(true);

        showLoading();

        loadVideosList();
    }

    private void loadVideosList() {
        if (NetworkUtils.isOnline(this)) {
            new FetchVideosTask().execute(movie.getId());
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            videosRecyclerView.setVisibility(View.INVISIBLE);
            mErrorTextView.setText(getString(R.string.check_network_connection));
            mErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(int adapterPosition) {
        URI uri = null;
        try {
            uri = new URI(mVideos.get(adapterPosition));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String[] segments = new String[0];
        if (uri != null) {
            segments = uri.getPath().split("/");
        }
        String videoID = segments[segments.length - 2];
        navigateToYoutube(videoID);
    }

    private void navigateToYoutube(String videoID) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoID));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchVideosTask extends AsyncTask<Integer, Void, List<String>> {

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

            URL videosUrl = NetworkUtils.getVideosUrl(movieID, getApplicationContext());

            try {
                mVideos = MovieJsonUtils.getVideosListFromURL(videosUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mVideos;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if (mVideos != null && mVideos.size() != 0) {
                showVideosListView();
                mVideosAdapter.setVideosData(mVideos);
            } else {
                mErrorTextView.setVisibility(View.VISIBLE);
                mErrorTextView.setText(getString(R.string.no_videos));
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showLoading() {
        videosRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showVideosListView() {
        videosRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }
}
