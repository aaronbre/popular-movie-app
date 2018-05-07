package com.example.aaronbrecher.popularmovies;

import android.app.LoaderManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.aaronbrecher.popularmovies.adapters.ReviewListAdapter;
import com.example.aaronbrecher.popularmovies.adapters.TrailerListAdapter;
import com.example.aaronbrecher.popularmovies.data.MovieContract;
import com.example.aaronbrecher.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.models.Trailer;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;
import com.example.aaronbrecher.popularmovies.viewModels.MovieDetailViewModel;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity implements TrailerListAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private Movie mMovie;
    private ActivityMovieDetailBinding mBinding;
    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;
    private MovieDetailViewModel mViewModel;
    private boolean isFavorite;

    public static final int LOADER_ID = 2;

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
            setUpUi();
        }

        mBinding.favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorite){
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, String.valueOf(mMovie.getId()));
                    Uri uri = getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);
                    if(uri != null){
                        Toast.makeText(MovieDetailActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                        isFavorite = true;
                        toggleFabDesign();
                    }
                }
                else {
                    //todo delete from favorites
                    Uri uri = MovieContract.FavoriteEntry.CONTENT_URI.buildUpon()
                            .appendPath(String.valueOf(mMovie.getId())).build();
                    int deleted = getContentResolver().delete(uri,null, null);
                    if(deleted != 0){
                        Toast.makeText(MovieDetailActivity.this, "Deleted movie at position" + deleted, Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                        toggleFabDesign();
                    }

                }
            }
        });
    }

    private void setUpUi() {
        populateUiWithMovieDetails();
        setUpTrailerRecyclerView();
        setUpReviewsRecyclerView();
        setUpFavorite();
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

    private void setUpFavorite() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            private Cursor data;
            @Override
            protected void onStartLoading() {
                if(data != null) deliverResult(data);
                else forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    Uri uri = MovieContract.FavoriteEntry.CONTENT_URI.buildUpon()
                            .appendPath(String.valueOf(mMovie.getId())).build();
                    return getContentResolver().query(uri,
                            null,
                            null,
                            null,
                             MovieContract.FavoriteEntry._ID);
                }catch (Exception e){
                    Log.e(TAG, "loadInBackground: Failed to load async data", e);
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                this.data = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount()<1){
            //todo fix this to check if cursor is empty
            isFavorite = false;
            toggleFabDesign();
        }else{
            isFavorite = true;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void toggleFabDesign() {
        if(!isFavorite){
            mBinding.favoriteFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            mBinding.favoriteFab.setImageResource(R.drawable.ic_favorite_accent_24dp);
        }else{
            mBinding.favoriteFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            mBinding.favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
    }
}
