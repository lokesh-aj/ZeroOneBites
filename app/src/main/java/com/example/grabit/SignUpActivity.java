package com.example.grabit;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class SignUpActivity extends AppCompatActivity {

    private EditText etSapId, etPassword;
    private Button btnRegister;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        etSapId = findViewById(R.id.sap_id);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.button_register);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String sapId = etSapId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(sapId)) {
            Toast.makeText(this, "Enter SAP ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sapId.length() != 11) {
            Toast.makeText(this, "SAP ID must be 11 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if SAP ID already exists
        databaseReference.child(sapId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SignUpActivity.this, "SAP ID already registered", Toast.LENGTH_SHORT).show();
                } else {
                    // Save user data to Firebase Realtime Database
                    databaseReference.child(sapId).child("password").setValue(password)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                // Navigate to login or home screen after registration
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SignUpActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUpActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
