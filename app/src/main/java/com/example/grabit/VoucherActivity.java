package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class VoucherActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderAmount, tvOrderDate, tvValidity, tvTransactionId, tvVoucherCode;
    private ImageView qrCodeImage;
    private Button shareButton, historyButton;
    private ImageButton backButton;
    private FirebaseFirestore db;
    private String userId;
    private boolean isFromHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("sapID", "0");

        // Initialize views
        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderAmount = findViewById(R.id.tv_order_amount);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvValidity = findViewById(R.id.tv_validity);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvVoucherCode = findViewById(R.id.tv_voucher_code);
        qrCodeImage = findViewById(R.id.qr_code_image);
        shareButton = findViewById(R.id.btn_share);
        historyButton = findViewById(R.id.btn_history);
        backButton = findViewById(R.id.btn_back);

        // Set up back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up history button
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Dashboard.class);
            intent.putExtra("fragment", "history");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Check if opened from history
        if (getIntent().hasExtra("orderDate")) {
            isFromHistory = true;
            // Get all data from intent
            String orderId = getIntent().getStringExtra("orderId");
            String totalAmount = getIntent().getStringExtra("totalAmount");
            String orderDate = getIntent().getStringExtra("orderDate");
            String validity = getIntent().getStringExtra("validity");
            String transactionId = getIntent().getStringExtra("transactionId");
            String voucherCode = getIntent().getStringExtra("voucherCode");

            // Update UI with existing data
            tvOrderId.setText("Order ID: " + orderId);
            tvOrderAmount.setText("₹" + totalAmount);
            tvOrderDate.setText(orderDate);
            tvValidity.setText(validity);
            tvTransactionId.setText(transactionId);
            tvVoucherCode.setText(voucherCode);

            // Generate QR code
            generateQRCode(orderId, totalAmount, orderDate, validity, transactionId, voucherCode);
        } else {
            // Get data from intent for new voucher
            String orderId = getIntent().getStringExtra("orderId");
            double totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);

            // Generate voucher details
            String orderDate = getCurrentDate();
            String validity = getValidityDate();
            String transactionId = generateTransactionId();
            String voucherCode = generateVoucherCode();

            // Update UI
            tvOrderId.setText("Order ID: " + orderId);
            tvOrderAmount.setText("₹" + String.format("%.2f", totalAmount));
            tvOrderDate.setText(orderDate);
            tvValidity.setText(validity);
            tvTransactionId.setText(transactionId);
            tvVoucherCode.setText(voucherCode);

            // Generate and display QR code
            generateQRCode(orderId, String.valueOf(totalAmount), orderDate, validity, transactionId, voucherCode);

            // Save voucher data to Firestore
            saveVoucherToFirestore(orderId, String.valueOf(totalAmount), orderDate, validity, transactionId, voucherCode);
        }

        // Set up share button
        shareButton.setOnClickListener(v -> {
            String shareText = String.format("Grabit's Voucher\n\nOrder ID: %s\nAmount: %s\nDate: %s\nValidity: %s\nTransaction ID: %s\nVoucher Code: %s",
                    tvOrderId.getText().toString().replace("Order ID: ", ""),
                    tvOrderAmount.getText().toString(),
                    tvOrderDate.getText().toString(),
                    tvValidity.getText().toString(),
                    tvTransactionId.getText().toString(),
                    tvVoucherCode.getText().toString().replace("Voucher Code: ", ""));
            shareVoucher(shareText);
        });
    }

    private void saveVoucherToFirestore(String orderId, String totalAmount, String orderDate, String validity, String transactionId, String voucherCode) {
        Map<String, Object> voucherData = new HashMap<>();
        voucherData.put("orderId", orderId);
        voucherData.put("orderAmount", totalAmount);
        voucherData.put("orderDate", orderDate);
        voucherData.put("validity", validity);
        voucherData.put("transactionId", transactionId);
        voucherData.put("voucherCode", voucherCode);
        voucherData.put("userId", userId);

        db.collection("Voucher")
                .document(orderId)
                .set(voucherData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Voucher saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving voucher: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void generateQRCode(String orderId, String totalAmount, String orderDate, String validity, String transactionId, String voucherCode) {
        String qrData = String.format("Order ID: %s\nAmount: %s\nDate: %s\nValidity: %s\nTransaction ID: %s\nVoucher Code: %s",
                orderId, totalAmount, orderDate, validity, transactionId, voucherCode);

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("dd MMM, yyyy, hh:mm a", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    private String getValidityDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 30);
        return new java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault())
                .format(calendar.getTime());
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    private String generateVoucherCode() {
        return "GR" + System.currentTimeMillis();
    }

    private void shareVoucher(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share Voucher"));
    }
}
