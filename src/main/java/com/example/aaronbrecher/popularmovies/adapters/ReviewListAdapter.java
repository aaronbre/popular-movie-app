package com.example.aaronbrecher.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aaronbrecher.popularmovies.databinding.ReviewListItemBinding;
import com.example.aaronbrecher.popularmovies.databinding.TrailerListItemBinding;
import com.example.aaronbrecher.popularmovies.models.Review;
import com.example.aaronbrecher.popularmovies.models.Trailer;

import java.util.List;

/**
 * Created by aaronbrecher on 5/1/18.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder>{
    private List<Review> mReviewList;
    private TrailerListAdapter.ListItemClickListener mClickListener;


    public ReviewListAdapter(List<Review> reviews) {
        mReviewList = reviews;
    }

    public void swapLists(List<Review> movieList, TrailerListAdapter.ListItemClickListener clickListener){
        mReviewList = movieList;
        mClickListener = clickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ReviewListViewHolder(ReviewListItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListViewHolder holder, int position) {
        Review review = mReviewList.get(position);
        holder.binding.reviewAuthor.setText(review.getAuthor());
        holder.binding.reviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(mReviewList == null) return 0;
        return mReviewList.size();
    }

    public class ReviewListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ReviewListItemBinding binding;

        public ReviewListViewHolder(ReviewListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Review review = mReviewList.get(position);
            mClickListener.onListItemClick(review.getUrl());
        }
    }
}