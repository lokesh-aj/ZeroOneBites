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
import android.widget.LinearLayout;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration cartListener;
    private Button button2;
    private LinearLayout emptyCartLayout;
    private Button btnContinueShopping;
    private View summaryCard;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cartRecyclerView);
        db = FirebaseFirestore.getInstance();

        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("sapID", "0");

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, db, userId, this::updateTotalPrice);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);

        button2 = view.findViewById(R.id.button2);
        button2.setOnClickListener(v -> proceedToPayment());

        emptyCartLayout = view.findViewById(R.id.emptyCartLayout);
        btnContinueShopping = view.findViewById(R.id.btnContinueShopping);
        summaryCard = view.findViewById(R.id.summaryCard);

        btnContinueShopping.setOnClickListener(v -> {
            // Navigate to home fragment
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        loadCartItems();

        return view;
    }

    private void loadCartItems() {
        if (userId.equals("0")) {
            showEmptyCart();
            return;
        }

        CollectionReference cartRef = db.collection("Users").document(userId).collection("Cart");

        cartListener = cartRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Cart", "Firestore Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error loading cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (value == null || value.isEmpty()) {
                showEmptyCart();
                return;
            }

            // Clear the current list
            cartItemList.clear();
            
            // Map to track items by name for combining
            Map<String, CartItem> combinedItems = new HashMap<>();
            
            // Process each document from Firestore
            for (DocumentSnapshot doc : value.getDocuments()) {
                String id = doc.getId();
                String name = doc.getString("name");

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

                String image = "";
                Object imageObj = doc.get("image");
                if (imageObj instanceof String) {
                    image = (String) imageObj;
                } else {
                    Log.e("CartFragment", "Invalid image format: " + imageObj);
                }

                int quantity = 1;
                if (doc.contains("quantity")) {
                    Long qty = doc.getLong("quantity");
                    if (qty != null) {
                        quantity = qty.intValue();
                    }
                }

                // If we already have this item, combine quantities
                if (combinedItems.containsKey(name)) {
                    CartItem existingItem = combinedItems.get(name);
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                } else {
                    // First time seeing this item
                    CartItem cartItem = new CartItem(id, name, price, image, quantity);
                    combinedItems.put(name, cartItem);
                }
            }
            
            // Add all combined items to the cart list
            cartItemList.addAll(combinedItems.values());
            
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
            
            if (cartItemList.isEmpty()) {
                showEmptyCart();
            } else {
                showCartItems();
            }
        });
    }

    private void showEmptyCart() {
        recyclerView.setVisibility(View.GONE);
        emptyCartLayout.setVisibility(View.VISIBLE);
        summaryCard.setVisibility(View.GONE);
    }

    private void showCartItems() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyCartLayout.setVisibility(View.GONE);
        summaryCard.setVisibility(View.VISIBLE);
    }

    private void proceedToPayment() {
        if (cartItemList.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        double tempTotal = 0;
        for (CartItem item : cartItemList) {
            tempTotal += item.getPrice() * item.getQuantity();
        }
        final double total = tempTotal;
        String orderId = db.collection("Orders").document().getId();
        final String finalOrderId = orderId;

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", finalOrderId);
        orderData.put("userId", userId);
        orderData.put("totalAmount", total);
        orderData.put("timestamp", FieldValue.serverTimestamp());

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

        // Show loading indicator
        button2.setEnabled(false);
        button2.setText("Processing...");

        db.collection("Orders").document(finalOrderId)
                .set(orderData)
                .addOnSuccessListener(aVoid -> {
                    // Generate current date and time
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy, hh:mm a", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    
                    // Generate validity date (30 days from now)
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, 30);
                    String validityDate = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                            .format(calendar.getTime());
                    
                    // Generate transaction ID and voucher code
                    String transactionId = "TXN" + System.currentTimeMillis();
                    String voucherCode = "GR" + System.currentTimeMillis();

                    Map<String, Object> voucherData = new HashMap<>();
                    voucherData.put("orderId", finalOrderId);
                    voucherData.put("userId", userId);
                    voucherData.put("orderAmount", String.valueOf(total));
                    voucherData.put("orderDate", currentDate);
                    voucherData.put("validity", validityDate);
                    voucherData.put("transactionId", transactionId);
                    voucherData.put("voucherCode", voucherCode);

                    db.collection("Voucher").document(finalOrderId)
                            .set(voucherData)
                            .addOnSuccessListener(aVoid1 -> {
                                clearCartAndProceed(finalOrderId, total);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CartFragment", "Failed to create voucher: " + e.getMessage());
                                Toast.makeText(getContext(), "Error creating voucher", Toast.LENGTH_SHORT).show();
                                button2.setEnabled(true);
                                updateTotalPrice();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("CartFragment", "Failed to create order: " + e.getMessage());
                    Toast.makeText(getContext(), "Error creating order", Toast.LENGTH_SHORT).show();
                    button2.setEnabled(true);
                    updateTotalPrice();
                });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        button2.setText("Place Order - ₹" + String.format("%.2f", total));
    }

    private void clearCartAndProceed(String orderId, double total) {
        WriteBatch batch = db.batch();
        db.collection("Users").document(userId).collection("Cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit().addOnSuccessListener(aVoid1 -> {
                        Intent intent = new Intent(getActivity(), PaymentConfirmationActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("totalAmount", total);
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Log.e("CartFragment", "Failed to clear cart items: " + e.getMessage());
                        Toast.makeText(getContext(), "Error clearing cart", Toast.LENGTH_SHORT).show();
                        button2.setEnabled(true);
                        updateTotalPrice();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("CartFragment", "Failed to get cart items for deletion: " + e.getMessage());
                    Toast.makeText(getContext(), "Error processing order", Toast.LENGTH_SHORT).show();
                    button2.setEnabled(true);
                    updateTotalPrice();
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartListener != null) {
            cartListener.remove();
        }
    }
}
