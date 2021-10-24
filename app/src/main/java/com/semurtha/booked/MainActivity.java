package com.semurtha.booked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SignIn";
    private FirebaseAuth mAuth;

    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.signInEmail);
        passwordField = findViewById(R.id.signInPassword);


        TextView signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(view -> signIn());

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if a user is currently signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, UserFeedActivity.class);
            startActivity(intent);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }


        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI();
                    } else {
                        // If sign in fails, display a message to the user.
                        Exception e = task.getException();
                        Log.w(TAG, "signInWithEmail:failure", e);
                        Toast.makeText(MainActivity.this, "Login failed: " + (e != null ? e.getLocalizedMessage() : null),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUI() {
        Intent intent = new Intent(this, UserFeedActivity.class);
        startActivity(intent);
    }

}
