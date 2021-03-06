package com.example.eman.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.eman.popularmovies.R;
import com.example.eman.popularmovies.data.MovieData;
import com.example.eman.popularmovies.network.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eman on 2/24/2018.
 */

public class MovieDataAdapter extends RecyclerView.Adapter<MovieDataAdapter.MoviesViewHolder> {
    private Context mContext;
    private List<MovieData> mMoviesList = new ArrayList<>();
    private OnListClickListenerOffline onListClickListener;

    public MovieDataAdapter() {
    }

    public MovieDataAdapter(Context mContext, List<MovieData> mMoviesList, OnListClickListenerOffline onListClickListner) {
        this.mContext = mContext;
        this.mMoviesList = mMoviesList;
        this.onListClickListener = onListClickListner;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_row, parent, false);
        return new MoviesViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        Glide.with(mContext).load(Constants.IMAGE_PATH + mMoviesList.get(position).getPosterPath()).into(holder.movie_poster);

    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder {
        public ImageView movie_poster;

        public MoviesViewHolder(final View itemView) {
            super(itemView);
            movie_poster = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onListClickListener.onListItemClickedOffline(getAdapterPosition());
                }
            });
        }
    }

    public interface OnListClickListenerOffline {
        void onListItemClickedOffline(int ClickedItemPosition);
    }
}
