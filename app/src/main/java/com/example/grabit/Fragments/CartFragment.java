package com.example.grabit.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.CartAdapter;
import com.example.grabit.Model.CartItem;
import com.example.grabit.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalPriceText;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration cartListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        db = FirebaseFirestore.getInstance();

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("sapID", "0");

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, db, userId, this::updateTotalPrice);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();

        return view;
    }

    private void loadCartItems() {
        CollectionReference cartRef = db.collection("Users").document(userId).collection("Cart");

        cartListener = cartRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Cart", "Firestore Error: " + error.getMessage());
                return;
            }

            cartItemList.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                String id = doc.getId();
                String name = doc.getString("name");

                // 🔹 Handling "price" properly
                double price = 0.0;
                Object priceObj = doc.get("price");

                if (priceObj instanceof Number) {
                    price = ((Number) priceObj).doubleValue();
                } else if (priceObj instanceof String) {
                    try {
                        price = Double.parseDouble((String) priceObj);
                    } catch (NumberFormatException e) {
                        Log.e("CartFragment", "Invalid price format: " + priceObj);
                    }
                }

                // 🔹 Handling "image" properly
                String image = "";
                Object imageObj = doc.get("image");
                if (imageObj instanceof String) {
                    image = (String) imageObj;
                } else {
                    Log.e("CartFragment", "Invalid image format: " + imageObj);
                }

                // 🔹 Handling "quantity" properly
                int quantity = 1; // Default quantity
                if (doc.contains("quantity")) {
                    Long qty = doc.getLong("quantity");
                    if (qty != null) {
                        quantity = qty.intValue();
                    }
                }

                cartItemList.add(new CartItem(id, name, price, image, quantity));
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });
    }

    public void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText("Total: ₹" + total);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartListener != null) {
            cartListener.remove();
        }
    }
}
