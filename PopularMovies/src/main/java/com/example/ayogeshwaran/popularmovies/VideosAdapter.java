package com.example.ayogeshwaran.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ayogeshwaran on 14/01/18.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {
    private List<String> mVideoUrls;

    private Context mContext;

    private IVideosAdapterClickHandler mVideosAdapterClickHandler;

    public VideosAdapter(Context context, IVideosAdapterClickHandler videosAdapterClickHandler) {
        mContext = context;
        mVideosAdapterClickHandler = videosAdapterClickHandler;

    }

    @Override
    public VideosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.video_list_item, parent, false);
        return new VideosAdapter.VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosAdapterViewHolder holder, int position) {
        String videoURL = null;

        if (mVideoUrls != null) {
            videoURL = mVideoUrls.get(position);
            Picasso.with(mContext).load
                    (videoURL).into(holder.imageThumbnailView);
        }
    }

    @Override
    public int getItemCount() {
        if (mVideoUrls != null && mVideoUrls.size() != 0) {
            return mVideoUrls.size();
        } else {
            return 0;
        }
    }

    public void setVideosData(List<String> videos) {
        mVideoUrls = videos;
        notifyDataSetChanged();
    }

    public interface IVideosAdapterClickHandler {
        void onClick(int adapterPosition);
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageThumbnailView;
        final ImageView playButton;

        public VideosAdapterViewHolder(View view) {
            super(view);

            imageThumbnailView = (ImageView) view.findViewById(R.id.video_thumbnail_imageview);
            playButton = (ImageButton) view.findViewById(R.id.btnVideo_player);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            mVideosAdapterClickHandler.onClick(adapterPosition);
        }
    }
}
