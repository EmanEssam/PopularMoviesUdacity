package com.example.eman.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eman.popularmovies.adapter.MovieDataAdapter;
import com.example.eman.popularmovies.adapter.MoviesAdapter;
import com.example.eman.popularmovies.data.MovieData;
import com.example.eman.popularmovies.data.MoviesContract;
import com.example.eman.popularmovies.model.Movie;
import com.example.eman.popularmovies.network.ApiHelper;
import com.example.eman.popularmovies.network.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ApiHelper.GetMovies, MoviesAdapter.OnListClickListner, MovieDataAdapter.OnListClickListenerOffline,
        LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mMovieList;
    private GridLayoutManager mLayoutManager;

    private RecyclerView.Adapter mAdapter;
    private ProgressBar mPb;
    private List<Movie> mMoviesList = new ArrayList<>();
    private TextView mMoviesEmptyView;
    private List<MovieData> movieDataList;
    private static final int TASK_LOADER_ID = 0;
    private static int sortBy = R.id.most_popular_item;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        showProgressbar();
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList(getString(R.string.Extra_list)) != null) {
                mMoviesList = savedInstanceState.getParcelableArrayList(getString(R.string.Extra_list));
                mAdapter = new MoviesAdapter(this, mMoviesList, this);
                mMovieList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            if (sortBy == R.id.favorite_item) {
                getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
            }

            mMoviesEmptyView.setVisibility(View.GONE);
            hideProgressbar();
        } else {
            ApiHelper.getMostPopularMovies(this);
            ApiHelper.setGetMoviesInterface(this);
        }

        mLayoutManager = new GridLayoutManager(this, 2);
        mMovieList.setHasFixedSize(true);
        mMovieList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMoviesList != null) {
            outState.putParcelableArrayList(getString(R.string.Extra_list), (ArrayList<? extends Parcelable>) mMoviesList);
        }
    }

    private void hideProgressbar() {
        mPb.setVisibility(View.GONE);
        mMovieList.setVisibility(View.VISIBLE);
    }

    private void showProgressbar() {
        mPb.setVisibility(View.VISIBLE);
        mMovieList.setVisibility(View.GONE);
        mMoviesEmptyView.setVisibility(View.GONE);

    }

    private void initView() {
        mMovieList = findViewById(R.id.movies_rv);
        mPb = findViewById(R.id.movie_pb);
        mMoviesEmptyView = findViewById(R.id.empty_view_tv);
    }

    @Override
    public void moviesList(List<Movie> movies, Context context) {
        mMoviesList = movies;
        mAdapter = new MoviesAdapter(this, movies, this);
        mMovieList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mMoviesEmptyView.setVisibility(View.GONE);
        hideProgressbar();
    }

    @Override
    public void onMovieRequestFailed(String error) {
        hideProgressbar();
        mMovieList.setVisibility(View.GONE);
        mMoviesEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClicked(int ClickedItemPosition) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(Constants.MOVIE, mMoviesList.get(ClickedItemPosition));
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.most_popular_item) {
            sortBy = R.id.most_popular_item;
            showProgressbar();
            ApiHelper.getMostPopularMovies(this);

        } else if (item_id == R.id.top_rated_item) {
            sortBy = R.id.top_rated_item;
            showProgressbar();
            ApiHelper.getTopRatedMovies(this);
        } else if (item_id == R.id.favorite_item) {
            sortBy = R.id.favorite_item;
            showProgressbar();
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListItemClickedOffline(int ClickedItemPosition) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(Constants.MOVIE_ID, movieDataList.get(ClickedItemPosition).getId());
        Log.e("id1", movieDataList.get(ClickedItemPosition).getId());
        startActivityForResult(intent, 2);


    }

    private List<MovieData> getAllMovies(Cursor data) {
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

    @SuppressLint("StaticFieldLeak")
    @Override

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mMovieData = null;


            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e("load", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportLoaderManager().destroyLoader(TASK_LOADER_ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (getAllMovies(data).size() == 0 || getAllMovies(data) == null) {
            mMoviesEmptyView.setVisibility(View.VISIBLE);
            mMovieList.setVisibility(View.GONE);
        } else {
            movieDataList = new ArrayList<>();
            movieDataList = getAllMovies(data);
            mAdapter = new MovieDataAdapter(this, getAllMovies(data), this);
            mMovieList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mMovieList.setVisibility(View.VISIBLE);
            movieDataList = getAllMovies(data);

        }
        mPb.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        getAllMovies(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }

    }
}
