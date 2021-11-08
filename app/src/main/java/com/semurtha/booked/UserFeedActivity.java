package com.semurtha.booked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class UserFeedActivity extends AppCompatActivity {

    private static final String TAG = "UserFeed";
    static final String REVIEWS = "reviews";
    static final String CLICKED_REVIEW = "clickedReview";
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Button mRefresh;
    private ArrayAdapter<Review> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String full = user.getEmail() + " " + user.getDisplayName();
        TextView text = findViewById(R.id.feedDisplay);

        if(user != null){
            text.setText(full);
        }

        ListView reviewListView = findViewById(R.id.review_list_view);
        adapter = new ArrayAdapter<Review>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<Review>()
        );
        reviewListView.setAdapter(adapter);
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UserFeedActivity.this, ReviewDetailActivity.class);
                intent.putExtra(CLICKED_REVIEW, (Review) adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });

        FloatingActionButton newReviewButton = findViewById(R.id.fab_new_post);
        newReviewButton.setOnClickListener(v -> clickedNewReview());

        mRefresh = findViewById(R.id.feed_refresh_button);
        mRefresh.setOnClickListener(v -> clickedRefresh());

    }

    public void clickedLogout(View view) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clickedNewReview() {
        Log.d(TAG,"Clicked Add Review");
        Intent intent = new Intent(this, NewReviewActivity.class);
        startActivity(intent);
    }

    public void clickedRefresh() {
        Log.d(TAG,"Clicked Refresh Feed");
        mDb.collection(REVIEWS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Review> reviews = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Review r = document.toObject(Review.class);
                            reviews.add(r);
                            Log.d(TAG, r.getReviewedBy() + " " + r.getReviewTitle());
                        }
                        adapter.clear();
                        adapter.addAll(reviews);
                    }
                });
    }
}