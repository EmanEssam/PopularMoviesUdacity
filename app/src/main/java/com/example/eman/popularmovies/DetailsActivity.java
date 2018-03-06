package com.example.eman.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eman.popularmovies.adapter.ReviewsAdapter;
import com.example.eman.popularmovies.adapter.TrailersAdapter;
import com.example.eman.popularmovies.data.DatabaseHelper;
import com.example.eman.popularmovies.data.MovieData;
import com.example.eman.popularmovies.data.MoviesContract;
import com.example.eman.popularmovies.model.Movie;
import com.example.eman.popularmovies.model.Review;
import com.example.eman.popularmovies.model.Trailer;
import com.example.eman.popularmovies.network.ApiHelper;
import com.example.eman.popularmovies.network.Constants;

import java.util.List;

public class DetailsActivity extends AppCompatActivity implements ApiHelper.GetTrailers, ApiHelper.GetReviews {
    private Movie currentMovie;
    private TextView mReleaseDate, mPlot;
    private RatingBar mRatingBar;
    private ImageView mMoviePoster;
    private RecyclerView mTrailers;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mTrailersAdapter;
    private RecyclerView mReviews;
    private RecyclerView.LayoutManager mLayoutManager_reviews;
    private RecyclerView.Adapter mReviewsAdapter;
    private ImageButton mFavoriteBtn;
    private DatabaseHelper mDbHelper = new DatabaseHelper(this);
    private static final String pref_favorite = "favorite";
    private Boolean isFavorite;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private int mMovie_id;
    private String movieID;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        SharedPreferences prefs = getSharedPreferences(pref_favorite, MODE_PRIVATE);
        editor = getSharedPreferences(pref_favorite, MODE_PRIVATE).edit();
        isFavorite = prefs.getBoolean(pref_favorite, false);
        mMovie_id = prefs.getInt("movie_id", 0);
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
        mDbHelper.getAllMovies();


    }

    private void populateUI() {
        if (currentMovie != null) {
            setTitle(currentMovie.getTitle());
//            mMovie_id = currentMovie.getId();
            ApiHelper.getTrailers(this, currentMovie.getId() + "");
            ApiHelper.getReviews(this, currentMovie.getId() + "");
            mReleaseDate.setText(currentMovie.getReleaseDate());
            mPlot.setText(currentMovie.getOverview());
            Glide.with(this).load(Constants.IMAGE_PATH_OVER_SIZE + currentMovie.getBackdropPath()).into(mMoviePoster);
            mRatingBar.setRating(Float.valueOf(String.valueOf(currentMovie.getVoteAverage() / 2)));
            if (isMovieFavorite(currentMovie.getId() + "")) {
                mFavoriteBtn.setImageResource(R.drawable.ic_favorite);
            } else {
                mFavoriteBtn.setImageResource(R.drawable.ic_action_favorite);
            }
            mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isFavorite) {
                        isFavorite = false;
                        mFavoriteBtn.setImageResource(R.drawable.ic_action_favorite);
                        editor.putBoolean(pref_favorite, false);
                        editor.putInt("movie_id", currentMovie.getId());
                        editor.apply();
                        mDbHelper.deleteMovie(currentMovie.getId());
                    } else {
                        isFavorite = true;
                        mFavoriteBtn.setImageResource(R.drawable.ic_favorite);
                        editor.putBoolean(pref_favorite, true);
                        editor.putInt("movie_id", currentMovie.getId());
                        editor.apply();
                        mDbHelper.insertIntoDb(currentMovie.getTitle(), currentMovie.getId() + "", currentMovie.getOverview(), currentMovie.getPosterPath(), currentMovie.getReleaseDate(), currentMovie.getVoteAverage() + "", currentMovie.getBackdropPath(), isFavorite);

                    }
                }
            });

        } else {

//            Log.e("id", getIntent().getIntExtra(Constants.MOVIE_ID, 1) + "");
            final MovieData offlineMovie = mDbHelper.getMovieById(getIntent().getStringExtra(Constants.MOVIE_ID));
            setTitle(offlineMovie.getTitle());
            mReleaseDate.setText(offlineMovie.getReleaseDate());
            mPlot.setText(offlineMovie.getOverview());
            Glide.with(this).load(Constants.IMAGE_PATH_OVER_SIZE + offlineMovie.getBackdrop_path()).into(mMoviePoster);
            mRatingBar.setRating(Float.parseFloat(offlineMovie.getVoteAverage()) / 2);
            if (isMovieFavorite(offlineMovie.getId())) {
                mFavoriteBtn.setImageResource(R.drawable.ic_favorite);
            } else {
                mFavoriteBtn.setImageResource(R.drawable.ic_action_favorite);
            }
            mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isFavorite) {
                        mFavoriteBtn.setImageResource(R.drawable.ic_action_favorite);
                        isFavorite = false;
                        editor.putBoolean(pref_favorite, false);
                        editor.putInt("movie_id", Integer.parseInt(offlineMovie.getId()));
                        editor.apply();
                        mDbHelper.deleteMovie(Integer.parseInt(offlineMovie.getId()));
                    } else {
                        mFavoriteBtn.setImageResource(R.drawable.ic_favorite);
                        isFavorite = true;
                        editor.putBoolean(pref_favorite, true);
                        editor.putInt("movie_id", Integer.parseInt(offlineMovie.getId()));
                        editor.apply();
                        mDbHelper.insertIntoDb(offlineMovie.getTitle(), offlineMovie.getId() + "", offlineMovie.getOverview(), offlineMovie.getPosterPath(), offlineMovie.getReleaseDate(), offlineMovie.getVoteAverage() + "", currentMovie.getBackdropPath(), isFavorite);

                    }
                }
            });
        }
    }

    private void initViews() {
        mReleaseDate = (TextView) findViewById(R.id.release_date_value);
        mPlot = (TextView) findViewById(R.id.plot_tv_value);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar2);
        mMoviePoster = (ImageView) findViewById(R.id.movie_poster);
        mTrailers = findViewById(R.id.trailers_rv);
        mReviews = findViewById(R.id.reviews_rv);
        mFavoriteBtn = findViewById(R.id.favoriteBtn);

    }

    @Override
    public void TrailersList(List<Trailer> trailers, Context context) {
        mTrailersAdapter = new TrailersAdapter(this, trailers);
        mTrailers.setAdapter(mTrailersAdapter);
    }

    @Override
    public void onTrailersRequestFailed(String error) {

    }

    @Override
    public void ReviewsList(List<Review> reviews, Context context) {
        mReviewsAdapter = new ReviewsAdapter(this, reviews);
        mReviews.setAdapter(mReviewsAdapter);
    }

    @Override
    public void onReviewsRequestFailed(String error) {

    }

    public Boolean isMovieFavorite(String id) {
        Boolean favorite = false;
        List<MovieData> movieData = mDbHelper.getAllMovies();
        for (int i = 0; i < movieData.size(); i++) {
            if ((movieData.get(i).getId()).equals(id)) {
                favorite = true;
                break;
            } else favorite = false;
        }
        return favorite;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
}
