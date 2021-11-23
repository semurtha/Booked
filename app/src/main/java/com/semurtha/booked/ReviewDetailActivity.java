package com.semurtha.booked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewDetailActivity extends AppCompatActivity {

    public static final String TAG = "ReviewDetail";

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    // Class variables
    ImageView mBookImage;
    TextView mBookTitle, mBookYear, mReviewerName, mReviewDate, mReviewTitle, mReviewContent;
    RatingBar mRating;

    Review mReview;
    String reviewer;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        // Pointers
        mBookImage = findViewById(R.id.detail_book_image);
        mBookTitle = findViewById(R.id.detail_book_title);
        mBookYear = findViewById(R.id.detail_book_year);
        mReviewerName = findViewById(R.id.detail_reviewer_name);
        mReviewDate = findViewById(R.id.detail_review_date);
        mReviewTitle = findViewById(R.id.detail_review_title);
        mReviewContent = findViewById(R.id.detail_review_content);
        mRating = findViewById(R.id.detail_book_rating);

        mReview = (Review) getIntent().getSerializableExtra(UserFeedActivity.CLICKED_REVIEW);
        if (mReview != null) {
            Log.d(TAG, "User clicked a review");
//            mBookImage.setImageDrawable(); TODO: Add Book API info to get cover image
            mBookTitle.setText(mReview.getBookTitle());
//            mBookYear.setText("Book Year Goes Here"); TODO: Add Book API info to get year
            mRating.setRating(mReview.getRating());
//            mReviewerName.setText("Reviewer Name Goes Here"); TODO: Implement Display Name Features
            mReviewDate.setText(mReview.getReviewDate().toString());
            mReviewTitle.setText(mReview.getReviewTitle());
            mReviewContent.setText(mReview.getReviewContent());
            reviewer = mReview.getReviewedBy();
            user = mAuth.getCurrentUser().getUid();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar
        getMenuInflater().inflate(R.menu.menu_view_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // User is viewing their own review
        if(user.contentEquals(reviewer)){
            Log.d(TAG, "Reviewer is the same. Edit/Delete visible.\n"
                    + "reviewer: "  + reviewer
                    + "\nuser: " + user);
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        // User is viewing somebody else's review
        else {
            Log.d(TAG, "Reviewer is NOT the same. Edit/Delete NOT visible.\n"
                    + "reviewer: "  + reviewer
                    + "\nuser: " + user);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(this, UserFeedActivity.class));
                return true;

            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_edit:
                Intent editIntent = new Intent(ReviewDetailActivity.this, EditReviewActivity.class);
                editIntent.putExtra(UserFeedActivity.CLICKED_REVIEW, mReview);
                startActivity(editIntent);

            case R.id.action_delete:
                // Popup window: are you sure?
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Are you sure you want to delete this review?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Remove document from firestore and launch feed activity
                                mDb.collection(UserFeedActivity.REVIEWS).document(mReview.getId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                Toast.makeText(ReviewDetailActivity.this, "Successfully deleted review!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                                dialogInterface.dismiss();
                                startActivity(new Intent(ReviewDetailActivity.this, UserFeedActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Close dialog box
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}