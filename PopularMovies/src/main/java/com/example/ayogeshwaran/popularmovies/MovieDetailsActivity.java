package com.example.ayogeshwaran.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by ayogeshwaran on 11/12/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView title;

    private ImageView poster;

    private TextView year;

    private TextView rating;

    private TextView description;

    private final int max_rating = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        Movies movie = getIntent().getParcelableExtra("parcel_data");
        intiViews();
        setValues(movie);
    }

    private void intiViews() {
        title = (TextView) findViewById(R.id.title_textview);
        poster = (ImageView) findViewById(R.id.poster_imageview);
        year = (TextView) findViewById(R.id.year_textView);
        rating = (TextView) findViewById(R.id.rating_textView);
        Button favorite = (Button) findViewById(R.id.favorite_button);
        description = (TextView) findViewById(R.id.description_textView);
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
