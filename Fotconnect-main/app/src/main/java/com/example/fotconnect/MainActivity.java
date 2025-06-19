package com.example.fotconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fotconnect.fragments.AcademicFragment;
import com.example.fotconnect.fragments.DevInfoFragment;
import com.example.fotconnect.fragments.EventsFragment;
import com.example.fotconnect.fragments.SportsFragment;
import com.example.fotconnect.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private final Fragment sportsFragment = new SportsFragment();
    private final Fragment academicFragment = new AcademicFragment();
    private final Fragment eventsFragment = new EventsFragment();
    private final Fragment devInfoFragment = new DevInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_sports) {
                loadFragment(sportsFragment);
                return true;
            } else if (id == R.id.nav_academic) {
                loadFragment(academicFragment);
                return true;
            } else if (id == R.id.nav_events) {
                loadFragment(eventsFragment);
                return true;
            } else if (id == R.id.dev_info) {
                loadFragment(devInfoFragment);
                return true;
            }
            return false;
        });

        // Launch UserActivity as a separate screen
        ImageButton imageButton = findViewById(R.id.imageButton4);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.fotconnect.models.UserActivity.class);
            startActivity(intent);
        });

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(academicFragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
