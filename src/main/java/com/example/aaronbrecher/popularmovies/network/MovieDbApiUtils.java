package com.example.aaronbrecher.popularmovies.network;

import com.example.aaronbrecher.popularmovies.BuildConfig;

/**
 * Utility Class containing all data for querying the MovieDb API
 */

public class MovieDbApiUtils {

    public static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY; //TODO add api key here
    private static final String BASE_URL = "http://api.themoviedb.org/";
    static final String MOVIE_PATH = "3/movie";
    static final String API_KEY_PARAM_NAME = "api_key";
    static final String SORT_BY = "sort_by";
    public static final String POPULAR_ENDPOINT = "popular";
    public static final String HIGHEST_RATED_ENDPOINT = "top_rated";
    public static final String THUMB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String LARGE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    public static MovieDbService createService(){
        return RetrofitClient.getClient(BASE_URL).create(MovieDbService.class);
    }
}
