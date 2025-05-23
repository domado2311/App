package com.example.sidenav;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvLoginRedirect, passwordRules;
    ImageView toggleNewPassword, toggleConfirmPassword;

    FirebaseAuth mAuth;
    DatabaseReference databaseRef;

    boolean isPasswordVisible = false;
    boolean isConfirmVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_number);
        etPassword = findViewById(R.id.Password);
        etConfirmPassword = findViewById(R.id.confirmPassword);
        passwordRules = findViewById(R.id.passwordRules);
        btnRegister = findViewById(R.id.btn_next);
        tvLoginRedirect = findViewById(R.id.tv_login_redirect);
        toggleNewPassword = findViewById(R.id.toggleNewPassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // Register button
        btnRegister.setOnClickListener(v -> registerUser());

        // Login redirect
        tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        // Toggle visibility
        toggleNewPassword.setOnClickListener(v ->
                toggleVisibility(etPassword, toggleNewPassword, true));
        toggleConfirmPassword.setOnClickListener(v ->
                toggleVisibility(etConfirmPassword, toggleConfirmPassword, false));

        // Password rules listener
        etPassword.addTextChangedListener(passwordWatcher);
    }

    private void toggleVisibility(EditText passwordField, ImageView toggleIcon, boolean isMainPassword) {
        int cursorPosition = passwordField.getSelectionStart();
        Typeface typeface = passwordField.getTypeface();

        boolean isVisible = isMainPassword ? isPasswordVisible : isConfirmVisible;

        if (isVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open);
        }

        passwordField.setTypeface(typeface);
        passwordField.setSelection(cursorPosition);

        if (isMainPassword) {
            isPasswordVisible = !isPasswordVisible;
        } else {
            isConfirmVisible = !isConfirmVisible;
        }
    }

    private final TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updatePasswordRules(s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void updatePasswordRules(String password) {
        String rulesText = "";
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        boolean hasMinLength = password.length() >= 8;

        rulesText += hasUpper ? "✓ 1 uppercase character\n" : "✗ 1 uppercase character\n";
        rulesText += hasSpecial ? "✓ 1 special character\n" : "✗ 1 special character\n";
        rulesText += hasMinLength ? "✓ Minimum 8 characters" : "✗ Minimum 8 characters";

        passwordRules.setText(rulesText);
        passwordRules.setTextColor(getResources().getColor(
                hasUpper && hasSpecial && hasMinLength ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    User user = new User(name, email, phone);
                    databaseRef.child(userId).setValue(user).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(Register.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class User {
        public String name, email, phone;
        public User() {}
        public User(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
    }
}
