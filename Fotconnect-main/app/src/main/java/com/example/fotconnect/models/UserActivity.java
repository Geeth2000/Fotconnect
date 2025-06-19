package com.example.fotconnect.models;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fotconnect.LoginActivity;
import com.example.fotconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView usernameTextView, emailTextView;
    private Button btnEditProfile, btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        usernameTextView = findViewById(R.id.username_text);
        emailTextView = findViewById(R.id.email_text);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSignOut = findViewById(R.id.btnSignOut);

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnSignOut.setOnClickListener(v -> showLogoutConfirmationDialog());

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        usernameTextView.setText("Username: " + (username != null ? username : "N/A"));
                        emailTextView.setText("Email: " + (email != null ? email : "N/A"));
                    } else {
                        Toast.makeText(UserActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(UserActivity.this, "Failed to load user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.editdialog, null);

        EditText editUsername = dialogView.findViewById(R.id.editUsername);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Pre-fill fields
        String currentUsername = usernameTextView.getText().toString().replace("Username: ", "");
        String currentEmail = emailTextView.getText().toString().replace("Email: ", "");
        editUsername.setText(currentUsername);
        editEmail.setText(currentEmail);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                mDatabase.child("users").child(uid).child("username").setValue(newUsername);
                mDatabase.child("users").child(uid).child("email").setValue(newEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                usernameTextView.setText("Username: " + newUsername);
                                emailTextView.setText("Email: " + newEmail);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(this, "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showLogoutConfirmationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.signout, null);

        Button btnOk = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnOk.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
