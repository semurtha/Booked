package com.semurtha.booked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class EditReviewActivity extends AppCompatActivity {

    private static final String TAG = "EditReview";

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    //    private EditText mDateReviewed;
    private EditText mBookTitle, mReviewTitle, mReviewContent;
    private RatingBar mRatingBar;
    private CheckBox mFavoritesCheckBox;
    private Button mSubmitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pointers
        mBookTitle = findViewById(R.id.edit_book_title);
        mReviewTitle = findViewById(R.id.edit_review_title);
        mReviewContent = findViewById(R.id.edit_review_content);
        mRatingBar = findViewById(R.id.edit_rating_bar);
        mFavoritesCheckBox = findViewById(R.id.edit_favorites_box);

        Review editReview = (Review) getIntent().getSerializableExtra(UserFeedActivity.CLICKED_REVIEW);
        if (editReview != null) {
            mBookTitle.setText(editReview.getBookTitle());
            mReviewTitle.setText(editReview.getReviewTitle());
            mReviewContent.setText(editReview.getReviewContent());
            mRatingBar.setRating(editReview.getRating());
            mFavoritesCheckBox.setChecked(editReview.isFavorited());
        }


        mSubmitButton = findViewById(R.id.edit_review_submit_button);
        mSubmitButton.setOnClickListener(view -> {
            Log.d(TAG, "Clicked submit.");
            String bookTitle = mBookTitle.getText().toString().trim();
            float rating = mRatingBar.getRating();
            boolean favorited = mFavoritesCheckBox.isChecked();
            String reviewTitle = mReviewTitle.getText().toString().trim();
            String reviewContent = mReviewContent.getText().toString().trim();
            user = FirebaseAuth.getInstance().getCurrentUser();

            // New Review Validation
            // Book Title
            if (TextUtils.isEmpty(bookTitle)) {
                mBookTitle.setError("Book Title is required");
                return;
            } else {
                mBookTitle.setError(null);
            }
            // Review Title
            if (TextUtils.isEmpty(reviewTitle)) {
                mReviewTitle.setError("Review Title is required");
                return;
            } else {
                mReviewTitle.setError(null);
            }
            // Review Content
            if (TextUtils.isEmpty(reviewContent)) {
                mReviewContent.setError("Review Content is required");
                return;
            } else {
                mReviewContent.setError(null);
            }
            // Rating
            if (rating <= 0) {
                Toast.makeText(EditReviewActivity.this, "Your rating must be higher than zero", Toast.LENGTH_LONG).show();
                return;
            }

            // If validation passes, create a new review and add it to the database
            Review review = new Review(
                    bookTitle,
                    rating,
                    favorited,
                    reviewTitle,
                    reviewContent,
                    user.getUid(),
                    editReview.getReviewDate()
            );

            Toast.makeText(EditReviewActivity.this, "Saving changes...", Toast.LENGTH_LONG).show();
            mDb.collection(UserFeedActivity.REVIEWS)
                    .document(editReview.getId())
                    .set(review)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "Review updated with ID: " + editReview.getId());
                                Toast.makeText(EditReviewActivity.this, "Successfully updated review!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditReviewActivity.this, UserFeedActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Exception e = task.getException();
                                Log.w(TAG, "Error updating review: ", e);
                                Toast.makeText(EditReviewActivity.this, "Error updating review: " + e, Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        }); // end of mSubmitButton OnClickListener
    } // end of OnCreate
} // end of Activity