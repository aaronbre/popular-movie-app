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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.aaronbrecher.popularmovies.adapters.ReviewListAdapter;
import com.example.aaronbrecher.popularmovies.adapters.TrailerListAdapter;
import com.example.aaronbrecher.popularmovies.data.MovieContract;
import com.example.aaronbrecher.popularmovies.data.MovieContract.FavoriteEntry;
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

        /**
         * Set up the fab. Will add to the database of favorites. If already a favorite will
         * remove from db
         */
        mBinding.favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorite){
                    Uri uri = getContentResolver().insert(FavoriteEntry.CONTENT_URI, createContentValues());
                    if(uri != null){
                        Toast.makeText(MovieDetailActivity.this, mMovie.getOriginalTitle() + " was added to favorites",
                                Toast.LENGTH_SHORT).show();
                        isFavorite = true;
                        toggleFabDesign();
                    }
                }
                else {
                    Uri uri = FavoriteEntry.CONTENT_URI.buildUpon()
                            .appendPath(String.valueOf(mMovie.getId())).build();
                    int deleted = getContentResolver().delete(uri,null, null);
                    if(deleted != 0){
                        Toast.makeText(MovieDetailActivity.this, "Deleted " + mMovie.getOriginalTitle() + " from your favorites",
                                Toast.LENGTH_SHORT).show();
                        isFavorite = false;
                        toggleFabDesign();
                    }
                }
            }
        });
    }


    private ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_TITLE, mMovie.getOriginalTitle());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_RELEASE, mMovie.getReleaseDate());
        contentValues.put(FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE, mMovie.getVoteAverage());
        return contentValues;
    }

    private void setUpUi() {
        populateUiWithMovieDetails();
        setUpTrailerRecyclerView();
        setUpReviewsRecyclerView();
        setUpFavorite();
    }

    /**
     * Populate all the basic fields - text, image, etc.
     */
    private void populateUiWithMovieDetails() {
        mBinding.movieDetailsTitleTv.setText(mMovie.getOriginalTitle());
        mBinding.movieDetailsRatingsTv.setText(String.valueOf(mMovie.getVoteAverage()));
        mBinding.movieDetailsReleaseTv.setText(mMovie.getReleaseDate());
        mBinding.movieDetailsPlotTv.setText(mMovie.getOverview());
        Picasso.get().load(MovieDbApiUtils.LARGE_IMAGE_BASE_URL + mMovie.getPosterPath())
                .placeholder(R.drawable.poster_placeholder).fit()
                .into(mBinding.movieDetailsPosterIv);
    }

    /**
     * set up the trailer recyclerView, will use LiveData from the ViewModel with a Retrofit
     * query to set up
     */
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

    /**
     * Set up the reviews this as well will use LiveData as well as
     * retrofit
     */
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

    /**
     * launch the trailer on youtube or browser (will also
     * send the user to the full review when clicking the review)
     * @param url - the url for the intent
     */
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

    /**
     * The loader to be used for accessing the database via the
     * content Provider
     */
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
                    Uri uri = FavoriteEntry.CONTENT_URI.buildUpon()
                            .appendPath(String.valueOf(mMovie.getId())).build();
                    return getContentResolver().query(uri,
                            null,
                            null,
                            null,
                             FavoriteEntry._ID);
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

    /**
     * Method to change the design of the fabButton according to it
     * being a favorite or not
     */
    private void toggleFabDesign() {
        if(!isFavorite){
            mBinding.favoriteFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            mBinding.favoriteFab.setImageResource(R.drawable.ic_favorite_accent_24dp);
        }else{
            mBinding.favoriteFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            mBinding.favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
    }

    // overriding back button so as not to recreate activity,
    // doing so will cause the sort option to return to default
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
