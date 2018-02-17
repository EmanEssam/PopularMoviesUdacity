package com.example.eman.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eman.popularmovies.model.Movie;
import com.example.eman.popularmovies.network.Constants;

public class DetailsActivity extends AppCompatActivity {
    private Movie currentMovie;
    private TextView mReleaseDate, mPlot;
    private RatingBar mRatingBar;
    private ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        currentMovie = getIntent().getExtras().getParcelable(Constants.MOVIE);
        initViews();
        populateUI();
    }

    private void populateUI() {
        if (currentMovie!=null){
            setTitle(currentMovie.getTitle());
            mReleaseDate.setText(currentMovie.getReleaseDate());
            mPlot.setText(currentMovie.getOverview());
            Glide.with(this).load(Constants.IMAGE_PATH_OVER_SIZE + currentMovie.getBackdropPath()).into(mMoviePoster);
            mRatingBar.setRating(Float.valueOf(String.valueOf(currentMovie.getVoteAverage() / 2)));

        }
    }

    private void initViews() {
        mReleaseDate = (TextView) findViewById(R.id.release_date_value);
        mPlot = (TextView) findViewById(R.id.plot_tv_value);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        mMoviePoster=(ImageView)findViewById(R.id.movie_poster);
    }
}
