package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RedeemedAdapter extends RecyclerView.Adapter<RedeemedAdapter.RedeemedViewHolder> {
    private List<Map<String, Object>> redeemedVouchers;
    private Context context;
    private String userId;
    private FirebaseFirestore db;

    public RedeemedAdapter(List<Map<String, Object>> redeemedVouchers, Context context, String userId) {
        this.redeemedVouchers = redeemedVouchers;
        this.context = context;
        this.userId = userId;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RedeemedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_redeemed_voucher, parent, false);
        return new RedeemedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RedeemedViewHolder holder, int position) {
        Map<String, Object> voucher = redeemedVouchers.get(position);
        holder.tvOrderId.setText("Order ID: " + voucher.get("orderId"));
        holder.tvAmount.setText("Amount: ₹" + voucher.get("orderAmount"));
        holder.tvDate.setText("Date: " + voucher.get("orderDate"));
        holder.tvRedeemedDate.setText("Redeemed: " + voucher.get("redeemedDate"));

        holder.btnRepeatOrder.setOnClickListener(v -> {
            String orderId = (String) voucher.get("orderId");
            repeatOrder(orderId);
        });
    }

    @Override
    public int getItemCount() {
        return redeemedVouchers.size();
    }

    private void repeatOrder(String orderId) {
        // First, get the original order details
        db.collection("Orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the items from the order
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");
                        if (items != null && !items.isEmpty()) {
                            // Add each item to the cart
                            for (Map<String, Object> item : items) {
                                addToCart(item);
                            }
                            Toast.makeText(context, "Items added to cart", Toast.LENGTH_SHORT).show();
                            // Navigate to cart
                            Intent intent = new Intent(context, Dashboard.class);
                            intent.putExtra("fragment", "cart");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "No items found in the order", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Order not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error repeating order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addToCart(Map<String, Object> item) {
        // Get current user's SAP ID from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String sapId = sharedPreferences.getString("sapID", "0");

        if (sapId.equals("0")) {
            // If user is not logged in, show login dialog
            showLoginDialog();
            return;
        }

        // Create a new cart item
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", item.get("name"));
        cartItem.put("price", item.get("price"));
        cartItem.put("image", item.get("image"));
        cartItem.put("quantity", item.get("quantity"));
        cartItem.put("timestamp", System.currentTimeMillis());

        // Add to Firestore
        db.collection("Users")
                .document(sapId)
                .collection("Cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    // Item added successfully
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("Cart", "Error adding item to cart: " + e.getMessage());
                });
    }

    private void showLoginDialog() {
        // Implementation of showLoginDialog method
    }

    static class RedeemedViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvAmount, tvDate, tvRedeemedDate;
        Button btnRepeatOrder;

        RedeemedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRedeemedDate = itemView.findViewById(R.id.tv_redeemed_date);
            btnRepeatOrder = itemView.findViewById(R.id.btn_repeat_order);
        }
    }
} 