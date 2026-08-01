package com.techno.aiproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.techno.aiproject.R;
import com.techno.aiproject.activities.auth.LoginActivity;
import com.techno.aiproject.utils.PrefManager;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PrefManager pref = new PrefManager(SplashActivity.this);
            Intent intent;

            if (!pref.isOnboardingComplete()) {
                // First launch: show onboarding
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            }
            else if (pref.isLoggedIn()) {
                // Already logged in: go to home
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            }
            else {
                // Not logged in: go to login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
