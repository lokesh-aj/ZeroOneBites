package com.example.grabit.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Model.GridItem;
import com.example.grabit.R;

import java.util.List;

public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> {
    private Context context;
    private List<GridItem> itemList;

    public GridItemAdapter(Context context, List<GridItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GridItem item = itemList.get(position);
        holder.imageView.setImageResource(item.getImageResource());
        holder.textView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemImage);
            textView = itemView.findViewById(R.id.itemTitle);
            cardView = itemView.findViewById(R.id.itemCard);
        }
    }
}