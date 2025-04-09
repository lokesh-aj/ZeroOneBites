package com.example.grabit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grabit.Model.PopularItem;
import com.example.grabit.R;
import com.example.grabit.databinding.PopularItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private SharedPreferences sharedPreferences;
    private final Context context;
    private List<Map<String, Object>> foodItems;
    private List<PopularItem> popularItems;

    public PopularAdapter(Context context) {
        this.context = context;
        this.foodItems = new ArrayList<>();
        this.popularItems = new ArrayList<>();
        loadPopularItems();
        fetchFoodItems();
    }

    private void loadPopularItems() {
        try {
            InputStream is = context.getAssets().open("popular_items.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            is.close();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<PopularItem>>() {}.getType();
            popularItems = gson.fromJson(jsonString.toString(), listType);
            


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error loading JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchFoodItems() {
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food Items");
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItems.clear();
                

                
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    try {
                        String itemName = itemSnapshot.child("Item Name").getValue(String.class);
                        if (itemName == null) {
                            continue; // Skip items with null names
                        }
                        
                        // Check if this item is in popular items
                        for (PopularItem popularItem : popularItems) {
                            if (popularItem != null && itemName.equals(popularItem.getItemName())) {
                                Map<String, Object> item = new HashMap<>();
                                item.put("name", itemName);
                                
                                Object price = itemSnapshot.child("Price (INR)").getValue();
                                String imageUrl = itemSnapshot.child("Image URL").getValue(String.class);
                                
                                if (price == null || imageUrl == null) {
                                    continue; // Skip items with missing data
                                }
                                
                                item.put("price", price);
                                item.put("image", imageUrl);
                                item.put("rating", popularItem.getAverageRating());
                                item.put("orders", popularItem.getTotalOrders());
                                foodItems.add(item);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error processing item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                
                // Sort foodItems by rating in descending order
                foodItems.sort((item1, item2) -> {
                    double rating1 = (double) item1.get("rating");
                    double rating2 = (double) item2.get("rating");
                    return Double.compare(rating2, rating1); // Descending order
                });

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Firebase Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularItemBinding binding = PopularItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PopularViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        Map<String, Object> item = foodItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class PopularViewHolder extends RecyclerView.ViewHolder {
        private final PopularItemBinding binding;

        public PopularViewHolder(@NonNull PopularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Map<String, Object> item) {
            binding.foodNamePopular.setText((String) item.get("name"));
            binding.foodPricePopular.setText("₹" + item.get("price"));
            
            // Add rating and orders info
            double rating = (double) item.get("rating");
            int orders = (int) item.get("orders");
            binding.ratingText.setText(String.format("%.1f ★", rating));
            binding.ordersText.setText(orders + " orders");

            // Load image using Glide
            String imageUrl = (String) item.get("image");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(binding.foodImagePopular);
            } else {
                // Set a default image if URL is null or empty
                binding.foodImagePopular.setImageResource(R.drawable.placeholder_image);
            }

            // Handle "Add to Cart" click
            binding.addToCartPopular.setOnClickListener(v -> addToCart(
                    (String) item.get("name"),
                    String.valueOf(item.get("price")),
                    (String) item.get("image")
            ));
        }

        private void addToCart(String itemName, String price, String imageUrl) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("sapID", "0");

            if (userId.equals("0")) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            double priceValue;
            try {
                priceValue = Double.parseDouble(price);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("name", itemName);
            cartItem.put("price", priceValue);
            cartItem.put("image", imageUrl);
            cartItem.put("quantity", 1);

            db.collection("Users").document(userId)
                    .collection("Cart").add(cartItem)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
        }
    }
}
