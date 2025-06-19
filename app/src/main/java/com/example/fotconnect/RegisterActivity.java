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

import com.example.fotconnect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField, passwordField, confirmPasswordField, emailField;
    private Button btnSignUp;
    private TextView signInLink;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.etConfirmPassword);
        emailField = findViewById(R.id.etEmail);
        btnSignUp = findViewById(R.id.loginButton);
        signInLink = findViewById(R.id.signUpText);

        btnSignUp.setOnClickListener(v -> registerUser());
        signInLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Username is required.");
            usernameField.requestFocus();
            return;
        }

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

        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters.");
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match.");
            confirmPasswordField.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            User user = new User(username, email);

                            databaseUsers.child(uid).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "User creation failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        String errorMessage = "Registration failed.";
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            errorMessage = "This email is already registered.";
                        } else if (task.getException() != null) {
                            errorMessage = "Error: " + task.getException().getMessage();
                        }
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
