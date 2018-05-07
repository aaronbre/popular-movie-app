package com.example.aaronbrecher.popularmovies.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.models.Review;
import com.example.aaronbrecher.popularmovies.models.ReviewsReturnObject;
import com.example.aaronbrecher.popularmovies.models.Trailer;
import com.example.aaronbrecher.popularmovies.models.TrailersReturnObject;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;
import com.example.aaronbrecher.popularmovies.network.YouTubeApiUtils;
import com.example.aaronbrecher.popularmovies.network.YouTubeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by aaronbrecher on 5/2/18.
 */

public class MovieDetailViewModel extends ViewModel {
    private MovieDbService movieDbService = MovieDbApiUtils.createService();
    private YouTubeService youTubeService = YouTubeApiUtils.getService();

    private MutableLiveData<List<Trailer>> mTrailers;
    private MutableLiveData<List<Review>> mReviews;
    private MutableLiveData<Movie> mMovie;

    public MutableLiveData<List<Trailer>> getTrailers() {
        if(mTrailers == null) mTrailers = new MutableLiveData<>();
        return mTrailers;
    }

    public MutableLiveData<List<Review>> getReviews(){
        if(mReviews == null)  mReviews = new MutableLiveData<>();
        return mReviews;
    }

    public MutableLiveData<Movie> getMovie(){
        if(mMovie == null) mMovie = new MutableLiveData<>();
        return mMovie;
    }

    public void queryMovieForId(String id){
        movieDbService.queryMovieForId(id, MovieDbApiUtils.API_KEY).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                if(movie != null) mMovie.postValue(movie);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d(TAG, "onFailure: Failure retrieving movie " + t);
            }
        });
    }

    public void queryTrailerListForId(String id){
        if (movieDbService == null) movieDbService = MovieDbApiUtils.createService();
        movieDbService.queryTrailers(id, MovieDbApiUtils.API_KEY).enqueue(new Callback<TrailersReturnObject>() {
            @Override
            public void onResponse(Call<TrailersReturnObject> call, Response<TrailersReturnObject> response) {
                List<Trailer> trailers = response.body().getResults();
                if(trailers != null){
                    mTrailers.postValue(trailers);
                }
            }

            @Override
            public void onFailure(Call<TrailersReturnObject> call, Throwable t) {
                Log.d(TAG, "onFailure: Error Retrieving Trailers from MovieDB " + t);
            }
        });
    }

    public void queryReviewListForId(String id){
        if(movieDbService == null) movieDbService = MovieDbApiUtils.createService();
        movieDbService.queryReviews(id, MovieDbApiUtils.API_KEY).enqueue(new Callback<ReviewsReturnObject>() {
            @Override
            public void onResponse(Call<ReviewsReturnObject> call, Response<ReviewsReturnObject> response) {
                List<Review> reviews = response.body().getResults();
                if(reviews != null){
                    mReviews.postValue(reviews);
                }
            }

            @Override
            public void onFailure(Call<ReviewsReturnObject> call, Throwable t) {
                Log.d(TAG, "onFailure: Error retrieving reviews from MovieDB " + t);
            }
        });
    }
}
