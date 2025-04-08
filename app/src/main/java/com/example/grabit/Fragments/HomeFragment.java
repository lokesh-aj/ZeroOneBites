package com.example.grabit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.CategoryAdapter;
import com.example.grabit.Adapter.RecipeAdapter;
import com.example.grabit.Adapter.PopularAdapter;
import com.example.grabit.Model.Category;
import com.example.grabit.Model.Recipe;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentHomeBinding;
import com.example.grabit.Adapter.GridItemAdapter;
import com.example.grabit.Model.GridItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private SearchView searchView;
    private RecyclerView categoryRecyclerView;
    private RecyclerView needToTryRecyclerView;
    private RecyclerView summerSelectionRecyclerView;
    private CategoryAdapter categoryAdapter;
    private RecipeAdapter needToTryAdapter;
    private RecipeAdapter summerSelectionAdapter;
    private FragmentHomeBinding binding;
    private List<GridItem> itemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize views
        searchView = view.findViewById(R.id.searchView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        needToTryRecyclerView = view.findViewById(R.id.needToTryRecyclerView);
        summerSelectionRecyclerView = view.findViewById(R.id.summerSelectionRecyclerView);

        // Set up search view
        searchView.setIconified(true); // Start in iconified state
        searchView.setQueryHint("Search for food items...");
        
        // Set up search view click listener
        searchView.setOnClickListener(v -> {
            // Get the current query from the search view
            String currentQuery = searchView.getQuery().toString();
            Bundle bundle = new Bundle();
            bundle.putString("searchQuery", currentQuery);
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_searchFragment, bundle);
        });

        // Set up search view query listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Navigate to search fragment with the query
                Bundle bundle = new Bundle();
                bundle.putString("searchQuery", query);
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_searchFragment, bundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Set up category RecyclerView
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        itemList = new ArrayList<>();
        itemList.add(new GridItem("Breakfast", R.drawable.ic_breakfast));
        itemList.add(new GridItem("Snack", R.drawable.ic_snack));
        itemList.add(new GridItem("Chinese", R.drawable.ic_chinese));
        itemList.add(new GridItem("Chinese Starter", R.drawable.ic_chinese_starter));
        itemList.add(new GridItem("Beverage", R.drawable.ic_beverage));
        itemList.add(new GridItem("Main Course", R.drawable.ic_main_course));
        itemList.add(new GridItem("A La Carte", R.drawable.ic_a_la_carte));
        itemList.add(new GridItem("Chaat", R.drawable.ic_chaat));

        GridItemAdapter adapter = new GridItemAdapter(getContext(), itemList);
        categoryRecyclerView.setAdapter(adapter);

        // Set up Need to try RecyclerView
        needToTryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        needToTryAdapter = new RecipeAdapter(getNeedToTryRecipes());
        needToTryRecyclerView.setAdapter(needToTryAdapter);

        // Set up Summer selection RecyclerView
        summerSelectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        summerSelectionAdapter = new RecipeAdapter(getSummerSelectionRecipes());
        summerSelectionRecyclerView.setAdapter(summerSelectionAdapter);

        // Set up See all buttons
        TextView needToTrySeeAll = view.findViewById(R.id.needToTrySeeAllButton);
        TextView summerSelectionSeeAll = view.findViewById(R.id.summerSelectionSeeAllButton);

        needToTrySeeAll.setOnClickListener(v -> {
            MenuBottomSheetFragment bottomSheetDialog = new MenuBottomSheetFragment();
            bottomSheetDialog.show(getParentFragmentManager(), "MenuBottomSheet");
        });

        summerSelectionSeeAll.setOnClickListener(v ->
                Toast.makeText(getContext(), "See all Summer selection recipes", Toast.LENGTH_SHORT).show());

        // Initialize data for popular items
        List<String> foodName = Arrays.asList("Burger", "Sandwich", "MOMOS");
        List<String> prices = Arrays.asList("₹50", "₹60", "₹70");
        List<Integer> popularFoodImages = Arrays.asList(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3);

        // Set up popular RecyclerView with horizontal layout
        PopularAdapter popularAdapter = new PopularAdapter(requireContext(), 
            new ArrayList<>(foodName), 
            new ArrayList<>(popularFoodImages), 
            new ArrayList<>(prices));
        binding.needToTryRecyclerView.setLayoutManager(
            new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.needToTryRecyclerView.setAdapter(popularAdapter);

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

    private List<Recipe> getNeedToTryRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Morning Pancakes", "Deep-fried ball of spiced with ground chickpeas or fava beans.",
                R.drawable.img_pancakes, 1, "Easy", "300 kcal"));
        recipes.add(new Recipe("Fresh Tofu Salad", "Crispy tofu, greens, veggies, and tangy sesame ginger dressing.",
                R.drawable.burger, 10, "Medium", "470 kcal"));
        return recipes;
    }

    private List<Recipe> getSummerSelectionRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe("Spaghetti with Tomatoes", "Fresh pasta with cherry tomatoes, basil and olive oil.",
                R.drawable.burger, 15, "Easy", "550 kcal"));
        return recipes;
    }
}