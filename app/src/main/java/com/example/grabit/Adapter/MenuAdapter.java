package com.example.grabit.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grabit.R;
import com.example.grabit.databinding.MenuItemBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<String> menuItemName;
    private List<String> menuItemPrice;
    private List<Integer> menuItemImage;
    private Context context;
    private SharedPreferences sharedPreferences;

    public MenuAdapter(Context context, List<String> menuItemName, List<String> menuItemPrice, List<Integer> menuItemImage) {
        this.context = context;
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
        this.menuItemImage = menuItemImage;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuItemBinding binding = MenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return menuItemName.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private final MenuItemBinding binding;

        public MenuViewHolder(@NonNull MenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.menuFoodName.setText(menuItemName.get(position));
            binding.menuPrice.setText(menuItemPrice.get(position));
            binding.menuImage.setImageResource(menuItemImage.get(position));

            // Use the correct view from the layout: menuAddToCart
            binding.menuAddToCart.setOnClickListener(v -> addToCart(position));
        }

        private void addToCart(int position) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("sapID", "0");

            if (userId.equals("0")) {
                showLoginDialog();
                return;
            }

            double priceValue;
            try {
                priceValue = Double.parseDouble(menuItemPrice.get(position).replace("₹", "").trim());
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("name", menuItemName.get(position));
            cartItem.put("price", priceValue);
            cartItem.put("image", getImageUrlFromResource(menuItemImage.get(position)));
            cartItem.put("quantity", 1);
            cartItem.put("timestamp", System.currentTimeMillis());

            db.collection("Users").document(userId)
                    .collection("Cart").add(cartItem)
                    .addOnSuccessListener(documentReference -> {
                        // Item added successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                        Log.e("Cart", "Error adding item to cart: " + e.getMessage());
                    });
        }

        private String getImageUrlFromResource(int imageResId) {
            if (imageResId == R.drawable.menu1) {
                return "https://example.com/menu1.jpg";
            } else if (imageResId == R.drawable.menu2) {
                return "https://example.com/menu2.jpg";
            } else {
                return "https://example.com/default.jpg";
            }
        }

        private void showLoginDialog() {
            // Implementation of showLoginDialog method
        }
    }
}