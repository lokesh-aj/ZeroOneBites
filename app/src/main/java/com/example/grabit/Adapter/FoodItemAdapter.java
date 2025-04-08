package com.example.grabit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    private void addToCart(FoodItem item) {
        String userId = sharedPreferences.getString("sapID", "0");
        
        if (!userId.equals("0")) {
            // Convert price to double
            double priceValue;
            try {
                priceValue = Double.parseDouble(item.getPrice());
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("name", item.getName());
            cartItem.put("price", priceValue);
            cartItem.put("image", item.getImageUrl());
            cartItem.put("quantity", 1);

            db.collection("Users").document(userId)
                    .collection("Cart").add(cartItem)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
        }
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