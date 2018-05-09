package com.example.aaronbrecher.popularmovies.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.models.MovieDbReturnObject;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by aaronbrecher on 5/8/18.
 */

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<List<Movie>> popular;
    private MutableLiveData<List<Movie>> highestRated;
    private List<Movie> favorite;
    private MovieDbService mMovieDbService = MovieDbApiUtils.createService();

    public MutableLiveData<List<Movie>> getPopular() {
        if(popular == null) popular = new MutableLiveData<>();
        return popular;
    }

    public MutableLiveData<List<Movie>> getHighestRated() {
        if(highestRated == null) highestRated = new MutableLiveData<>();
        return highestRated;
    }

    public void queryMovieDb(){
        if(popular == null){
            mMovieDbService.queryMovies(MovieDbApiUtils.POPULAR_ENDPOINT, MovieDbApiUtils.API_KEY).enqueue(new Callback<MovieDbReturnObject>() {
                @Override
                public void onResponse(Call<MovieDbReturnObject> call, Response<MovieDbReturnObject> response) {
                    List<Movie> movies = response.body().getResults();
                    if (movies != null && movies.size() > 0) {
                        popular.postValue(movies);
                    }
                }

                @Override
                public void onFailure(Call<MovieDbReturnObject> call, Throwable t) {
                    Log.d("MOVIEDPAPI", "onFailure: Error retrieving movies" + t);
                }
            });
        }

        if(highestRated == null){
            mMovieDbService.queryMovies(MovieDbApiUtils.HIGHEST_RATED_ENDPOINT, MovieDbApiUtils.API_KEY).enqueue(new Callback<MovieDbReturnObject>() {
                @Override
                public void onResponse(Call<MovieDbReturnObject> call, Response<MovieDbReturnObject> response) {
                    List<Movie> movies = response.body().getResults();
                    if (movies != null && movies.size() > 0){
                        highestRated.postValue(movies);
                    }
                }

                @Override
                public void onFailure(Call<MovieDbReturnObject> call, Throwable t) {
                    Log.d("MOVIEDPAPI", "onFailure: ERROR");
                }
            });
        }
    }

}
