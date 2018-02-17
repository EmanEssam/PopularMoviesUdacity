package com.example.eman.popularmovies.network;

import android.content.Context;

import com.example.eman.popularmovies.model.Movie;
import com.example.eman.popularmovies.model.MovieResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Eman on 2/17/2018.
 */

public class ApiHelper {
    public static GetMovies getMovies;

    public static void getTopRatedMovies(final Context context) {
        Retrofit retrofit = new Retrofit.Builder()        // Retrofit : library to make a network request to get the data from the api
                .baseUrl(Constants.BASE_URL)                       // Retrofit initialization
                .addConverterFactory(GsonConverterFactory.create()) // Retrofit initialization
                .build(); // Retrofit initialization

        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<MovieResponse> movies = service.getTopRatedMovies(Constants.API_KEY);

        movies.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) { // onresponse
                getMovies.moviesList(response.body().getResults(), context);
            }


            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                getMovies.onMovieRequestFailed(t.getMessage());
            }
        });
    }

    public static void getMostPopularMovies(final Context context) {
        Retrofit retrofit = new Retrofit.Builder()        // Retrofit : library to make a network request to get the data from the api
                .baseUrl(Constants.BASE_URL)                       // Retrofit initialization
                .addConverterFactory(GsonConverterFactory.create()) // Retrofit initialization
                .build(); // Retrofit initialization

        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<MovieResponse> movies = service.getMostPopularMovies(Constants.API_KEY);

        movies.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) { // onresponse
                getMovies.moviesList(response.body().getResults(), context);
            }


            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                getMovies.onMovieRequestFailed(t.getMessage());
            }
        });
    }


    public interface GetMovies {
        void moviesList(List<Movie> movies, Context context);
        void onMovieRequestFailed(String error);
    }

    public static void setGetMoviesInterface(GetMovies Movies) {
        getMovies = Movies;
    }


}

