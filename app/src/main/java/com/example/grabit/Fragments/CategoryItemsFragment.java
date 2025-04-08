package com.example.grabit.Fragments;

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

import com.example.grabit.Adapter.FoodItemAdapter;
import com.example.grabit.Model.FoodItem;
import com.example.grabit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemsFragment extends Fragment {
    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;
    private List<FoodItem> foodItems;
    private TextView categoryTitleText;
    private String categoryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_items, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.categoryItemsRecyclerView);
        categoryTitleText = view.findViewById(R.id.categoryTitleText);

        // Get category name from arguments
        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName", "");
            categoryTitleText.setText(categoryName);
        }

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        foodItems = new ArrayList<>();
        adapter = new FoodItemAdapter(getContext(), foodItems);
        recyclerView.setAdapter(adapter);

        // Load food items from Firebase
        loadFoodItems();

        return view;
    }

    private void loadFoodItems() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Food Items");
        Query query = databaseRef.orderByChild("Category").equalTo(categoryName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        // Handle Item ID (could be Long)
                        String itemId;
                        Object idObj = snapshot.child("Item ID").getValue();
                        if (idObj instanceof Long) {
                            itemId = String.valueOf(idObj);
                        } else {
                            itemId = (String) idObj;
                        }

                        // Get item name
                        String itemName = snapshot.child("Item Name").getValue(String.class);
                        
                        // Get image URL
                        String imageUrl = snapshot.child("Image URL").getValue(String.class);
                        
                        // Handle Price (could be Long)
                        String price;
                        Object priceObj = snapshot.child("Price (INR)").getValue();
                        if (priceObj instanceof Long) {
                            price = String.valueOf(priceObj);
                        } else {
                            price = (String) priceObj;
                        }

                        if (itemName != null && imageUrl != null) {
                            FoodItem foodItem = new FoodItem(itemId, itemName, imageUrl, price);
                            foodItems.add(foodItem);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error parsing item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                adapter.notifyDataSetChanged();

                if (foodItems.isEmpty()) {
                    Toast.makeText(getContext(), "No items found in this category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 