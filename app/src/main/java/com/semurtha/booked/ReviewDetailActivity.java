package com.semurtha.booked;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewDetailActivity extends AppCompatActivity {

    public static final String TAG = "ReviewDetail";

    // Class variables
    ImageView mBookImage;
    TextView mBookTitle, mBookYear, mReviewerName, mReviewDate, mReviewTitle, mReviewContent;
    RatingBar mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        // Pointers
        mBookImage = findViewById(R.id.detail_book_image);
        mBookTitle = findViewById(R.id.detail_book_title);
        mBookYear = findViewById(R.id.detail_book_year);
        mReviewerName = findViewById(R.id.detail_reviewer_name);
        mReviewDate = findViewById(R.id.detail_review_date);
        mReviewTitle = findViewById(R.id.detail_review_title);
        mReviewContent = findViewById(R.id.detail_review_content);
        mRating = findViewById(R.id.detail_book_rating);

        Review review = (Review) getIntent().getSerializableExtra(UserFeedActivity.CLICKED_REVIEW);
        if (review != null) {
            Log.d(TAG, "User clicked a review");
//            mBookImage.setImageDrawable(); TODO: Add Book API info to get cover image
            mBookTitle.setText(review.getBookTitle());
//            mBookYear.setText("Book Year Goes Here"); TODO: Add Book API info to get year
            mRating.setRating(review.getRating());
//            mReviewerName.setText("Reviewer Name Goes Here"); TODO: Implement Display Name Features
            mReviewDate.setText(review.getReviewDate().toString());
            mReviewTitle.setText(review.getReviewTitle());
            mReviewContent.setText(review.getReviewContent());
        }
    }
}