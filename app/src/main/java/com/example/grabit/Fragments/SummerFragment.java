package com.example.grabit.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.SummerAdapter;
import com.example.grabit.Model.SummerItem;
import com.example.grabit.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SummerFragment extends Fragment implements SummerAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private SummerAdapter adapter;
    private List<SummerItem> summerItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summer, container, false);
        
        recyclerView = view.findViewById(R.id.summerRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        summerItems = loadSummerItems();
        adapter = new SummerAdapter(requireContext(), summerItems, this);
        recyclerView.setAdapter(adapter);
        
        return view;
    }

    private List<SummerItem> loadSummerItems() {
        try {
            InputStream is = requireContext().getAssets().open("summer_items.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<List<SummerItem>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void onAddToCartClick(SummerItem item) {
        // TODO: Implement add to cart functionality
        Toast.makeText(getContext(), "Added " + item.getItemName() + " to cart", Toast.LENGTH_SHORT).show();
    }
} 