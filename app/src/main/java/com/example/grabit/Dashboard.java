package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import android.util.Log;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import javax.annotation.Nullable;
import java.util.Calendar;

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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        bottomNav = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get user info from SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String sapId = sharedPreferences.getString("sapID", "0");
        String userName = sharedPreferences.getString("userName", "User");

        // Set user info in navigation header
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_user_name);
        TextView tvSapId = headerView.findViewById(R.id.tv_sap_id);
        tvUserName.setText(userName);
        tvSapId.setText("SAP ID: " + sapId);

        // Get the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            // Get the NavController from the NavHostFragment
            navController = navHostFragment.getNavController();

            // Configure the AppBarConfiguration with the navigation drawer and bottom navigation
            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.homeFragment, R.id.historyFragment, R.id.profileFragment, R.id.cartFragment)
                    .setOpenableLayout(drawerLayout)
                    .build();

            // Set up the ActionBar with the NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Set up the NavigationView with the NavController
            navigationView.setNavigationItemSelectedListener(item -> {
                // Handle navigation items
                NavigationUI.onNavDestinationSelected(item, navController);
                drawerLayout.closeDrawers();
                return true;
            });

            // Set up the BottomNavigationView with the NavController
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Add navigation listener to handle badge updates
            bottomNav.setOnItemSelectedListener(item -> {
                NavigationUI.onNavDestinationSelected(item, navController);
                drawerLayout.closeDrawers();
                return true;
            });
        }

        // Update cart badge count dynamically
        updateCartBadge();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
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