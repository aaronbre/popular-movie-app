package com.example.aaronbrecher.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aaronbrecher.popularmovies.databinding.TrailerListItemBinding;
import com.example.aaronbrecher.popularmovies.models.Trailer;

import java.util.List;

/**
 * Created by aaronbrecher on 5/1/18.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerListViewHolder>{
    private List<Trailer> mTrailerList;
    private ListItemClickListener mClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Trailer trailer);
    }

    public TrailerListAdapter(List<Trailer> trailers, ListItemClickListener clickListener) {
        mTrailerList = trailers;
        mClickListener = clickListener;
    }

    public void swapLists(List<Trailer> movieList){
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

    }

    @Override
    public int getItemCount() {
        if(mTrailerList == null) return 0;
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
            mClickListener.onListItemClick(trailer);
        }
    }
}
