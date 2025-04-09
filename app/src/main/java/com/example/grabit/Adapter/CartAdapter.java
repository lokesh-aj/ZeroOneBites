package com.example.grabit.Adapter;

import android.content.Context;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        holder.price.setText("₹" + String.format("%.2f", item.getPrice()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(holder.itemView.getContext())
            .load(item.getImage())
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(holder.image);

        holder.increaseBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity < 10) { // Maximum quantity limit
                updateQuantity(item, currentQuantity + 1, holder.itemView.getContext());
            } else {
                Toast.makeText(holder.itemView.getContext(), "Maximum quantity limit reached", Toast.LENGTH_SHORT).show();
            }
        });

        holder.decreaseBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                updateQuantity(item, currentQuantity - 1, holder.itemView.getContext());
            } else {
                // If quantity is 1, ask user if they want to remove the item
                Toast.makeText(holder.itemView.getContext(), "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteBtn.setOnClickListener(v -> deleteCartItem(item, holder.itemView.getContext()));
    }

    private void updateQuantity(CartItem item, int newQuantity, Context context) {
        if (newQuantity < 1 || newQuantity > 10) {
            return; // Invalid quantity
        }

        // Find all documents with the same name and update their quantities
        db.collection("Users").document(userId)
                .collection("Cart")
                .whereEqualTo("name", item.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Calculate how much to increase/decrease each document
                    int totalCurrentQuantity = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long qty = doc.getLong("quantity");
                        if (qty != null) {
                            totalCurrentQuantity += qty.intValue();
                        }
                    }
                    
                    // Calculate the difference to distribute
                    int difference = newQuantity - totalCurrentQuantity;
                    
                    if (difference == 0) {
                        // No change needed
                        item.setQuantity(newQuantity);
                        notifyDataSetChanged();
                        updateTotalPriceCallback.run();
                        return;
                    }
                    
                    // Distribute the difference across documents
                    int documentsCount = queryDocumentSnapshots.size();
                    if (documentsCount == 0) {
                        // No documents found, something went wrong
                        Toast.makeText(context, "Error updating quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Calculate base change per document
                    int baseChange = difference / documentsCount;
                    int remainder = difference % documentsCount;
                    
                    // Update each document
                    int updatedCount = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long currentQty = doc.getLong("quantity");
                        if (currentQty != null) {
                            int newQty = currentQty.intValue() + baseChange;
                            // Distribute remainder one by one
                            if (remainder > 0) {
                                newQty++;
                                remainder--;
                            }
                            
                            // Update the document
                            doc.getReference().update("quantity", newQty);
                            updatedCount++;
                        }
                    }
                    
                    // Update the local item
                    item.setQuantity(newQuantity);
                    notifyDataSetChanged();
                    updateTotalPriceCallback.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteCartItem(CartItem item, Context context) {
        // Delete all items with the same name
        db.collection("Users").document(userId)
                .collection("Cart")
                .whereEqualTo("name", item.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    
                    // Remove the item from the local list
                    cartItemList.remove(item);
                    notifyDataSetChanged();
                    updateTotalPriceCallback.run();
                    Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show();
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