package com.example.sidenav;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button loginButton;
    private TextView tvRegister, tvForgot;
    private FirebaseAuth mAuth;

    private static final String ADMIN_USERNAME = "khalid@gmail.com";
    private static final String ADMIN_PASSWORD = "Skidush143";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_design);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT); // Optional: Transparent status ba

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password1);
        loginButton = findViewById(R.id.loginbutton);
        tvRegister = findViewById(R.id.tv_register);
        tvForgot = findViewById(R.id.tv_register2);

        loginButton.setOnClickListener(view -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
            finish();
        });

        tvForgot.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        if (email.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            Toast.makeText(Login.this, "Admin login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this, MainFragment.class));
            finish();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                        userRef.get().addOnSuccessListener(dataSnapshot -> {
                            String fullName = dataSnapshot.child("name").getValue(String.class);
                            String emailFromDb = dataSnapshot.child("email").getValue(String.class);

                            Intent intent = new Intent(Login.this, MainFragment.class); // send to home screen
                            intent.putExtra("fullName", fullName);
                            intent.putExtra("email", emailFromDb);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Login.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
