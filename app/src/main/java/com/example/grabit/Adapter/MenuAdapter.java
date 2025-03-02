package com.example.grabit.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grabit.databinding.MenuItemBinding;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<String> menuItemName;
    private List<String> menuItemPrice;
    private List<Integer> menuItemImage;

    public MenuAdapter(List<String> menuItemName, List<String> menuItemPrice, List<Integer> menuItemImage) {
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
        }
    }
}
