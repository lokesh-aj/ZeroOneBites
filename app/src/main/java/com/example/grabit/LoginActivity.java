package com.example.grabit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etSapId;
    private EditText etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    public void goToActivity() {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        finish();  // Finish login activity to prevent going back to it
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        etSapId = findViewById(R.id.sapIdEditText);
        etPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String sapId = etSapId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(sapId) || sapId.length() != 11) {
            Toast.makeText(this, "Enter a valid SAP ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if SAP ID exists in Realtime Database
        databaseReference.child(sapId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedPassword = snapshot.child("password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // Save login state in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("logged", true);
                        editor.putString("sapID", sapId);  // Storing a String value
                        // Get and save user's name from the database
                        String userName = snapshot.child("name").getValue(String.class);
                        if (userName != null) {
                            editor.putString("userName", userName);
                        } else {
                            editor.putString("userName", "User");  // Default name if not found
                        }
                        editor.apply();
                        // Save login state in Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("sapId", sapId);

                        db.collection("Users").document(sapId)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    // Proceed to Dashboard
                                    goToActivity();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(LoginActivity.this, "Firestore Error", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "SAP ID not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
