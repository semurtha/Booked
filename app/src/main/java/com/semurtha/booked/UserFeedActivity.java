package com.semurtha.booked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserFeedActivity extends AppCompatActivity {

    private static final String TAG = "UserFeed";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String em = user.getEmail();
        String dn = user.getDisplayName();
        String full = em + " " + dn;
        TextView text = findViewById(R.id.feedDisplay);

        if(user != null){
            text.setText(full);
        }
    }

    public void clickedLogout(View view) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}