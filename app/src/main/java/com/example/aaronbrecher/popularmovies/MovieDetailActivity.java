package com.example.aaronbrecher.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.aaronbrecher.popularmovies.adapters.ReviewListAdapter;
import com.example.aaronbrecher.popularmovies.adapters.TrailerListAdapter;
import com.example.aaronbrecher.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.models.Trailer;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.viewModels.MovieDetailViewModel;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements TrailerListAdapter.ListItemClickListener {
    private Movie mMovie;
    private ActivityMovieDetailBinding mBinding;
    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;
    private MovieDetailViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        mViewModel = ViewModelProviders.of(this).get(MovieDetailViewModel.class);

        setSupportActionBar(mBinding.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("movie")) {
            mMovie = intent.getParcelableExtra("movie");
            setTitle(mMovie.getTitle());
            populateUiWithMovieDetails();
            setUpTrailerRecyclerView();
            setUpReviewsRecyclerView();
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
        mBinding.movieDetailsTrailersRv.setAdapter(mTrailerListAdapter);
        mViewModel.queryTrailerListForId(String.valueOf(mMovie.getId()));

        mViewModel.getTrailers().observe(this, trailers -> {
            mTrailerListAdapter.swapLists(trailers);
        });
    }

    private void setUpReviewsRecyclerView(){
        mBinding.movieDetailsReviewsRv.setLayoutManager
                (new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mReviewListAdapter = new ReviewListAdapter(null, this);
        mBinding.movieDetailsReviewsRv.setAdapter(mReviewListAdapter);
        mViewModel.queryReviewListForId(String.valueOf(mMovie.getId()));

        mViewModel.getReviews().observe(this, reviews -> {
            mReviewListAdapter.swapLists(reviews);
        });
    }


    @Override
    public void onListItemClick(String url) {
        Log.i("Click","onListItemClick: ");
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
