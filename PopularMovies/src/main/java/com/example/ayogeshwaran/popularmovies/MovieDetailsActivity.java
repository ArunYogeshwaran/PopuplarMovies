package com.example.ayogeshwaran.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Created by ayogeshwaran on 11/12/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.title_textview)
    TextView title;

    @BindView(R.id.poster_imageview)
    ImageView poster;

    @BindView(R.id.year_textView)
    TextView year;

    @BindView(R.id.rating_textView)
    TextView rating;

    @BindView(R.id.description_textView)
    TextView description;

    private final int max_rating = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Movies movie = getIntent().getParcelableExtra("parcel_data");
        setValues(movie);
    }

    private void setValues(Movies movie) {
        title.setText(movie.getTitle());

        Picasso.with(getApplicationContext()).load
                (NetworkUtils.getFullPosterUrl(movie.getPosterPath())).into(poster);

        year.setText(movie.getReleaseDate());

        rating.setText(String.valueOf(movie.getVoteAverage()) + "/" + max_rating);

        description.setText(movie.getOverview());
    }
}
