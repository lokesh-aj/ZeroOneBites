package com.example.grabit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Model.SummerItem;
import com.example.grabit.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SummerAdapter extends RecyclerView.Adapter<SummerAdapter.SummerViewHolder> {

    private List<SummerItem> summerItems;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAddToCartClick(SummerItem item);
    }

    public SummerAdapter(Context context, List<SummerItem> summerItems, OnItemClickListener listener) {
        this.context = context;
        this.summerItems = summerItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SummerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_summer, parent, false);
        return new SummerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummerViewHolder holder, int position) {
        SummerItem item = summerItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return summerItems.size();
    }

    class SummerViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView rating;
        private TextView orders;
        private MaterialButton addToCartButton;

        public SummerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            rating = itemView.findViewById(R.id.rating);
            orders = itemView.findViewById(R.id.orders);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        void bind(SummerItem item) {
            itemName.setText(item.getItemName());
            rating.setText(String.format("%.1f", item.getAverageRating()));
            orders.setText(String.format("(%d orders)", item.getTotalOrders()));

            addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(item);
                }
            });
        }
    }
} 