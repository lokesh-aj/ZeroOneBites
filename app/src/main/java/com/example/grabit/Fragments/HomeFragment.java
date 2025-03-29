package com.example.grabit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.CategoryAdapter;
import com.example.grabit.Adapter.PopularAdapter;
import com.example.grabit.Adapter.RecipeAdapter;
import com.example.grabit.Model.Category;
import com.example.grabit.Model.Recipe;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView categoryRecyclerView;
    private RecyclerView popularRecyclerView;
    private RecyclerView summerSelectionRecyclerView;
    private CategoryAdapter categoryAdapter;
    private PopularAdapter popularAdapter;
    private RecipeAdapter summerSelectionAdapter;
    private FragmentHomeBinding binding;
    private ImageView notificationIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize views
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        popularRecyclerView = view.findViewById(R.id.needToTryRecyclerView);
        summerSelectionRecyclerView = view.findViewById(R.id.summerSelectionRecyclerView);
        notificationIcon = view.findViewById(R.id.notificationIcon);

        // Set up notification icon click
        notificationIcon.setOnClickListener(v -> {
            // TODO: Implement notification functionality
            Toast.makeText(getContext(), "Notifications coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Set up category RecyclerView
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getCategoryList());
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Set up Popular RecyclerView
        popularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<String> foodNames = Arrays.asList("Burger", "Sandwich", "MOMOS");
        List<Integer> foodImages = Arrays.asList(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3);
        List<String> prices = Arrays.asList("₹50", "₹60", "₹70");
        popularAdapter = new PopularAdapter(requireContext(), new ArrayList<>(foodNames), new ArrayList<>(foodImages), new ArrayList<>(prices));
        popularRecyclerView.setAdapter(popularAdapter);

        // Set up Summer selection RecyclerView
        summerSelectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        summerSelectionAdapter = new RecipeAdapter(getSummerSelectionRecipes());
        summerSelectionRecyclerView.setAdapter(summerSelectionAdapter);

        // Set up See all buttons
        TextView popularSeeAll = view.findViewById(R.id.viewAllMenu);
        TextView summerSelectionSeeAll = view.findViewById(R.id.summerSelectionSeeAllButton);

        popularSeeAll.setOnClickListener(v -> {
            PopularMenuBottomSheetFragment bottomSheetDialog = new PopularMenuBottomSheetFragment();
            bottomSheetDialog.show(getParentFragmentManager(), "PopularMenuBottomSheet");
        });

        summerSelectionSeeAll.setOnClickListener(v ->
                Toast.makeText(getContext(), "See all Summer selection recipes", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Category> getCategoryList() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Special", R.drawable.ic_special, true));
        categories.add(new Category("Breakfast", R.drawable.ic_breakfast, false));
        categories.add(new Category("Lunch", R.drawable.ic_lunch, false));
        categories.add(new Category("Dinner", R.drawable.ic_dinner, false));
        return categories;
    }

    private List<Recipe> getSummerSelectionRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Spaghetti with Tomatoes", "Fresh pasta with cherry tomatoes, basil and olive oil.",
                R.drawable.burger, 15, "Easy", "550 kcal"));
        return recipes;
    }
}