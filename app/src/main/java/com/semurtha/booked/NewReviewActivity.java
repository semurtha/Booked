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

public class NewReviewActivity extends AppCompatActivity {

    private static final String TAG = "AddReview";

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseUser user;

//    private EditText mDateReviewed;
    private EditText mBookTitle, mReviewTitle, mReviewContent;
    private RatingBar mRatingBar;
    private CheckBox mFavoritesCheckBox;
    private Button mSubmitButton;
    private Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pointers
        mBookTitle = findViewById(R.id.add_book_title);
//        mDateReviewed = findViewById(R.id.add_date_reviewed);
        mReviewTitle = findViewById(R.id.add_review_title);
        mReviewContent = findViewById(R.id.add_review_content);
        mRatingBar = findViewById(R.id.add_rating_bar);
        mFavoritesCheckBox = findViewById(R.id.add_favorites_box);
        mSubmitButton = findViewById(R.id.add_review_submit_button);
        // Add mask to date field
//        new DateInputMask(mDateReviewed);
        //mSubmitButton.setOnClickListener(v -> createReview());
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
                Toast.makeText(NewReviewActivity.this, "Your rating must be higher than zero", Toast.LENGTH_LONG).show();
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
                    new Date()
            );

            Toast.makeText(NewReviewActivity.this, "Adding new review...", Toast.LENGTH_LONG).show();
            mDb.collection(UserFeedActivity.REVIEWS)
                    .add(review)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "Review created with ID: " + task.getResult().getId());
                                Toast.makeText(NewReviewActivity.this, "Successfully created new review!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(NewReviewActivity.this, UserFeedActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Exception e = task.getException();
                                Log.w(TAG, "Error adding review: ", e);
                                Toast.makeText(NewReviewActivity.this, "Error adding review: " + e, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            // TODO : Fix handling of a failed upload. The above and below versions do not display the error toast.
//            Toast.makeText(NewReviewActivity.this, "Adding new review...", Toast.LENGTH_LONG).show();
//            mDb.collection(UserFeedActivity.REVIEWS)
//                    .add(review)
//                    .addOnSuccessListener(documentReference -> {
//                        Log.d(TAG, "Review created with ID: " + documentReference.getId());
//                        Toast.makeText(NewReviewActivity.this, "Successfully created new review!", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(NewReviewActivity.this, UserFeedActivity.class);
//                        startActivity(intent);
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.w(TAG, "Error adding review: ", e);
//                        Toast.makeText(NewReviewActivity.this, "Error adding review: " + e, Toast.LENGTH_LONG).show();
//                    });

        }); // end of mSubmitButton OnClickListener
    } // end of OnCreate
} // end of Activity