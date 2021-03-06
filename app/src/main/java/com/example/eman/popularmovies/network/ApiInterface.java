package com.example.eman.popularmovies.network;

import com.example.eman.popularmovies.model.MovieResponse;
import com.example.eman.popularmovies.model.ReviewResponse;
import com.example.eman.popularmovies.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Eman on 2/17/2018.
 */

public interface ApiInterface {
    @GET("top_rated?")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String api_key);

    @GET("popular?")
    Call<MovieResponse> getMostPopularMovies(@Query("api_key") String api_key);
    @GET("{id}/videos?")
    Call<TrailerResponse> getTrailers(@Path("id") String movie_id, @Query("api_key") String api_key);

    @GET("{id}/reviews?")
    Call<ReviewResponse> getReviews(@Path("id") String movie_id, @Query("api_key") String api_key);
}
