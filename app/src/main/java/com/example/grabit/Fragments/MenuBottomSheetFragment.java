package com.example.grabit.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.grabit.Adapter.MenuAdapter;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentMenuBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentMenuBottomSheetBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnBack.setOnClickListener(v -> dismiss());

        // Initialize data
        List<String> menuFoodName = Arrays.asList("Burger", "Sandwich", "Momo", "Pizza", "Sandwich", "Momo");
        List<String> menuItemPrice = Arrays.asList("₹50", "₹50", "₹50", "₹50", "₹50", "₹50");
        List<Integer> menuImage = Arrays.asList(
                R.drawable.menu1,
                R.drawable.menu2,
                R.drawable.menu3,
                R.drawable.menu4,
                R.drawable.menu5,
                R.drawable.menu6
        );

        // Set up RecyclerView
        MenuAdapter adapter = new MenuAdapter(new ArrayList<>(menuFoodName), new ArrayList<>(menuItemPrice), new ArrayList<>(menuImage));
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.menuRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
