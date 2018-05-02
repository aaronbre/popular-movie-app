package com.example.aaronbrecher.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.example.aaronbrecher.popularmovies.adapters.TrailerListAdapter;
import com.example.aaronbrecher.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.models.Trailer;
import com.example.aaronbrecher.popularmovies.models.TrailersReturnObject;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements TrailerListAdapter.ListItemClickListener {
    private Movie mMovie;
    private Toolbar mToolbar;
    private ActivityMovieDetailBinding mBinding;
    private MovieDbService movieDbService;
    private TrailerListAdapter mTrailerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieDbService = MovieDbApiUtils.createService();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        mToolbar = mBinding.toolbar;

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            mMovie = intent.getParcelableExtra("movie");
            setTitle(mMovie.getTitle());
            setUpTrailerRecyclerView();
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

    private void setUpTrailerRecyclerView() {
        mBinding.movieDetailsTrailersRv.setLayoutManager
                (new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerListAdapter = new TrailerListAdapter(null, this);
    }

    private void queryTrailers(Integer id) {
        movieDbService.queryTrailers(String.valueOf(id), BuildConfig.MOVIE_DB_API_KEY)
                .enqueue(new Callback<TrailersReturnObject>() {
                    @Override
                    public void onResponse(Call<TrailersReturnObject> call, Response<TrailersReturnObject> response) {

                    }

                    @Override
                    public void onFailure(Call<TrailersReturnObject> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onListItemClick(Trailer trailer) {

    }
}
