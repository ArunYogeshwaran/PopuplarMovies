package com.example.ayogeshwaran.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ayogeshwaran.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ayogeshwaran on 23/11/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private final Context mContext;

    private List<Movies> movies;

    /*
     * An interface is defined below to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private IMoviesAdapterClickHandler mClickHandler;

    public MoviesAdapter(@NonNull Context context,
                         IMoviesAdapterClickHandler moviesAdpterClickHandler) {
        mContext = context;
        mClickHandler = moviesAdpterClickHandler;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        String posterPath = movies.get(position).getPosterPath();

        posterPath = NetworkUtils.getFullPosterUrl(posterPath);

        Picasso.with(mContext).load
                (posterPath).into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if (movies == null) {
            return 0;
        } else {
            return movies.size();
        }
    }


    public interface IMoviesAdapterClickHandler {
        void onClick(int adapterPosition);
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView moviePoster;

        MoviesAdapterViewHolder(View view) {
            super(view);

            moviePoster = (ImageView) view.findViewById(R.id.movie_poster_image_view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.onClick(adapterPosition);
        }
    }

    public void setMoviesData(List<Movies> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
