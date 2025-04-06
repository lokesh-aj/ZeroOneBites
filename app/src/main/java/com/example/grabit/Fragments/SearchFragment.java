package com.example.grabit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grabit.Adapter.MenuAdapter;
import com.example.grabit.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;

    // Original menu data
    private final List<String> originalMenuFoodName = Arrays.asList(
            "Burger", "Sandwich", "Momo", "Pizza", "French Fries", "Coffee"
    );
    private final List<String> originalMenuItemPrice = Arrays.asList(
            "₹50", "₹40", "₹60", "₹120", "₹80", "₹30"
    );
    private final List<Integer> originalMenuImage = Arrays.asList(
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu5,
            R.drawable.menu6
    );

    // Filtered lists
    private final List<String> filteredMenuFoodName = new ArrayList<>();
    private final List<String> filteredMenuItemPrice = new ArrayList<>();
    private final List<Integer> filteredMenuImage = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize views
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MenuAdapter(requireContext(), filteredMenuFoodName, filteredMenuItemPrice, filteredMenuImage);
        recyclerView.setAdapter(adapter);

        // Show all items initially
        showAllItems();

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });

        return view;
    }

    private void showAllItems() {
        filteredMenuFoodName.clear();
        filteredMenuItemPrice.clear();
        filteredMenuImage.clear();

        filteredMenuFoodName.addAll(originalMenuFoodName);
        filteredMenuItemPrice.addAll(originalMenuItemPrice);
        filteredMenuImage.addAll(originalMenuImage);

        adapter.notifyDataSetChanged();
    }

    private void filterItems(String query) {
        filteredMenuFoodName.clear();
        filteredMenuItemPrice.clear();
        filteredMenuImage.clear();

        String lowercaseQuery = query.toLowerCase().trim();

        if (lowercaseQuery.isEmpty()) {
            showAllItems();
            return;
        }

        for (int i = 0; i < originalMenuFoodName.size(); i++) {
            String foodName = originalMenuFoodName.get(i);
            if (foodName.toLowerCase().contains(lowercaseQuery)) {
                filteredMenuFoodName.add(foodName);
                filteredMenuItemPrice.add(originalMenuItemPrice.get(i));
                filteredMenuImage.add(originalMenuImage.get(i));
            }
        }

        adapter.notifyDataSetChanged();
    }
}
