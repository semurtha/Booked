package com.semurtha.booked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUp";
    private FirebaseAuth mAuth;

    private EditText emailField;
    private EditText emailConfField;
    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.sign_up_title);

        emailField = findViewById(R.id.su_email);
        emailConfField = findViewById(R.id.su_email_confirm);
        usernameField = findViewById(R.id.su_display_name);
        passwordField = findViewById(R.id.su_password);
        passwordConfField = findViewById(R.id.su_password_confirm);

        mAuth = FirebaseAuth.getInstance();
    }

    public void clickedCancel(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clickedSignUp(View view){

        // Verify the entries are valid
        if (!validateForm()){
            return;

        }

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:Success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(String.valueOf(usernameField.getText())).build();
                        assert user != null;
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        Log.d(TAG, "Display name updated.");
                                    }
                                    else {
                                        Log.d(TAG, "Display name failed to update.");
                                    }
                                });
                        updateUI(user);
                    } else {
                        Exception e = task.getException();
                        Log.w(TAG, "CreateUserWithEmail:Failed", task.getException());
                        Toast.makeText(SignUpActivity.this, "Auth failed: " + (e != null ? e.getLocalizedMessage() : null),
                                Toast.LENGTH_LONG).show();
                    }
                });

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

        String emailConf = emailConfField.getText().toString();
        if (TextUtils.isEmpty(emailConf)) {
            emailConfField.setError("Required.");
            valid = false;
        } else if (!email.contentEquals(emailConf)) {
            emailField.setError("Does not match.");
            emailConfField.setError("Does not match.");
        } else {
            emailField.setError(null);
            emailConfField.setError(null);
        }

        String username = usernameField.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Required.");
            valid = false;
        } else {
            usernameField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        String passwordConf = passwordConfField.getText().toString();
        if (TextUtils.isEmpty(passwordConf)) {
            passwordConfField.setError("Required.");
            valid = false;
        } else if (!password.contentEquals(passwordConf)) {
            passwordField.setError("Does not match.");
            passwordConfField.setError("Does not match.");
        } else if (password.length() < 6) {
            passwordField.setError("Must be >= 6 characters.");
        } else {
            passwordField.setError(null);
            passwordConfField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        startActivity(intent);
    }

}