package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import javax.annotation.Nullable;


public class Dashboard extends AppCompatActivity {
    private void setCartBadge(int count) {
        if (count > 0) {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.cartFragment);
            badge.setVisible(true);
            badge.setNumber(count);
        } else {
            bottomNav.removeBadge(R.id.cartFragment);
        }
    }

    private void updateCartBadge() {


        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String user =  sharedPreferences.getString("sapID", "0");
        if (user != "0") {
            CollectionReference cartRef = db.collection("Users").document(user).collection("Cart");

            // Listen for real-time updates
            cartRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Cart", "Firestore Error: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        setCartBadge(value.size()); // Update badge with total cart count
                    }
                }
            });
        }
    }


    public void goToActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();  // Finish login activity to prevent going back to it
    }

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();

        // Initialize NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Setup BottomNavigationView with NavController
        bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Update cart badge count dynamically
        updateCartBadge();
    }

    public void logout(View view) {
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Save login state in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logged", false);
        editor.apply();

        goToActivity();
    }
}