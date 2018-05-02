package com.example.aaronbrecher.popularmovies.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.aaronbrecher.popularmovies.models.Trailer;
import com.example.aaronbrecher.popularmovies.models.TrailersReturnObject;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by aaronbrecher on 5/2/18.
 */

public class MovieDetailViewModel extends ViewModel {
    private MovieDbService movieDbService = null;

    public void queryTrailerListForId(String id){
        if (movieDbService == null) movieDbService = MovieDbApiUtils.createService();
        movieDbService.queryTrailers(id, MovieDbApiUtils.API_KEY).enqueue(new Callback<TrailersReturnObject>() {
            @Override
            public void onResponse(Call<TrailersReturnObject> call, Response<TrailersReturnObject> response) {
                List<Trailer> trailers = response.body().getResults();

            }

            @Override
            public void onFailure(Call<TrailersReturnObject> call, Throwable t) {

            }
        });
    }
}
