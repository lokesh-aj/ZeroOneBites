package com.example.grabit;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Find the TextView
        TextView textView = findViewById(R.id.textView);

        // Get the colors from resources
        int startColor = ContextCompat.getColor(this, R.color.gradient_start);
        int endColor = ContextCompat.getColor(this, R.color.gradient_end);

        // Get paint object
        Paint paint = textView.getPaint();
        float textWidth = paint.measureText(textView.getText().toString());

        // Create LinearGradient Shader (for vertical gradient)
        Shader textShader = new LinearGradient(
                0, 0, textWidth, 0,  // Horizontal Gradient
                new int[]{startColor, endColor},
                null, Shader.TileMode.CLAMP
        );


        // Apply shader to TextView
        textView.getPaint().setShader(textShader);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }
}
