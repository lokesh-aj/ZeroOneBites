package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.example.grabit.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Load animations
        android.view.animation.Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        android.view.animation.Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Start animations
        binding.logoImage.startAnimation(fadeIn);
        binding.appName.startAnimation(slideUp);

        // Navigate to appropriate activity after delay
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in
                boolean isLoggedIn = sharedPreferences.getBoolean("logged", false);
                
                Intent intent;
                if (isLoggedIn) {
                    // User is logged in, go to Dashboard
                    intent = new Intent(SplashActivity.this, Dashboard.class);
                } else {
                    // User is not logged in, go to StartActivity
                    intent = new Intent(SplashActivity.this, StartActivity.class);
                }
                
                startActivity(intent);
                finish();
            }
        }, 2500); // 2.5 seconds delay
    }
} 