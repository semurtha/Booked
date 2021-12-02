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

public class NewReviewActivity extends AppCompatActivity {

    private static final String TAG = "NewReview";
    public static final String RESULT = "result";

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private EditText mBookTitle, mReviewTitle, mReviewContent;
    private RatingBar mRatingBar;
    private CheckBox mFavoritesCheckBox;
    private Button mSubmitButton;
    private String pubYear, coverURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.add_review_title);

        ActivityResultLauncher<Intent> bookResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Book book = (Book) data.getSerializableExtra(RESULT);
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

        // Pointers
        mBookTitle = findViewById(R.id.add_book_title);
        mBookTitle.setOnClickListener(view -> {
            Intent intent = new Intent(NewReviewActivity.this, BookListActivity.class);
            bookResultLauncher.launch(intent);
        });

        mReviewTitle = findViewById(R.id.add_review_title);
        mReviewContent = findViewById(R.id.add_review_content);
        mRatingBar = findViewById(R.id.add_rating_bar);
        mFavoritesCheckBox = findViewById(R.id.add_favorites_box);

        mSubmitButton = findViewById(R.id.add_review_submit_button);
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
                    pubYear,
                    coverURL,
                    rating,
                    favorited,
                    reviewTitle,
                    reviewContent,
                    user.getUid(),
                    user.getDisplayName(),
                    new Date()
            );

            Toast addToast = Toast.makeText(NewReviewActivity.this, "Adding new review...", Toast.LENGTH_SHORT);
            addToast.show();
            mDb.collection(UserFeedActivity.REVIEWS)
                    .add(review)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Log.d(TAG, "Review created with ID: " + task.getResult().getId());
                                addToast.cancel();
                                Toast.makeText(NewReviewActivity.this, "Successfully created new review!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NewReviewActivity.this, UserFeedActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Exception e = task.getException();
                                Log.w(TAG, "Error adding review: ", e);
                                addToast.cancel();
                                Toast.makeText(NewReviewActivity.this, "Error adding review: " + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }); // end of mSubmitButton OnClickListener
    } // end of OnCreate
} // end of Activity