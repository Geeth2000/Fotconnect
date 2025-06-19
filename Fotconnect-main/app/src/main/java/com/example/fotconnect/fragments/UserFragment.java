package com.example.fotconnect.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.fotconnect.LoginActivity;
import com.example.fotconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView usernameTextView, emailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_info, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Get reference to your database

        usernameTextView = view.findViewById(R.id.username_text);
        emailTextView = view.findViewById(R.id.email_text);
        Button btnSignOut = view.findViewById(R.id.btnSignOut);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);

        btnSignOut.setOnClickListener(v -> showLogoutConfirmationDialog());
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        loadUserProfile(); // Load user data when the fragment is created

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        if (username != null) {
                            usernameTextView.setText("Username: " + username);
                        }
                        if (email != null) {
                            emailTextView.setText("Email: " + email);
                        }
                    } else {
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.editdialog, null);

        EditText editUsername = dialogView.findViewById(R.id.editUsername);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Pre-fill current data in edit fields
        String currentUsername = usernameTextView.getText().toString().replace("Username: ", "");
        String currentEmail = emailTextView.getText().toString().replace("Email: ", "");
        editUsername.setText(currentUsername);
        editEmail.setText(currentEmail);


        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Firebase Realtime Database
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                mDatabase.child("users").child(uid).child("username").setValue(newUsername);
                mDatabase.child("users").child(uid).child("email").setValue(newEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                                // Update UI immediately after successful save
                                usernameTextView.setText("Username: " + newUsername);
                                emailTextView.setText("Email: " + newEmail);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showLogoutConfirmationDialog() {
        // Inflate the custom layout for the sign out dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.signout, null);

        // Find the custom buttons within the inflated layout
        Button btnOk = dialogView.findViewById(R.id.btnSave); // Assuming btnSave corresponds to "Ok" in signout.xml
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Create the AlertDialog using the custom view
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false) // Prevent dismissing by tapping outside
                .create();

        // Set click listeners for the custom buttons
        btnOk.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getContext(), "Signed out successfully.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
            dialog.dismiss(); // Dismiss the custom dialog after action
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss the custom dialog
        });

        dialog.show();
    }
}