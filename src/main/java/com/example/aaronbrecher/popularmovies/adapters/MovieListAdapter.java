package com.example.aaronbrecher.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aaronbrecher.popularmovies.R;
import com.example.aaronbrecher.popularmovies.databinding.MovieListItemBinding;
import com.example.aaronbrecher.popularmovies.models.Movie;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;



public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {
    private List<Movie> mMovieList;
    private ListItemClickListener mClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Movie movie);
    }

    public MovieListAdapter(List<Movie> movies, ListItemClickListener clickListener) {
        mMovieList = movies;
        mClickListener = clickListener;
    }

    public void swapLists(List<Movie> movieList){
        mMovieList = movieList;
        notifyDataSetChanged();
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
