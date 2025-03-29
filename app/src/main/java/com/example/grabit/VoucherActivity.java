package com.example.grabit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class VoucherActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private Button shareButton;
    private TextView orderIdTextView, orderAmountTextView, orderDateTextView, validityTextView, transactionIdTextView, voucherCodeTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        qrCodeImageView = findViewById(R.id.qr_code_image);
        shareButton = findViewById(R.id.btn_share);
        orderIdTextView = findViewById(R.id.tv_order_id);
        orderAmountTextView = findViewById(R.id.tv_order_amount);
        orderDateTextView = findViewById(R.id.tv_order_date);
        validityTextView = findViewById(R.id.tv_validity);
        transactionIdTextView = findViewById(R.id.tv_transaction_id);
        voucherCodeTextView = findViewById(R.id.tv_voucher_code);

        // Retrieve the orderId passed from the previous activity
        String orderId = getIntent().getStringExtra("orderId");

        if (orderId != null) {
            // Query Firestore for the voucher details using the orderId
            db.collection("Voucher").document(orderId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve voucher details from the document
                            String fetchedOrderId = documentSnapshot.getString("orderId");
                            String orderAmount = documentSnapshot.getString("orderAmount");
                            String orderDate = documentSnapshot.getString("orderDate");
                            String validity = documentSnapshot.getString("validity");
                            String transactionId = documentSnapshot.getString("transactionId");
                            String voucherCode = documentSnapshot.getString("voucherCode");

                            // Update UI elements with the fetched voucher details
                            orderIdTextView.setText(fetchedOrderId);
                            orderAmountTextView.setText(orderAmount);
                            orderDateTextView.setText(orderDate);
                            validityTextView.setText(validity);
                            transactionIdTextView.setText(transactionId);
                            voucherCodeTextView.setText(voucherCode);

                            // Generate a QR code using the voucher details
                            String qrData = "Order ID: " + fetchedOrderId + "\n" +
                                    "Amount: " + orderAmount + "\n" +
                                    "Date: " + orderDate + "\n" +
                                    "Validity: " + validity + "\n" +
                                    "Transaction ID: " + transactionId + "\n" +
                                    "Voucher Code: " + voucherCode;
                            generateQRCode(qrData);

                            // Set up share button to share voucher details
                            shareButton.setOnClickListener(v -> shareVoucher("Grabit's Voucher\n\n" + qrData));
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error (for example, show an error message)
                        e.printStackTrace();
                    });
        }
    }

    // Method to generate a QR code from the given data string
    private void generateQRCode(String qrData) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrData,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Create the bitmap by iterating through the BitMatrix
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            // Set the bitmap to the ImageView
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    // Method to share the voucher details as plain text
    private void shareVoucher(String qrData) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, qrData);
        startActivity(Intent.createChooser(shareIntent, "Share Voucher"));
    }
}
