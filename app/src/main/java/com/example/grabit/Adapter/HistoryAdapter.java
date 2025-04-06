package com.example.grabit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Model.Voucher;
import com.example.grabit.R;
import com.example.grabit.VoucherActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Voucher> voucherList;
    private Context context;
    private FirebaseFirestore db;

    public HistoryAdapter(List<Voucher> voucherList, Context context) {
        this.voucherList = voucherList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.orderIdText.setText("Order ID: " + voucher.getOrderId());
        holder.orderDateText.setText(voucher.getOrderDate());
        holder.orderAmountText.setText("₹" + voucher.getOrderAmount());
        holder.voucherCodeText.setText("Voucher Code: " + voucher.getVoucherCode());

        // Set click listener on the card
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VoucherActivity.class);
            intent.putExtra("orderId", voucher.getOrderId());
            intent.putExtra("totalAmount", voucher.getOrderAmount());
            intent.putExtra("orderDate", voucher.getOrderDate());
            intent.putExtra("validity", voucher.getValidity());
            intent.putExtra("transactionId", voucher.getTransactionId());
            intent.putExtra("voucherCode", voucher.getVoucherCode());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public void deleteItem(int position) {
        Voucher voucher = voucherList.get(position);
        db.collection("Voucher")
                .document(voucher.getOrderId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    voucherList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Voucher deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting voucher: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void shareItem(int position) {
        Voucher voucher = voucherList.get(position);
        String shareText = String.format("Grabit's Voucher\n\nOrder ID: %s\nAmount: %s\nDate: %s\nValidity: %s\nTransaction ID: %s\nVoucher Code: %s",
                voucher.getOrderId(),
                "₹" + voucher.getOrderAmount(),
                voucher.getOrderDate(),
                voucher.getValidity(),
                voucher.getTransactionId(),
                voucher.getVoucherCode());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, "Share Voucher"));
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView orderIdText;
        TextView orderDateText;
        TextView orderAmountText;
        TextView voucherCodeText;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            orderIdText = itemView.findViewById(R.id.tv_order_id);
            orderDateText = itemView.findViewById(R.id.tv_order_date);
            orderAmountText = itemView.findViewById(R.id.tv_order_amount);
            voucherCodeText = itemView.findViewById(R.id.tv_voucher_code);
        }
    }
} 