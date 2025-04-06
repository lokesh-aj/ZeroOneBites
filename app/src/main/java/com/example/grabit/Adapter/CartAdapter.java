package com.example.grabit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grabit.Model.CartItem;
import com.example.grabit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItemList;
    private FirebaseFirestore db;
    private String userId;
    private Runnable updateTotalPriceCallback;

    public CartAdapter(List<CartItem> cartItemList, FirebaseFirestore db, String userId, Runnable updateTotalPriceCallback) {
        this.cartItemList = cartItemList;
        this.db = db;
        this.userId = userId;
        this.updateTotalPriceCallback = updateTotalPriceCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.name.setText(item.getName());
        holder.price.setText("₹" + item.getPrice());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(holder.itemView.getContext()).load(item.getImage()).into(holder.image);

        holder.increaseBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            updateQuantity(item, currentQuantity + 1);
        });

        holder.decreaseBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                updateQuantity(item, currentQuantity - 1);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Cart Item can't go below 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteBtn.setOnClickListener(v -> deleteCartItem(item));
    }

    private void updateQuantity(CartItem item, int newQuantity) {
        db.collection("Users").document(userId)
                .collection("Cart").document(item.getId())
                .update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    item.setQuantity(newQuantity);
                    notifyDataSetChanged();
                    updateTotalPriceCallback.run();
                });
    }

    private void deleteCartItem(CartItem item) {
        db.collection("Users").document(userId)
                .collection("Cart").document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cartItemList.remove(item);
                    notifyDataSetChanged();
                    updateTotalPriceCallback.run();
                });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image;
        ImageButton increaseBtn, decreaseBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
            quantity = itemView.findViewById(R.id.itemQuantity);
            image = itemView.findViewById(R.id.itemImage);
            increaseBtn = itemView.findViewById(R.id.increaseBtn);
            decreaseBtn = itemView.findViewById(R.id.decreaseBtn);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
        }
    }
}