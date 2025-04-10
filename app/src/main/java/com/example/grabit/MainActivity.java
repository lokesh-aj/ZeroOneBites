package com.example.grabit;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.grabit.Fragments.CartFragment;
import com.example.grabit.Fragments.HomeFragment;
import com.example.grabit.Fragments.ProfileFragment;
import com.example.grabit.Fragments.SummerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Fragment activeFragment;
    private int lastSelectedItemId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // Set custom animations based on navigation direction
        if (fragment instanceof HomeFragment) {
            if (lastSelectedItemId == R.id.nav_cart || lastSelectedItemId == R.id.nav_profile) {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (fragment instanceof CartFragment) {
            if (lastSelectedItemId == R.id.nav_home || lastSelectedItemId == R.id.nav_profile) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        } else if (fragment instanceof ProfileFragment) {
            if (lastSelectedItemId == R.id.nav_home || lastSelectedItemId == R.id.nav_cart) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        activeFragment = fragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            loadFragment(new HomeFragment());
        } else if (itemId == R.id.nav_cart) {
            loadFragment(new CartFragment());
        } else if (itemId == R.id.nav_profile) {
            loadFragment(new ProfileFragment());
        }
        lastSelectedItemId = itemId;
        return true;
    }

    @Override
    public void onBackPressed() {
        if (activeFragment instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
} 