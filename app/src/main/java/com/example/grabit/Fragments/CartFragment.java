package com.example.grabit.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.grabit.Adapter.CartAdapter;
import com.example.grabit.R;
import com.example.grabit.databinding.FragmentCartBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        List<String> cartFoodName = Arrays.asList("Burger", "Sandwich", "Momo", "Pizza", "Sandwich", "Momo");
        List<String> cartItemPrice = Arrays.asList("₹50", "₹50", "₹50", "₹50", "₹50", "₹50");
        List<Integer> cartImage = Arrays.asList(
                R.drawable.menu1,
                R.drawable.menu2,
                R.drawable.menu3,
                R.drawable.menu4,
                R.drawable.menu5,
                R.drawable.menu6
        );

        CartAdapter adapter = new CartAdapter(new ArrayList<>(cartFoodName), new ArrayList<>(cartItemPrice), new ArrayList<>(cartImage));
        binding.carRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.carRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }
}
