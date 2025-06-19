package com.example.fotconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button btnSignIn;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailField = findViewById(R.id.emailEditText);
        passwordField = findViewById(R.id.passwordField);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvRegister = findViewById(R.id.tvRegister);

        btnSignIn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString();

            if (TextUtils.isEmpty(email)) {
                emailField.setError("Email is required.");
                emailField.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordField.setError("Password is required.");
                passwordField.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Fetch user profile data from Realtime Database
                                fetchUserProfile(user.getUid());
                            }
                        } else {
                            String errorMessage = "Login failed.";
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                errorMessage = "No user found with this email.";
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Invalid credentials. Please check your password.";
                            } else if (task.getException() != null) {
                                errorMessage = "Error: " + task.getException().getMessage();
                            }

                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void fetchUserProfile(String uid) {
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    Toast.makeText(LoginActivity.this,
                            "Welcome " + (username != null ? username : email),
                            Toast.LENGTH_SHORT).show();

                    // Proceed to MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "User profile data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,
                        "Failed to fetch user profile: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User already signed in, optionally fetch profile or just open MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
