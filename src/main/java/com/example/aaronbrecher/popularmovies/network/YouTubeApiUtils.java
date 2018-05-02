package com.example.aaronbrecher.popularmovies.network;

import com.example.aaronbrecher.popularmovies.BuildConfig;

/**
 * Created by aaronbrecher on 5/2/18.
 */

public class YouTubeApiUtils {
    public static final String API_KEY = BuildConfig.YOU_TUBE_API_KEY;
    private static final String BASE_URL = "https://www.googleapis.com/";
    private static final String ENDPOINT = "youtube/v3/videos/";
    private static final String PARAM_KEY = BuildConfig.YOU_TUBE_API_KEY;
    private static final String PARAM_SNIPPET = "part=snippet";
    public static final String PARAM_ID_NAME = "id";
    public static final String PATH = ENDPOINT + "?" + PARAM_KEY + "&" + PARAM_SNIPPET;


    public static YouTubeService getService(){
        return RetrofitClient.getClient(BASE_URL).create(YouTubeService.class);
    }

}