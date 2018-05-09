package com.example.aaronbrecher.popularmovies;

import android.app.LoaderManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.aaronbrecher.popularmovies.adapters.MovieListAdapter;
import com.example.aaronbrecher.popularmovies.data.MovieContract;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.network.NetworkUtils;
import com.example.aaronbrecher.popularmovies.viewModels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        MovieListAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    public static final String POPULAR_MOVIES = "Popular";
    public static final String HIGHEST_RATED_MOVIES = "Highest Rated";
    public static final String FAVORITE_MOVIES = "Favorite";
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int FAVORITE_LOADER_ID = 1;
    private static final String SORT_OPTION = "sortOption";

    RecyclerView mRecyclerView;
    Spinner mSortSpinner;
    String mSortOption;
    Cursor mFavoriteMovies;
    MovieListAdapter mListAdapter;
    MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(SORT_OPTION))
            mSortOption = savedInstanceState.getString(SORT_OPTION);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mRecyclerView = findViewById(R.id.movie_list_rv);
        mSortSpinner = findViewById(R.id.sort_options_spinner);
        mListAdapter = new MovieListAdapter(null, this, null);
        mRecyclerView.setAdapter(mListAdapter);
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        setUpSpinner();
        retrieveData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_OPTION, mSortOption);
    }

    /**
     * Set up the spinner with two options popular or highest rated
     * initialize the spinner to popular setting it to member variable will
     * use when quering the API to initialize
     */
    private void setUpSpinner() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList(POPULAR_MOVIES, HIGHEST_RATED_MOVIES, FAVORITE_MOVIES));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.sort_spinner_item, options);
        mSortSpinner.setAdapter(adapter);
        int position;
        if(mSortOption == null) position = 0;
        else position = adapter.getPosition(mSortOption);
        mSortSpinner.setSelection(position);
        mSortOption = adapter.getItem(position);
        mSortSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sortOption = (String) parent.getItemAtPosition(position);
        if (!Objects.equals(sortOption, mSortOption)) {
            if (Objects.equals(sortOption, POPULAR_MOVIES)){
                mListAdapter.swapLists(viewModel.getPopular().getValue());
                mSortOption = POPULAR_MOVIES;
            }
            else if(Objects.equals(sortOption, HIGHEST_RATED_MOVIES)) {
                mSortOption = HIGHEST_RATED_MOVIES;
                mListAdapter.swapLists(viewModel.getHighestRated().getValue());
            }
            else if(Objects.equals(sortOption, FAVORITE_MOVIES)){
                mSortOption = FAVORITE_MOVIES;
                if(getLoaderManager().getLoader(FAVORITE_LOADER_ID) == null){
                   getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
               }else {
                   getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
               }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Query both options (popular and highest rated) to cache the results so
     * do not need to query a second time on swithching sort_by
     */
    private void retrieveData() {
        viewModel.queryMovieDb();
        viewModel.getHighestRated().observe(this,movies -> {
            if(Objects.equals(mSortOption, HIGHEST_RATED_MOVIES)) mListAdapter.swapLists(movies);
        });
        viewModel.getPopular().observe(this, movies -> {
            if(Objects.equals(mSortOption, POPULAR_MOVIES)) mListAdapter.swapLists(movies);
        });
        getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(this) {
                Cursor mData;
                @Override
                protected void onStartLoading() {
                    if(mData != null) deliverResult(mData);
                    else forceLoad();
                }

                @Override
                public Cursor loadInBackground() {
                    try{
                        return getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                MovieContract.FavoriteEntry._ID);
                    }catch (Exception e){
                        Log.e(TAG, "loadInBackground: Failed to load favorites", e);
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {
                    mData = data;
                    super.deliverResult(data);
                }
            };
        }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteMovies = data;
        if(Objects.equals(mSortOption, FAVORITE_MOVIES)) mListAdapter.swapLists(mFavoriteMovies);
        else if(!NetworkUtils.hasNetworkConnection(this)){
            mSortOption = FAVORITE_MOVIES;
            int postion = ((ArrayAdapter) mSortSpinner.getAdapter()).getPosition(mSortOption);
            mSortSpinner.setSelection(postion);
            mListAdapter.swapLists(mFavoriteMovies);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapLists((Cursor) null);
    }


    // if we are resuming the activity it is possible the favorites will be out of sync
    // for ex. if a movie was removed from favorites and we are returning to the
    // favorites list
    @Override
    protected void onResume() {
        super.onResume();
        if(Objects.equals(mSortOption, FAVORITE_MOVIES)) getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
    }
}
