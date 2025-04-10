package com.example.grabit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grabit.LoginActivity;
import com.example.grabit.Model.FoodItem;
import com.example.grabit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> {
    private Context context;
    private List<FoodItem> foodItems;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    public FoodItemAdapter(Context context, List<FoodItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
        this.sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);

        holder.foodNameText.setText(item.getName());
        holder.priceText.setText("₹" + item.getPrice());

        // Load image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.foodImage);

        // Add to Cart button click listener
        holder.addToCartButton.setOnClickListener(v -> addToCart(item));
    }

    private void addToCart(FoodItem foodItem) {
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
        cartItem.put("name", foodItem.getName());
        cartItem.put("price", foodItem.getPrice());
        cartItem.put("image", foodItem.getImageUrl());
        cartItem.put("quantity", 1);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Login Required")
                .setMessage("Please login to add items to cart")
                .setPositiveButton("Login", (dialog, which) -> {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodNameText;
        TextView priceText;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodNameText = itemView.findViewById(R.id.foodNameText);
            priceText = itemView.findViewById(R.id.priceText);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
} 