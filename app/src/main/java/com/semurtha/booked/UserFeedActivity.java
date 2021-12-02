package com.semurtha.booked;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class UserFeedActivity extends AppCompatActivity  implements FeedAdapter.Listener{

    private static final String TAG = "UserFeed";
    static final String REVIEWS = "reviews";
    static final String CLICKED_REVIEW = "clickedReview";
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayAdapter<Review> adapter;
    private FeedAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Review> reviewArrayList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.user_feed_title);
        FirebaseUser user = mAuth.getCurrentUser();
        mDb = FirebaseFirestore.getInstance();
        reviewArrayList = new ArrayList<Review>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        mRecyclerView = findViewById(R.id.review_recycler_view);
        mAdapter = new FeedAdapter(UserFeedActivity.this, reviewArrayList);
        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton newReviewButton = findViewById(R.id.fab_new_post);
        newReviewButton.setOnClickListener(v -> clickedNewReview());

        EventChangeListener();
    }

    private void EventChangeListener() {

        mDb.collection(REVIEWS).orderBy("reviewDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.e(TAG, error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                QueryDocumentSnapshot document = dc.getDocument();
                                Review r = document.toObject(Review.class);
                                r.setId(document.getId());
                                reviewArrayList.add(r);
                            }
                            if (dc.getType() == DocumentChange.Type.REMOVED) {
                                QueryDocumentSnapshot document = dc.getDocument();
                                Review r = document.toObject(Review.class);
                                r.setId(document.getId());
                                reviewArrayList.remove(r);
                            }

                            mAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(this, UserFeedActivity.class));
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clickedNewReview() {
        Log.d(TAG,"Clicked Add Review");
        Intent intent = new Intent(this, NewReviewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(int position) {
        Log.d(TAG, "Clicked index: " + position);
        Intent intent = new Intent(UserFeedActivity.this, ReviewDetailActivity.class);
        intent.putExtra(CLICKED_REVIEW, reviewArrayList.get(position));
        startActivity(intent);

    }

    public void logout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}