package com.example.grabit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.R;
import com.example.grabit.databinding.PopularItemBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private SharedPreferences sharedPreferences;
    private final List<String> items;
    private final List<Integer> images;
    private final List<String> prices;
    private final Context context;

    public PopularAdapter(Context context, List<String> items, List<Integer> images, List<String> prices) {
        this.context = context;
        this.items = items;
        this.images = images;
        this.prices = prices;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularItemBinding binding = PopularItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PopularViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        String item = items.get(position);
        int itemImage = images.get(position);
        String itemPrice = prices.get(position);
        holder.bind(item, itemImage, itemPrice);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PopularViewHolder extends RecyclerView.ViewHolder {
        private final PopularItemBinding binding;

        public PopularViewHolder(@NonNull PopularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String item, int image, String price) {
            binding.foodNamePopular.setText(item);
            binding.pricePopular.setText(price);
            binding.foodImagePopular.setImageResource(image);

            // Handle "Add to Cart" click
            binding.addToCartPopular.setOnClickListener(v -> addToCart(item, image, price));
        }

        private String getImageUrlFromResource(int imageResId) {
            if (imageResId == R.drawable.menu1) {
                return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
            } else if (imageResId == R.drawable.menu2) {
                return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
            }
            else if (imageResId == R.drawable.menu3) {
                return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
            }
            else if (imageResId == R.drawable.menu4) {
                return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
            }
            else if (imageResId == R.drawable.menu5) {
                return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
            }
            else if (imageResId == R.drawable.menu6) {
                return "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_660/FOOD_CATALOG/IMAGES/CMS/2024/10/28/d8c070a1-31e6-4f67-a2a4-f2201079d410_86a01a3a-e97f-4c70-9cfd-4cd96102e5f0.jpg";
            }else {
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




    }
}
