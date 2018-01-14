package com.example.ayogeshwaran.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayogeshwaran.popularmovies.data.MoviesContract;
import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.favoriteButton)
    ImageButton favorite;

    @BindView(R.id.scrollView2)
    ScrollView scrollView;

    private final int max_rating = 10;

    private boolean isFavorite = false;

    private Movies movie;

    private static final Uri contentUri = MoviesContract.MoviesEntry.CONTENT_URI;

    private static final String columnMovieID = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        movie = getIntent().getParcelableExtra("parcel_data");
        setValues(movie);
        setFavoriteButtonState();

        favorite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    if(addMovieToDb()) {
                        favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                        isFavorite = true;
                    }
                } else {
                    if(removeFromDb()) {
                        favorite.setImageResource(R.drawable.ic_favorite_border_black_24px);
                        isFavorite = false;
                    }
                }
            }
        });
    }

    private boolean addMovieToDb() {
        ContentValues[] movieDetails = assignContentValues(movie);
        if (movie != null) {
            ContentResolver movieContentResolver = getContentResolver();

            boolean recordExist = checkForEntryInDb();

            int added = 0;
            if (!recordExist) {
                added = movieContentResolver.bulkInsert(contentUri,
                        movieDetails);
            }

            if (added > 0) {
                Toast.makeText(getApplicationContext(), "Favorite added",
                        Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean checkForEntryInDb() {
        Cursor cursor = getContentResolver().query(contentUri,
                new String[]{columnMovieID},
                columnMovieID + " = ?",
                new String[] { String.valueOf(movie.getId()) },
                null);

        return cursor.getCount() > 0;
    }

    private boolean removeFromDb() {
        if (movie != null) {
            int deleted = getContentResolver().delete
                    (contentUri,
                            columnMovieID + " = ? ",
                            new String[]{String.valueOf(movie.getId())});


            if (deleted > 0) {
                Toast.makeText(getApplicationContext(), "Favorite removed",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private ContentValues[] assignContentValues(Movies movie) {
        ContentValues[] movieValues = new ContentValues[1];

        ContentValues movieValue = new ContentValues();
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_ADULT, movie.getAdult());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movie.getPopularity());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_VIDEO, movie.getVideo());
        movieValue.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

        movieValues[0] = movieValue;
        return movieValues;
    }

    private void setFavoriteButtonState() {
        boolean recordExist = checkForEntryInDb();

        if (recordExist) {
            isFavorite = true;
            favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            isFavorite = false;
            favorite.setImageResource(R.drawable.ic_favorite_border_black_24px);
        }
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
