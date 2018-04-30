package com.example.aaronbrecher.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aaronbrecher.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    private Movie mMovie;
    private ActivityMovieDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            mMovie = intent.getParcelableExtra("movie");
            setTitle(mMovie.getTitle());
            populateUiWithMovieDetails();
        }
    }

    private void populateUiWithMovieDetails() {
        mBinding.movieDetailsTitleTv.setText(mMovie.getOriginalTitle());
        mBinding.movieDetailsRatingsTv.setText(String.valueOf(mMovie.getVoteAverage()));
        mBinding.movieDetailsReleaseTv.setText(mMovie.getReleaseDate());
        mBinding.movieDetailsPlotTv.setText(mMovie.getOverview());
        Picasso.get().load(MovieDbApiUtils.LARGE_IMAGE_BASE_URL + mMovie.getPosterPath())
                .placeholder(R.drawable.poster_placeholder).fit()
                .into(mBinding.movieDetailsPosterIv);
    }
}
