package com.semurtha.booked;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    Context context;

    public FeedAdapter(Context context, ArrayList<Review> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    ArrayList<Review> reviewArrayList;

    interface Listener {
        void onClick(int position);
    }

    private Listener listener;

    void setListener(Listener listener) {
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView layout;
        TextView mReviewTitle, mReviewContent, mReviewedBy;
        ImageView mBookImage;
        RatingBar mRatingBar;

        public MyViewHolder(@NonNull CardView itemView) {
            super(itemView);
            layout = itemView;
            mReviewTitle = layout.findViewById(R.id.card_review_title);
            mReviewContent = layout.findViewById(R.id.card_review_content);
            mReviewedBy = layout.findViewById(R.id.card_reviewer);
            mBookImage = layout.findViewById(R.id.card_book_image);
            mRatingBar = layout.findViewById(R.id.card_rating);
        }
    }

    @NonNull
    @Override
    public FeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(context)
                .inflate(R.layout.user_feed_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.MyViewHolder holder, int position) {

        Review review = reviewArrayList.get(position);
        // position 0 == first item, 1 == second item etc.
        holder.mReviewTitle.setText(review.getReviewTitle());
        holder.mRatingBar.setRating(review.getRating());
        holder.mReviewedBy.setText(review.getReviewedByName());
        holder.mReviewContent.setText(review.getReviewContent());
        Picasso.with(context).load(Uri.parse(review.getCoverURL()))
                .error(R.drawable.ic_nocover).into(holder.mBookImage);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(holder.getAbsoluteAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }
}
