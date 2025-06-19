package com.example.fotconnect;  // replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fotconnect.MainActivity;
import com.example.fotconnect.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // replace with your XML layout file name without .xml

        // Optional: Navigate to next activity after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, com.example.fotconnect.LoginActivity.class); // replace NextActivity with your target
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
