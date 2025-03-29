package com.example.grabit.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.CartAdapter;
import com.example.grabit.Model.CartItem;
import com.example.grabit.PaymentConfirmationActivity;
import com.example.grabit.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Button proceedToPayButton = view.findViewById(R.id.button2);
        proceedToPayButton.setOnClickListener(v -> proceedToPayment());

        loadCartItems();

        return view;
    }

    // This listener now merges items with the same name (or use a unique product identifier)
    private void loadCartItems() {
        CollectionReference cartRef = db.collection("Users").document(userId).collection("Cart");

        cartListener = cartRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Cart", "Firestore Error: " + error.getMessage());
                return;
            }

            // Merge duplicate items by using product name as key
            Map<String, CartItem> mergedItems = new HashMap<>();

            for (DocumentSnapshot doc : value.getDocuments()) {
                String id = doc.getId();
                String name = doc.getString("name");

                // Handle price
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

                // Handle image
                String image = "";
                Object imageObj = doc.get("image");
                if (imageObj instanceof String) {
                    image = (String) imageObj;
                } else {
                    Log.e("CartFragment", "Invalid image format: " + imageObj);
                }

                // Handle quantity
                int quantity = 1;
                if (doc.contains("quantity")) {
                    Long qty = doc.getLong("quantity");
                    if (qty != null) {
                        quantity = qty.intValue();
                    }
                }

                // Merge items – if an item with the same name already exists, sum their quantities.
                if (mergedItems.containsKey(name)) {
                    CartItem existingItem = mergedItems.get(name);
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                } else {
                    mergedItems.put(name, new CartItem(id, name, price, image, quantity));
                }
            }
            cartItemList.clear();
            cartItemList.addAll(mergedItems.values());
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });
    }

    private void proceedToPayment() {
        double tempTotal = 0;
        for (CartItem item : cartItemList) {
            tempTotal += item.getPrice() * item.getQuantity();
        }
        final double total = tempTotal;

        // Generate a unique order ID using Firestore's automatic ID
        String orderId = db.collection("Orders").document().getId();
        final String finalOrderId = orderId;

        // Prepare the order data
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", finalOrderId);
        orderData.put("userId", userId);
        orderData.put("totalAmount", total);
        orderData.put("timestamp", FieldValue.serverTimestamp());

        // Build a list of order items from cart items
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", item.getId());
            itemData.put("name", item.getName());
            itemData.put("price", item.getPrice());
            itemData.put("image", item.getImage());
            itemData.put("quantity", item.getQuantity());
            orderItems.add(itemData);
        }
        orderData.put("items", orderItems);

        // Create the order in the "Orders" collection
        db.collection("Orders").document(finalOrderId)
                .set(orderData)
                .addOnSuccessListener(aVoid -> {
                    // After order creation, clear the cart using a batch deletion
                    WriteBatch batch = db.batch();
                    db.collection("Users").document(userId).collection("Cart")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    batch.delete(doc.getReference());
                                }
                                batch.commit().addOnSuccessListener(aVoid1 -> {
                                    // Proceed to the PaymentConfirmationActivity
                                    Intent intent = new Intent(getActivity(), PaymentConfirmationActivity.class);
                                    intent.putExtra("totalAmount", total);
                                    intent.putExtra("orderId", finalOrderId);
                                    startActivity(intent);
                                }).addOnFailureListener(e -> {
                                    Log.e("CartFragment", "Failed to clear cart items: " + e.getMessage());
                                });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CartFragment", "Failed to get cart items for deletion: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("CartFragment", "Failed to create order: " + e.getMessage());
                });
    }

    public void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.valueOf(total));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartListener != null) {
            cartListener.remove();
        }
    }
}
