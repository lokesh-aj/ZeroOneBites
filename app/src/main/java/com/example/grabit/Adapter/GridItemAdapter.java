package com.example.grabit.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Model.GridItem;
import com.example.grabit.R;

import java.util.List;

public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> {
    private Context context;
    private List<GridItem> items;

    public GridItemAdapter(Context context, List<GridItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GridItem item = items.get(position);
        holder.imageView.setImageResource(item.getImageResource());
        holder.textView.setText(item.getName());

        holder.cardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("categoryName", item.getName());
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_categoryItemsFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryIcon);
            textView = itemView.findViewById(R.id.categoryName);
            cardView = itemView.findViewById(R.id.categoryCard);
        }
    }
}