package com.example.grabit.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.grabit.databinding.PopularItemBinding;
import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private final List<String> items;
    private final List<Integer> images;
    private final List<String> prices;

    public PopularAdapter(List<String> items, List<Integer> images, List<String> prices) {
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

    static class PopularViewHolder extends RecyclerView.ViewHolder {
        private final PopularItemBinding binding;

        public PopularViewHolder(@NonNull PopularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String item, int image, String price) {
            binding.foodNamePopular.setText(item);
            binding.pricePopular.setText(price);
            binding.foodImagePopular.setImageResource(image);
        }
    }
}
