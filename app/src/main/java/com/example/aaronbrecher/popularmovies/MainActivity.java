package com.example.aaronbrecher.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
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
import com.example.aaronbrecher.popularmovies.models.MovieDbReturnObject;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        MovieListAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    public static final String POPULAR_MOVIES = "Popular";
    public static final String HIGHEST_RATED_MOVIES = "Highest Rated";
    public static final String FAVORITE_MOVIES = "Favorite";
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int FAVORITE_LOADER_ID = 1;

    RecyclerView mRecyclerView;
    Spinner mSortSpinner;
    String mSortOption = POPULAR_MOVIES;
    List<Movie> mPopularMovies;
    List<Movie> mHighestRatedMovies;
    Cursor mFavoriteMovies;
    MovieDbService mMovieDbService;
    MovieListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //create the MovieDbService to query the API
        mMovieDbService = MovieDbApiUtils.createService();

        mRecyclerView = findViewById(R.id.movie_list_rv);
        mSortSpinner = findViewById(R.id.sort_options_spinner);
        mListAdapter = new MovieListAdapter(null, this, null);
        mRecyclerView.setAdapter(mListAdapter);
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        setUpSpinner();
        queryMovieDbApi();
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
        mSortSpinner.setSelection(0);
        mSortOption = adapter.getItem(0);
        mSortSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //TODO bug when switching sortOption loses the list
        String sortOption = (String) parent.getItemAtPosition(position);
        if (!Objects.equals(sortOption, mSortOption)) {
            if (Objects.equals(sortOption, POPULAR_MOVIES)){
                mListAdapter.swapLists(mPopularMovies);
                mSortOption = POPULAR_MOVIES;
            }
            else if(Objects.equals(sortOption, HIGHEST_RATED_MOVIES)) {
                mSortOption = HIGHEST_RATED_MOVIES;
                mListAdapter.swapLists(mHighestRatedMovies);
            }
            else if(Objects.equals(sortOption, FAVORITE_MOVIES)){
               if(getLoaderManager().getLoader(FAVORITE_LOADER_ID) == null){
                   getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
               }else {
                   getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
               }
               mSortOption = FAVORITE_MOVIES;
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
    private void queryMovieDbApi() {
        mMovieDbService.queryMovies(MovieDbApiUtils.POPULAR_ENDPOINT, MovieDbApiUtils.API_KEY).enqueue(new Callback<MovieDbReturnObject>() {
            @Override
            public void onResponse(Call<MovieDbReturnObject> call, Response<MovieDbReturnObject> response) {
                List<Movie> movies = response.body().getResults();
                Log.i("MovieDBAPI", "onResponse: " + movies.toString());
                if (movies.size() > 0) {
                    mListAdapter.swapLists(movies);
                    mPopularMovies = movies;
                }
            }

            @Override
            public void onFailure(Call<MovieDbReturnObject> call, Throwable t) {
                Log.d("MOVIEDPAPI", "onFailure: Error retrieving movies" + t);
            }
        });

        mMovieDbService.queryMovies(MovieDbApiUtils.HIGHEST_RATED_ENDPOINT, MovieDbApiUtils.API_KEY).enqueue(new Callback<MovieDbReturnObject>() {
            @Override
            public void onResponse(Call<MovieDbReturnObject> call, Response<MovieDbReturnObject> response) {
                List<Movie> movies = response.body().getResults();
                if (movies != null && movies.size() > 0) mHighestRatedMovies = movies;
            }

            @Override
            public void onFailure(Call<MovieDbReturnObject> call, Throwable t) {
                Log.d("MOVIEDPAPI", "onFailure: ERROR");
            }
        });
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
        mListAdapter.swapLists(mFavoriteMovies);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapLists((Cursor) null);
    }
}
