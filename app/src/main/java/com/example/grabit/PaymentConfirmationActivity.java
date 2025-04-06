package com.example.grabit;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private View rippleView1;
    private View rippleView2;
    private View rippleView3;
    private ImageView checkmarkIcon;
    private TextView titleText;
    private TextView amountText;
    private TextView ordId;
    private String orderId;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        // Initialize views
        rippleView1 = findViewById(R.id.ripple_view_1);
        rippleView2 = findViewById(R.id.ripple_view_2);
        rippleView3 = findViewById(R.id.ripple_view_3);
        checkmarkIcon = findViewById(R.id.checkmark_icon);
        titleText = findViewById(R.id.title_text);
        amountText = findViewById(R.id.amount_text);
        ordId = findViewById(R.id.ord_id);

        // Retrieve the transferred data
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        orderId = getIntent().getStringExtra("orderId");

        // Display the data
        amountText.setText("₹" + String.format("%.2f", totalAmount));
        ordId.setText("( Order ID: " + orderId + " )");

        // Start animations
        animatePaymentConfirmation();

        // Navigate to VoucherActivity after animation completes
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, VoucherActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("totalAmount", totalAmount);
            startActivity(intent);
            finish();
        }, 2000); // Wait for 2 seconds (animation duration)
    }

    private void animatePaymentConfirmation() {
        // Ripple Animations
        AnimatorSet ripple1AnimatorSet = createRippleAnimator(rippleView1);
        AnimatorSet ripple2AnimatorSet = createRippleAnimator(rippleView2);
        AnimatorSet ripple3AnimatorSet = createRippleAnimator(rippleView3);

        // Checkmark Scale Animation
        ObjectAnimator checkmarkScaleX = ObjectAnimator.ofFloat(checkmarkIcon, View.SCALE_X, 0.3f, 1f);
        ObjectAnimator checkmarkScaleY = ObjectAnimator.ofFloat(checkmarkIcon, View.SCALE_Y, 0.3f, 1f);
        checkmarkScaleX.setInterpolator(new AnticipateOvershootInterpolator());
        checkmarkScaleY.setInterpolator(new AnticipateOvershootInterpolator());

        // Checkmark Translation Animation
        ObjectAnimator checkmarkTranslationX = ObjectAnimator.ofFloat(checkmarkIcon, View.TRANSLATION_X, 0f, 50f, 0f);
        ObjectAnimator checkmarkTranslationY = ObjectAnimator.ofFloat(checkmarkIcon, View.TRANSLATION_Y, 0f, -50f, 0f);
        checkmarkTranslationX.setInterpolator(new FastOutSlowInInterpolator());
        checkmarkTranslationY.setInterpolator(new FastOutSlowInInterpolator());

        // Text Animations
        ObjectAnimator titleFadeAnimator = ObjectAnimator.ofFloat(titleText, View.ALPHA, 0f, 1f);
        ObjectAnimator amountFadeAnimator = ObjectAnimator.ofFloat(amountText, View.ALPHA, 0f, 1f);
        ObjectAnimator titleScaleX = ObjectAnimator.ofFloat(titleText, View.SCALE_X, 0.5f, 1f);
        ObjectAnimator titleScaleY = ObjectAnimator.ofFloat(titleText, View.SCALE_Y, 0.5f, 1f);

        // Combine Animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ripple1AnimatorSet,
                ripple2AnimatorSet,
                ripple3AnimatorSet,
                checkmarkScaleX,
                checkmarkScaleY,
                checkmarkTranslationX,
                checkmarkTranslationY,
                titleFadeAnimator,
                amountFadeAnimator,
                titleScaleX,
                titleScaleY
        );
        animatorSet.setDuration(1500);
        animatorSet.start();
    }

    private AnimatorSet createRippleAnimator(View rippleView) {
        ObjectAnimator rippleScaleX = ObjectAnimator.ofFloat(rippleView, View.SCALE_X, 0.5f, 3f);
        ObjectAnimator rippleScaleY = ObjectAnimator.ofFloat(rippleView, View.SCALE_Y, 0.5f, 3f);
        ObjectAnimator rippleAlpha = ObjectAnimator.ofFloat(rippleView, View.ALPHA, 1f, 0f);

        rippleScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        rippleScaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        rippleAlpha.setInterpolator(new FastOutSlowInInterpolator());

        AnimatorSet rippleAnimatorSet = new AnimatorSet();
        rippleAnimatorSet.playTogether(rippleScaleX, rippleScaleY, rippleAlpha);
        return rippleAnimatorSet;
    }
}
