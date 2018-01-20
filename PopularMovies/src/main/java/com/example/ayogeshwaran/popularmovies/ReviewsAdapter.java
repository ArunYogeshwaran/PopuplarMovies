package com.example.ayogeshwaran.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ayogeshwaran on 14/01/18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private Context mContext;

    private List<String> mReviews;

    public ReviewsAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        String reviewText = null;

        if (mReviews != null) {
            reviewText = mReviews.get(position);
            holder.reviewsText.setText(reviewText);
        }
    }

    @Override
    public int getItemCount() {
        if (mReviews != null) {
            return  mReviews.size();
        } else {
            return 0;
        }
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView reviewsText;

        ReviewsAdapterViewHolder(View view) {
            super(view);

            reviewsText = (TextView) view.findViewById(R.id.text_cardview);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setReviewsData(List<String> reviews) {
        if (mReviews != null) {
            mReviews = null;
        }
        mReviews = reviews;
        notifyDataSetChanged();
    }
}
