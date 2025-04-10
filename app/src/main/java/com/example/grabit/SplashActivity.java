package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.grabit.databinding.ActivitySplashBinding;
import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private TextView mealTypeTextView;

    private String getCurrentMealType() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hour >= 8 && hour < 18) { // Between 8 AM and 6 PM
            if (hour >= 9 && hour < 11) {
                return "🍳 Breakfast Time";
            } else if (hour >= 11 && hour < 15) {
                return "🍽️ Main Course Time";
            } else {
                return "🍜 All Day Menu Available";
            }
        }
        return "🚫 Canteen is closed"; // Return closed message if outside business hours
    }

    private void animateMealType() {
        String mealType = getCurrentMealType();
        mealTypeTextView.setText(mealType);
        mealTypeTextView.animate()
                .alpha(1.0f)
                .setDuration(1000)
                .setStartDelay(500)
                .start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        mealTypeTextView = binding.mealType;
        animateMealType();

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

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
        }, SPLASH_DELAY);
    }
} 