package com.example.grabit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grabit.databinding.CartItemBinding;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<String> cartItems;
    private List<String> cartItemPrice;
    private List<Integer> cartImage;
    private int[] itemQuantity;

    public CartAdapter(List<String> cartItems, List<String> cartItemPrice, List<Integer> cartImage) {
        this.cartItems = cartItems;
        this.cartItemPrice = cartItemPrice;
        this.cartImage = cartImage;
        this.itemQuantity = new int[cartItems.size()];
        for (int i = 0; i < cartItems.size(); i++) {
            itemQuantity[i] = 1;
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartItemBinding binding = CartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final CartItemBinding binding;

        public CartViewHolder(@NonNull CartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            int quantity = itemQuantity[position];
            binding.productName.setText(cartItems.get(position));
            binding.productPrice.setText(cartItemPrice.get(position));
            binding.cartImage.setImageResource(cartImage.get(position));
            binding.cartItemQuantity.setText(String.valueOf(quantity));

            binding.buttonMinus.setOnClickListener(v -> decreaseQuantity(position));
            binding.buttonPlus.setOnClickListener(v -> increaseQuantity(position));
            binding.deleteButton.setOnClickListener(v -> {
                int itemPosition = getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    deleteItem(itemPosition);
                }
            });
        }

        private void decreaseQuantity(int position) {
            if (itemQuantity[position] > 1) {
                itemQuantity[position]--;
                binding.cartItemQuantity.setText(String.valueOf(itemQuantity[position]));
            }
        }

        private void increaseQuantity(int position) {
            if (itemQuantity[position] < 10) {
                itemQuantity[position]++;
                binding.cartItemQuantity.setText(String.valueOf(itemQuantity[position]));
            }
        }

        private void deleteItem(int position) {
            cartItems.remove(position);
            cartImage.remove(position);
            cartItemPrice.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }
}
