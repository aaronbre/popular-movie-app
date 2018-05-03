package com.example.aaronbrecher.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aaronbrecher.popularmovies.R;
import com.example.aaronbrecher.popularmovies.databinding.TrailerListItemBinding;
import com.example.aaronbrecher.popularmovies.models.Trailer;
import com.example.aaronbrecher.popularmovies.models.youtubeModels.Default;
import com.example.aaronbrecher.popularmovies.models.youtubeModels.YouTubeTrailerReturnObject;
import com.example.aaronbrecher.popularmovies.network.MovieDbApiUtils;
import com.example.aaronbrecher.popularmovies.network.YouTubeApiUtils;
import com.example.aaronbrecher.popularmovies.network.YouTubeService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by aaronbrecher on 5/1/18.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerListViewHolder> {
    private List<Trailer> mTrailerList;
    private ListItemClickListener mClickListener;
    private YouTubeService youTubeService;

    public interface ListItemClickListener {
        void onListItemClick(String url);
    }

    public TrailerListAdapter(List<Trailer> trailers, ListItemClickListener clickListener) {
        mTrailerList = trailers;
        mClickListener = clickListener;
        youTubeService = YouTubeApiUtils.getService();
    }

    public void swapLists(List<Trailer> movieList) {
        mTrailerList = movieList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrailerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new TrailerListViewHolder(TrailerListItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerListViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);
        //if it is a youtube video get the video thumbnail, otherwise put in placeholder
        if (Objects.equals(trailer.getSite(), MovieDbApiUtils.TRAILER_SITE_YOU_TUBE)) {
            youTubeService.queryTrailer(trailer.getKey()).enqueue(new Callback<YouTubeTrailerReturnObject>() {
                @Override
                public void onResponse(Call<YouTubeTrailerReturnObject> call, Response<YouTubeTrailerReturnObject> response) {
                    String imageUrl = response.body().getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
                    Picasso.get().load(imageUrl).fit().into(holder.binding.trailerItemThumbIv);
                }

                @Override
                public void onFailure(Call<YouTubeTrailerReturnObject> call, Throwable t) {

                }
            });
        } else {
            holder.binding.trailerItemThumbIv.setImageResource(R.drawable.poster_placeholder);
        }
        holder.binding.trailerItemTitleTv.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) return 0;
        return mTrailerList.size();
    }

    public class TrailerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TrailerListItemBinding binding;

        public TrailerListViewHolder(TrailerListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Trailer trailer = mTrailerList.get(position);
            String url = YouTubeApiUtils.YOU_TUBE_INTENT_URL + trailer.getKey();
            mClickListener.onListItemClick(url);
        }
    }
}
