package com.example.aaronbrecher.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aaronbrecher.popularmovies.R;
import com.example.aaronbrecher.popularmovies.data.MovieContract;
import com.example.aaronbrecher.popularmovies.databinding.MovieListItemBinding;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.MovieDbService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;



public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {
    private List<Movie> mMovieList;
    private ListItemClickListener mClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Movie movie);
    }

    public MovieListAdapter(List<Movie> movies, ListItemClickListener clickListener, Cursor movieCursor) {
        mMovieList = movies;
        mClickListener = clickListener;
//        if(movieCursor != null){
//            mMovieList = convertCursorToMovieList(movieCursor);
//        }
    }

    public void swapLists(List<Movie> movieList){
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public void swapLists(Cursor cursor){
        //Todo swap the cursors and create a list from the new cursor
        if(cursor != null){
            swapLists(new ArrayList<>());
            convertCursorToMovieList(cursor);
            notifyDataSetChanged();
        }
    }

    /**
     * Being as we need to use both a List and a Cursor(due to the favorite database)
     * the option used is to convert the cursor to a list and use a list as the base
     * for the adapter. this function converts the cursor into a list of movies querying
     * the API for each movie Id stored in the cursor
     * @param movieCursor the cursor to be converted
     */
    //Todo create a List from the cursor and set the MovieList to that
    private void convertCursorToMovieList(Cursor movieCursor) {
        MovieDbService service = MovieDbApiUtils.createService();
        while (movieCursor.moveToNext()){
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
            String id = movieCursor.getString(movieIdIndex);
            service.queryMovieForId(id, MovieDbApiUtils.API_KEY).enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    Movie movie = response.body();
                    mMovieList.add(movie);
                    notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {

                }
            });
        }
    }

    @NonNull
    @Override
    public MovieListAdapter.MovieListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MovieListViewHolder(MovieListItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter.MovieListViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        String imagePath = MovieDbApiUtils.THUMB_IMAGE_BASE_URL + movie.getPosterPath();
        Picasso.get().load(imagePath).fit().placeholder(R.drawable.poster_placeholder).into(holder.binding.movieListPosterIv);
    }

    @Override
    public int getItemCount() {
        if(mMovieList == null) return 0;
        return mMovieList.size();
    }

    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MovieListItemBinding binding;
        public MovieListViewHolder(MovieListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie movie = mMovieList.get(position);
            mClickListener.onListItemClick(movie);
        }
    }
}
