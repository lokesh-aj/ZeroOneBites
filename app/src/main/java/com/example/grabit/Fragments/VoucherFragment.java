package com.example.grabit.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.grabit.R;
import com.example.grabit.databinding.FragmentVoucherBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.FirebaseFirestore;

public class VoucherFragment extends Fragment {

    private static final String TAG = "VoucherFragment";
    private FragmentVoucherBinding binding;
    private DatabaseReference voucherRef;
    private ValueEventListener voucherListener;
    private View statusIndicator;
    private TextView orderStatus;
    private String voucherId;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVoucherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize views
        statusIndicator = root.findViewById(R.id.statusIndicator);
        orderStatus = root.findViewById(R.id.orderStatus);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        voucherId = getArguments() != null ? getArguments().getString("orderId") : null;
        if (voucherId == null) {
            Toast.makeText(getContext(), "Voucher ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set voucher ID
        binding.voucherId.setText("Voucher ID: " + voucherId);

        // Generate QR code
        generateQRCode(voucherId);

        // Initialize Firebase references
        voucherRef = FirebaseDatabase.getInstance().getReference("Vouchers").child(voucherId);
        
        // Listen for voucher status changes
        voucherListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get all voucher data
                    String status = dataSnapshot.child("status").getValue(String.class);
                    String date = dataSnapshot.child("orderDate").getValue(String.class);
                    String amount = dataSnapshot.child("orderAmount").getValue(String.class);

                    Log.d(TAG, "Received data from Firebase - Status: " + status 
                            + ", Date: " + date 
                            + ", Amount: " + amount);

                    // Update status immediately when changed in Firebase
                    if (status != null) {
                        Log.d(TAG, "Status changed in Firebase to: " + status);
                        updateStatusUI(status);
                    }

                    // Update date
                    if (date != null) {
                        binding.orderDate.setText("Order Date: " + date);
                    }

                    // Update amount
                    if (amount != null) {
                        binding.orderAmount.setText("Amount: RM " + amount);
                    } else {
                        binding.orderAmount.setText("Amount: Not available");
                    }

                    // Make sure the views are visible
                    binding.voucherCard.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "No voucher data found in Firebase for ID: " + voucherId);
                    // If voucher doesn't exist, create it with pending status
                    Map<String, Object> voucherData = new HashMap<>();
                    voucherData.put("status", "pending");
                    voucherData.put("orderDate", java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
                    voucherData.put("orderAmount", "0.00");
                    voucherRef.setValue(voucherData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase error: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Error loading voucher status", Toast.LENGTH_SHORT).show();
            }
        };

        // Add the listener and ensure it stays active
        voucherRef.addValueEventListener(voucherListener);
    }

    private void updateStatusUI(String status) {
        if (status == null) {
            status = "pending";
        }

        Log.d(TAG, "Updating UI for status: " + status);
        
        switch (status.toLowerCase()) {
            case "collected":
                orderStatus.setText("COLLECTED");
                orderStatus.setTextColor(getResources().getColor(R.color.green));
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_collected);
                break;
            case "received":
                orderStatus.setText("RECEIVED");
                orderStatus.setTextColor(getResources().getColor(R.color.green));
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_collected);
                break;
            case "pending":
                orderStatus.setText("PENDING COLLECTION");
                orderStatus.setTextColor(getResources().getColor(R.color.orange));
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_pending);
                break;
            default:
                Log.d(TAG, "Unknown status received: " + status);
                orderStatus.setText("PENDING COLLECTION");
                orderStatus.setTextColor(getResources().getColor(R.color.orange));
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_pending);
                break;
        }
    }

    private void updateVoucherStatus(String newStatus) {
        if (voucherId == null) {
            Log.e(TAG, "Voucher ID is null");
            return;
        }

        // Update status in Firestore
        db.collection("Voucher").document(voucherId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Update status in Realtime Database
                    DatabaseReference voucherRef = FirebaseDatabase.getInstance()
                            .getReference("Vouchers")
                            .child(voucherId);
                    
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", newStatus);
                    updates.put("lastUpdated", ServerValue.TIMESTAMP);
                    
                    voucherRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d(TAG, "Status updated successfully to: " + newStatus);
                                updateStatusUI(newStatus);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update status in Realtime DB: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update status in Firestore: " + e.getMessage());
                });
    }

    private void generateQRCode(String voucherId) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(voucherId, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            binding.qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(getContext(), "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (voucherRef != null && voucherListener != null) {
            voucherRef.removeEventListener(voucherListener);
        }
        binding = null;
        statusIndicator = null;
        orderStatus = null;
    }
} 