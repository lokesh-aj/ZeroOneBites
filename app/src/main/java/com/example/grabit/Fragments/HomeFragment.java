package com.example.grabit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.grabit.Adapter.PopularAdapter;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.viewAllMenu.setOnClickListener(v -> {
            MenuBottomSheetFragment bottomSheetDialog = new MenuBottomSheetFragment();
            bottomSheetDialog.show(getParentFragmentManager(), "Test");
        });

        // Initialize data
        List<String> foodName = Arrays.asList("Burger", "Sandwich", "MOMOS");
        List<String> prices = Arrays.asList("₹50", "₹60", "₹70");
        List<Integer> popularFoodImages = Arrays.asList(R.drawable.menu1, R.drawable.menu2, R.drawable.menu3);

        // Set up RecyclerView
        PopularAdapter adapter = new PopularAdapter(new ArrayList<>(foodName), new ArrayList<>(popularFoodImages), new ArrayList<>(prices));
        binding.popularRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.popularRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
