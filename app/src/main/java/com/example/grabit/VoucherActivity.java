package com.example.grabit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class VoucherActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private Button shareButton;
    private TextView orderIdTextView, orderAmountTextView, orderDateTextView, validityTextView, transactionIdTextView, voucherCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        // Initialize UI elements
        qrCodeImageView = findViewById(R.id.qr_code_image);
        shareButton = findViewById(R.id.btn_share);
        orderIdTextView = findViewById(R.id.tv_order_id);
        orderAmountTextView = findViewById(R.id.tv_order_amount);
        orderDateTextView = findViewById(R.id.tv_order_date);
        validityTextView = findViewById(R.id.tv_validity);
        transactionIdTextView = findViewById(R.id.tv_transaction_id);
        voucherCodeTextView = findViewById(R.id.tv_voucher_code);

        // Get voucher details from UI
        String orderId = orderIdTextView.getText().toString();
        String orderAmount = orderAmountTextView.getText().toString();
        String orderDate = orderDateTextView.getText().toString();
        String validity = validityTextView.getText().toString();
        String transactionId = transactionIdTextView.getText().toString();
        String voucherCode = voucherCodeTextView.getText().toString();

        // Generate QR Code using voucher details
        String qrData = "Order ID: " + orderId + "\n" +
                "Amount: " + orderAmount + "\n" +
                "Date: " + orderDate + "\n" +
                "Validity: " + validity + "\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Voucher Code: " + voucherCode;

        generateQRCode(qrData);

        // Share button click listener
        shareButton.setOnClickListener(v -> shareVoucher("Grabit's Voucher\n\n"+qrData));
    }

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

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void shareVoucher(String qrData) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, qrData);
        startActivity(Intent.createChooser(shareIntent, "Share Voucher"));
    }
}
