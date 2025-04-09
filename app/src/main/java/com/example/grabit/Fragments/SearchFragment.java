package com.example.grabit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.grabit.Adapter.FoodItemAdapter;
import com.example.grabit.Model.FoodItem;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentSearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private FoodItemAdapter adapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> filteredFoodItems;
    private ValueEventListener valueEventListener;
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize lists
        allFoodItems = new ArrayList<>();
        filteredFoodItems = new ArrayList<>();

        // Initialize RecyclerView and adapter
        adapter = new FoodItemAdapter(getContext(), filteredFoodItems);
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.searchResultsRecyclerView.setAdapter(adapter);

        // Get the search query from arguments if available
        String searchQuery = getArguments() != null ? getArguments().getString("searchQuery") : null;

        // Setup search view and load all food items
        setupSearchView(searchQuery);
        loadAllFoodItems();

        return view;
    }

    private void loadAllFoodItems() {
        showLoadingState();
        
        databaseRef = FirebaseDatabase.getInstance().getReference("Food Items");
        
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allFoodItems.clear();
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

                        if (itemName != null && imageUrl != null && price != null) {
                            FoodItem foodItem = new FoodItem(itemId, itemName, imageUrl, price);
                            allFoodItems.add(foodItem);
                        }
                    } catch (Exception e) {
                        showError("Error parsing item: " + e.getMessage());
                    }
                }
                
                if (allFoodItems.isEmpty()) {
                    showEmptyState();
                } else {
                    // Show all items initially
                    filteredFoodItems.clear();
                    filteredFoodItems.addAll(allFoodItems);
                    adapter.notifyDataSetChanged();
                    showResultsState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError("Error loading items: " + databaseError.getMessage());
            }
        };
        
        databaseRef.addValueEventListener(valueEventListener);
    }

    private void setupSearchView(String initialQuery) {
        // Set the initial query if available
        if (initialQuery != null && !initialQuery.isEmpty()) {
            binding.searchView.setQuery(initialQuery, false);
            filterFoodItems(initialQuery);
        }

        // Request focus on the search view
        binding.searchView.requestFocus();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFoodItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFoodItems(newText);
                return true;
            }
        });
    }

    private void filterFoodItems(String query) {
        if (allFoodItems == null || allFoodItems.isEmpty()) {
            return;
        }

        filteredFoodItems.clear();
        
        if (query == null || query.trim().isEmpty()) {
            // If query is empty, show all items
            filteredFoodItems.addAll(allFoodItems);
        } else {
            // Filter items based on the search query
            String lowerQuery = query.toLowerCase().trim();
            for (FoodItem item : allFoodItems) {
                if (item.getName() != null && item.getName().toLowerCase().contains(lowerQuery)) {
                    filteredFoodItems.add(item);
                }
            }
        }
        
        if (filteredFoodItems.isEmpty()) {
            showEmptyState();
        } else {
            showResultsState();
        }
        
        adapter.notifyDataSetChanged();
    }

    private void showLoadingState() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);
    }

    private void showResultsState() {
        binding.progressBar.setVisibility(View.GONE);
        binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
        binding.emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.progressBar.setVisibility(View.GONE);
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        showEmptyState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseRef != null && valueEventListener != null) {
            databaseRef.removeEventListener(valueEventListener);
        }
        binding = null;
    }
}
