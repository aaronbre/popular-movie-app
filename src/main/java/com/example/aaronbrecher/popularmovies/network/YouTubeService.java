package com.example.aaronbrecher.popularmovies.network;

import com.example.aaronbrecher.popularmovies.models.youtubeModels.YouTubeTrailerReturnObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by aaronbrecher on 5/2/18.
 */

public interface YouTubeService {
    @GET(YouTubeApiUtils.PATH)
    Call<YouTubeTrailerReturnObject> queryTrailer(@Query(YouTubeApiUtils.PARAM_ID_NAME) String id);
}
