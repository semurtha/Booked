package com.semurtha.booked;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
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
    private EditText mBookTitle, mReviewTitle, mReviewContent;
    private RatingBar mRatingBar;
    private CheckBox mFavoritesCheckBox;
    private Button mSubmitButton;
    private String pubYear, coverURL;
    private Review editReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.edit_review_title);

        // Get review from passed intent
        editReview = (Review) getIntent().getSerializableExtra(UserFeedActivity.CLICKED_REVIEW);

        // Pointers
        mBookTitle = findViewById(R.id.edit_book_title);
        mReviewTitle = findViewById(R.id.edit_review_title);
        mReviewContent = findViewById(R.id.edit_review_content);
        mRatingBar = findViewById(R.id.edit_rating_bar);
        mFavoritesCheckBox = findViewById(R.id.edit_favorites_box);
        mSubmitButton = findViewById(R.id.edit_review_submit_button);

        ActivityResultLauncher<Intent> bookResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Book book = (Book) data.getSerializableExtra(NewReviewActivity.RESULT);
                            Log.d(TAG, "openLibraryId: " + book.getOpenLibraryId());
                            Log.d(TAG, "title: " + book.getTitle());
                            Log.d(TAG, "author: " + book.getAuthor());
                            Log.d(TAG, "pubYear: " + book.getPubYear());
                            Log.d(TAG, "coverURL: " + book.getCoverUrl());
                            // get book object, set title text
                            pubYear = book.getPubYear();
                            coverURL = book.getCoverUrl();
                            mBookTitle.setText(book.getTitle());
                        }
                    }
                });

        if (editReview != null) {
            mBookTitle.setText(editReview.getBookTitle());
            mReviewTitle.setText(editReview.getReviewTitle());
            mReviewContent.setText(editReview.getReviewContent());
            mRatingBar.setRating(editReview.getRating());
            pubYear = editReview.getPublished();
            coverURL = editReview.getCoverURL();
            mFavoritesCheckBox.setChecked(editReview.isFavorited());
        }

        mBookTitle.setOnClickListener(view -> {
            Intent intent = new Intent(EditReviewActivity.this, BookListActivity.class);
            bookResultLauncher.launch(intent);
        });

        mSubmitButton.setOnClickListener(view -> {
            Log.d(TAG, "Clicked submit.");
            String bookTitle = mBookTitle.getText().toString().trim();
            float rating = mRatingBar.getRating();
            boolean favorited = mFavoritesCheckBox.isChecked();
            String reviewTitle = mReviewTitle.getText().toString().trim();
            String reviewContent = mReviewContent.getText().toString().trim();
            user = FirebaseAuth.getInstance().getCurrentUser();

            // Edit Review Validation
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
                    pubYear,
                    coverURL,
                    rating,
                    favorited,
                    reviewTitle,
                    reviewContent,
                    user.getUid(),
                    user.getDisplayName(),
                    editReview.getReviewDate()
            );

            Toast addToast = Toast.makeText(EditReviewActivity.this, "Saving changes...", Toast.LENGTH_SHORT);
            addToast.show();
            mDb.collection(UserFeedActivity.REVIEWS)
                    .document(editReview.getId())
                    .set(review)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                addToast.cancel();
                                Log.d(TAG, "Review updated with ID: " + editReview.getId());
                                Toast.makeText(EditReviewActivity.this, "Successfully updated review!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditReviewActivity.this, UserFeedActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Exception e = task.getException();
                                Log.w(TAG, "Error updating review: ", e);
                                addToast.cancel();
                                Toast.makeText(EditReviewActivity.this, "Error updating review: " + e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }); // end of mSubmitButton OnClickListener
    } // end of OnCreate
} // end of Activity