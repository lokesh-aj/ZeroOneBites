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
import com.example.grabit.Adapter.MenuAdapter;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentSearchBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private MenuAdapter adapter;

    // Original menu data
    private final List<String> originalMenuFoodName = Arrays.asList("Burger", "Sandwich", "Momo", "Pizza", "Sandwich", "Momo");
    private final List<String> originalMenuItemPrice = Arrays.asList("₹50", "₹50", "₹50", "₹50", "₹50", "₹50");
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
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize RecyclerView and adapter
        adapter = new MenuAdapter(requireContext(), filteredMenuFoodName, filteredMenuItemPrice, filteredMenuImage);
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.searchResultsRecyclerView.setAdapter(adapter);

        // Get the search query from arguments if available
        String searchQuery = getArguments() != null ? getArguments().getString("searchQuery") : null;

        // Setup search view and show all menu items initially
        setupSearchView(searchQuery);
        showAllMenu();

        return view;
    }

    private void showAllMenu() {
        filteredMenuFoodName.clear();
        filteredMenuItemPrice.clear();
        filteredMenuImage.clear();

        filteredMenuFoodName.addAll(originalMenuFoodName);
        filteredMenuItemPrice.addAll(originalMenuItemPrice);
        filteredMenuImage.addAll(originalMenuImage);

        adapter.notifyDataSetChanged();
    }

    private void setupSearchView(String initialQuery) {
        // Set the initial query if available
        if (initialQuery != null && !initialQuery.isEmpty()) {
            binding.searchView.setQuery(initialQuery, false);
            filterMenuItem(initialQuery);
        }

        // Request focus on the search view
        binding.searchView.requestFocus();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMenuItem(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenuItem(newText);
                return true;
            }
        });
    }

    private void filterMenuItem(String query) {
        filteredMenuFoodName.clear();
        filteredMenuItemPrice.clear();
        filteredMenuImage.clear();

        for (int i = 0; i < originalMenuFoodName.size(); i++) {
            String foodName = originalMenuFoodName.get(i);
            if (foodName.toLowerCase().contains(query.toLowerCase())) {
                filteredMenuFoodName.add(foodName);
                filteredMenuItemPrice.add(originalMenuItemPrice.get(i));
                filteredMenuImage.add(originalMenuImage.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
