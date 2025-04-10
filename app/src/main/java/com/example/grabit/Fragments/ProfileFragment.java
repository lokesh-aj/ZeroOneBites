package com.example.grabit.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grabit.LoginActivity;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentProfileBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private String sapId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase instance
        database = FirebaseDatabase.getInstance();

        // Get SAP ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        sapId = sharedPreferences.getString("sapID", null);

        // Set up logout button click listener
        binding.btnLogout.setOnClickListener(v -> {
            // Clear user data from SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("logged", false);
            editor.apply();

            // Navigate to login activity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        if (sapId != null) {
            // Initialize database reference with SAP ID
            userRef = database.getReference().child("Users").child(sapId);
            
            // Load user data
            loadUserData();

            // Set up save button click listener
            binding.saveButton.setOnClickListener(v -> saveProfileChanges());
        } else {
            // User is not logged in, show appropriate UI
            showNotLoggedInUI();
        }

        return view;
    }

    private void showNotLoggedInUI() {
        // Disable all input fields
        binding.nameEditText.setEnabled(false);
        binding.emailEditText.setEnabled(false);
        binding.phoneEditText.setEnabled(false);
        binding.saveButton.setEnabled(false);

        // Show message to user
        binding.nameEditText.setText("Please log in to view and edit your profile");
        binding.emailEditText.setText("");
        binding.phoneEditText.setText("");
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.nameEditText.setText(snapshot.child("name").getValue(String.class));
                    binding.emailEditText.setText(snapshot.child("email").getValue(String.class));
                    binding.phoneEditText.setText(snapshot.child("phone").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading profile data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileChanges() {
        String name = binding.nameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameLayout.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            binding.emailLayout.setError("Email is required");
            return;
        }

        if (phone.isEmpty()) {
            binding.phoneLayout.setError("Phone number is required");
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);

        // Update Realtime Database
        userRef.updateChildren(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}