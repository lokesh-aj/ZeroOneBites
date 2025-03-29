package com.example.grabit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {
    private Context context;
    private ArrayList<String> foodNames;
    private ArrayList<Integer> foodImages;
    private ArrayList<String> prices;
    private SharedPreferences sharedPreferences;

    public PopularAdapter(Context context, ArrayList<String> foodNames, ArrayList<Integer> foodImages, ArrayList<String> prices) {
        this.context = context;
        this.foodNames = foodNames;
        this.foodImages = foodImages;
        this.prices = prices;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popular_item, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        holder.recipeImage.setImageResource(foodImages.get(position));
        holder.recipeName.setText(foodNames.get(position));
        holder.recipePrice.setText(prices.get(position));

        holder.addToCartButton.setOnClickListener(v -> 
            addToCart(foodNames.get(position), foodImages.get(position), prices.get(position)));
    }

    @Override
    public int getItemCount() {
        return foodNames.size();
    }

    private String getImageUrlFromResource(int imageResId) {
        if (imageResId == R.drawable.menu1) {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
        } else if (imageResId == R.drawable.menu2) {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
        } else if (imageResId == R.drawable.menu3) {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
        } else if (imageResId == R.drawable.menu4) {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
        } else if (imageResId == R.drawable.menu5) {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
        } else if (imageResId == R.drawable.menu6) {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
        } else {
            return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/RX_THUMBNAIL/IMAGES/VENDOR/2025/2/20/8000dfb3-f5ad-413e-8165-c4e1d506ff73_855090.jpg";
        }
    }

    private void addToCart(String item, int imageResId, String price) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("sapID", "0");

        if (userId.equals("0")) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert price to double
        double priceValue;
        try {
            priceValue = Double.parseDouble(price.replace("₹", "").trim());  // Remove ₹ symbol if present
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert image resource ID to a URL
        String imageUrl = getImageUrlFromResource(imageResId);

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", item);
        cartItem.put("price", priceValue);  // Store as double
        cartItem.put("image", imageUrl);   // Store image URL instead of resource ID

        db.collection("Users").document(userId)
                .collection("Cart").add(cartItem)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show());
    }

    static class PopularViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName;
        TextView recipePrice;
        TextView addToCartButton;

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipePrice = itemView.findViewById(R.id.recipePrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}