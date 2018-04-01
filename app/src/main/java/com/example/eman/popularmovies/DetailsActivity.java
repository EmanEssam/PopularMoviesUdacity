package com.example.eman.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eman.popularmovies.adapter.ReviewsAdapter;
import com.example.eman.popularmovies.adapter.TrailersAdapter;
import com.example.eman.popularmovies.data.MovieData;
import com.example.eman.popularmovies.data.MoviesContract;
import com.example.eman.popularmovies.model.Movie;
import com.example.eman.popularmovies.model.Review;
import com.example.eman.popularmovies.model.Trailer;
import com.example.eman.popularmovies.network.ApiHelper;
import com.example.eman.popularmovies.network.Constants;

import java.util.ArrayList;
import java.util.List;


public class DetailsActivity extends AppCompatActivity implements ApiHelper.GetTrailers, ApiHelper.GetReviews {
    private Movie currentMovie;
    private TextView mReleaseDate, mPlot,mReviewsTv,mTrailersTv;
    private RatingBar mRatingBar;
    private ImageView mMoviePoster;
    private RecyclerView mTrailers;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mTrailersAdapter;
    private RecyclerView mReviews;
    private RecyclerView.LayoutManager mLayoutManager_reviews;
    private RecyclerView.Adapter mReviewsAdapter;
    private ImageButton mFavoriteBtn;
    private String movieID;
    MovieData offlineMovie;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        currentMovie = getIntent().getExtras().getParcelable(Constants.MOVIE);
        movieID = getIntent().getStringExtra(Constants.MOVIE_ID);
        initViews();
        populateUI();
        ApiHelper.setGetTrailersInterface(this);
        ApiHelper.setGetReviewsInterface(this);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager_reviews = new LinearLayoutManager(this);
        mReviews.setLayoutManager(mLayoutManager_reviews);
        mTrailers.setLayoutManager(mLayoutManager);
        mTrailers.setHasFixedSize(true);


    }

    private void populateUI() {
        if (currentMovie != null) {
            setTitle(currentMovie.getTitle());
            ApiHelper.getTrailers(this, currentMovie.getId() + "");
            ApiHelper.getReviews(this, currentMovie.getId() + "");
            mReleaseDate.setText(currentMovie.getReleaseDate());
            mPlot.setText(currentMovie.getOverview());
            Glide.with(this).load(Constants.IMAGE_PATH_OVER_SIZE + currentMovie.getBackdropPath()).into(mMoviePoster);
            mRatingBar.setRating(Float.valueOf(String.valueOf(currentMovie.getVoteAverage() / 2)));
            if (isMovieFavorite(currentMovie.getId() + "")) {
                mFavoriteBtn.setImageResource(R.drawable.ic_favorite_white_48dp);
            } else {
                mFavoriteBtn.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            }
            mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isMovieFavorite(currentMovie.getId() + "")) {
                        mFavoriteBtn.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                        Log.e("uri", currentMovie.getId().toString());
                        getContentResolver().delete(MoviesContract.buildFlavorsUri(currentMovie.getId()), null, null);
                    } else {
                        mFavoriteBtn.setImageResource(R.drawable.ic_favorite_white_48dp);
                        ContentValues values = new ContentValues();
                        values.put(MoviesContract.MovieEntry._ID, currentMovie.getId());
                        values.put(MoviesContract.MovieEntry.BACKDROP_IMAGE, currentMovie.getBackdropPath());
                        values.put(MoviesContract.MovieEntry.RATE, currentMovie.getVoteAverage());
                        values.put(MoviesContract.MovieEntry.RELEASE_DATE, currentMovie.getReleaseDate());
                        values.put(MoviesContract.MovieEntry.TITLE, currentMovie.getTitle());
                        values.put(MoviesContract.MovieEntry.OVERVIEW, currentMovie.getOverview());
                        values.put(MoviesContract.MovieEntry.IMAGE_PATH, currentMovie.getPosterPath());
                        getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, values);
                    }
                }
            });

        } else {

            Log.e("id", getIntent().getIntExtra(Constants.MOVIE_ID, 1) + "");
            offlineMovie = getMovieById(movieID);
            if (offlineMovie != null) {
                setTitle(offlineMovie.getTitle());
                mReleaseDate.setText(offlineMovie.getReleaseDate());
                mPlot.setText(offlineMovie.getOverview());
                Glide.with(this).load(Constants.IMAGE_PATH_OVER_SIZE + offlineMovie.getBackdrop_path()).into(mMoviePoster);
                mRatingBar.setRating(Float.parseFloat(offlineMovie.getVoteAverage()) / 2);
                mFavoriteBtn.setImageResource(R.drawable.ic_favorite_white_48dp);
                mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isMovieFavorite(offlineMovie.getId())) {
                            mFavoriteBtn.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                            getContentResolver().delete(MoviesContract.buildFlavorsUri(Long.parseLong(offlineMovie.getId())), null, null);

                        } else {

                            mFavoriteBtn.setImageResource(R.drawable.ic_favorite_white_48dp);
                            ContentValues values = new ContentValues();
                            values.put(MoviesContract.MovieEntry._ID, offlineMovie.getId());
                            values.put(MoviesContract.MovieEntry.RATE, offlineMovie.getVoteAverage());
                            values.put(MoviesContract.MovieEntry.RELEASE_DATE, offlineMovie.getReleaseDate());
                            values.put(MoviesContract.MovieEntry.TITLE, offlineMovie.getTitle());
                            values.put(MoviesContract.MovieEntry.OVERVIEW, offlineMovie.getOverview());
                            values.put(MoviesContract.MovieEntry.IMAGE_PATH, offlineMovie.getPosterPath());
                            getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, values);

                        }
                    }
                });
            }
        }
    }

    private void initViews() {
        mReleaseDate = findViewById(R.id.release_date_value);
        mPlot = findViewById(R.id.plot_tv_value);
        mRatingBar = findViewById(R.id.ratingBar2);
        mMoviePoster = findViewById(R.id.movie_poster);
        mTrailers = findViewById(R.id.trailers_rv);
        mReviews = findViewById(R.id.reviews_rv);
        mFavoriteBtn = findViewById(R.id.favoriteBtn);
        mTrailersTv=findViewById(R.id.trailers);
        mReviewsTv=findViewById(R.id.reviews);
    }

    @Override
    public void TrailersList(List<Trailer> trailers, Context context) {

        Log.e("listSize",trailers.size()+"");

        if (trailers.size()==0){
            mTrailersTv.setVisibility(View.GONE);
            mTrailers.setVisibility(View.GONE);
        }else {
            mTrailers.setVisibility(View.VISIBLE);
            mTrailersTv.setVisibility(View.VISIBLE);
            mTrailersAdapter = new TrailersAdapter(this, trailers);
            mTrailers.setAdapter(mTrailersAdapter);
        }

    }

    @Override
    public void onTrailersRequestFailed(String error) {

    }

    @Override
    public void ReviewsList(List<Review> reviews, Context context) {

        Log.e("listSize",reviews.size()+"");
        if (reviews.size()==0){
            mReviewsTv.setVisibility(View.GONE);
            mReviews.setVisibility(View.GONE);
        }else {
            mReviewsTv.setVisibility(View.VISIBLE);
            mReviews.setVisibility(View.VISIBLE);
            mReviewsAdapter = new ReviewsAdapter(this, reviews);
            mReviews.setAdapter(mReviewsAdapter);
        }
    }

    @Override
    public void onReviewsRequestFailed(String error) {

    }

    public Boolean isMovieFavorite(String id) {
        Boolean favorite = false;
        List<MovieData> movieData = getAllMovies();
        for (int i = 0; i < movieData.size(); i++) {
            if ((movieData.get(i).getId()).equals(id)) {
                favorite = true;
                break;
            } else favorite = false;
        }
        return favorite;

    }

    private List<MovieData> getAllMovies() {
        Cursor data = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                getIntent().getStringExtra(Constants.MOVIE_ID),
                null,
                null);
        List<MovieData> movies = new ArrayList<>();
        MovieData movieData;
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    movieData = new MovieData();
                    movieData.setId(data.getString(0));
                    movieData.setTitle(data.getString(1));
                    movieData.setOverview(data.getString(2));
                    movieData.setPosterPath(data.getString(3));
                    movieData.setVoteAverage(data.getString(4));
                    movieData.setReleaseDate(data.getString(5));
                    movieData.setBackdrop_path(data.getString(6));

                    movies.add(movieData);
                } while (data.moveToNext());
            }
        }
        return movies;
    }

    private MovieData getMovieById(String id) {
        Cursor data = getContentResolver().query(MoviesContract.buildFlavorsUri(Long.parseLong(id)),
                null,
                null,
                null,
                null);
        MovieData movieData = null;
        if (data != null) {
            data.moveToFirst();
            movieData = new MovieData();
            movieData.setId(data.getString(0));
            movieData.setTitle(data.getString(1));
            movieData.setOverview(data.getString(2));
            movieData.setPosterPath(data.getString(3));
            movieData.setVoteAverage(data.getString(4));
            movieData.setReleaseDate(data.getString(5));
            movieData.setBackdrop_path(data.getString(6));
        }
        return movieData;
    }
}
