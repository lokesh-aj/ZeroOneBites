package com.example.grabit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedeemedFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FirebaseFirestore db;
    private String userId;
    private RedeemedAdapter adapter;
    private List<Map<String, Object>> redeemedVouchers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_redeemed, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_redeemed);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("sapID", "0");
        
        // Setup RecyclerView
        redeemedVouchers = new ArrayList<>();
        adapter = new RedeemedAdapter(redeemedVouchers, requireContext(), userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        // Load redeemed vouchers
        loadRedeemedVouchers();
        
        return view;
    }
    
    private void loadRedeemedVouchers() {
        db.collection("RedeemedHistory")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    redeemedVouchers.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        redeemedVouchers.add(document.getData());
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (redeemedVouchers.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading redeemed vouchers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
} 