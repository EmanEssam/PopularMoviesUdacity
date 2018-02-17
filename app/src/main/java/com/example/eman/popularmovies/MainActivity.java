package com.example.eman.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eman.popularmovies.adapter.MoviesAdapter;
import com.example.eman.popularmovies.model.Movie;
import com.example.eman.popularmovies.network.ApiHelper;
import com.example.eman.popularmovies.network.Constants;
import com.google.android.gms.common.api.Api;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ApiHelper.GetMovies, MoviesAdapter.OnListClickListner {
    private RecyclerView mMovieList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar mPb;
    private List<Movie> mMoviesList = new ArrayList<>();
    private TextView mMoviesEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        hideProgressbar();
        showProgressbar();
        mLayoutManager = new GridLayoutManager(this, 2);
        mMovieList.setHasFixedSize(true);
        mMovieList.setLayoutManager(mLayoutManager);
        ApiHelper.getMostPopularMovies(this);
        ApiHelper.setGetMoviesInterface(this);
    }

    private void hideProgressbar() {
        mPb.setVisibility(View.GONE);
        mMovieList.setVisibility(View.VISIBLE);
    }

    private void showProgressbar() {
        mPb.setVisibility(View.VISIBLE);
        mMovieList.setVisibility(View.GONE);
    }

    private void initView() {
        mMovieList = (RecyclerView) findViewById(R.id.movies_rv);
        mPb = (ProgressBar) findViewById(R.id.movie_pb);
        mMoviesEmptyView = (TextView) findViewById(R.id.empty_view_tv);
    }

    @Override
    public void moviesList(List<Movie> movies, Context context) {
        mMoviesList = movies;
        mAdapter = new MoviesAdapter(this, movies, this);
        mMovieList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mMoviesEmptyView.setVisibility(View.GONE);
        hideProgressbar();
        mMoviesEmptyView.setVisibility(View.GONE);
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
            showProgressbar();
            ApiHelper.getMostPopularMovies(this);

        } else if (item_id == R.id.top_rated_item) {
            showProgressbar();
            ApiHelper.getTopRatedMovies(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
