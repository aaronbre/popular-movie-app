package com.example.aaronbrecher.popularmovies.network;

import com.example.aaronbrecher.popularmovies.models.MovieDbReturnObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by aaronbrecher on 4/25/18.
 */

public interface MovieDbService {

    /**
     * Query the API for a list of Movies.
     * @param endpoint endpoint param will be either popular or highest Rated
     * @return the Return object containing a list of all the movies
     */
    @GET(MovieDbApiUtils.MOVIE_PATH + "/{endpoint}")
    Call<MovieDbReturnObject>QueryMovies(@Path("endpoint") String endpoint,
                                         @Query(MovieDbApiUtils.API_KEY_PARAM_NAME) String apiKey);
}
