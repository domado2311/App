package com.example.sidenav;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    private EditText currentPassword, newPassword, confirmPassword;
    private TextView passwordRules;
    private Button confirmButton;
    private ImageView toggleNewPassword, toggleConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass);

        // Bind UI elements
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.Password);
        confirmPassword = findViewById(R.id.confirmPassword);
        passwordRules = findViewById(R.id.passwordRules);
        confirmButton = findViewById(R.id.confirmButton);
        toggleNewPassword = findViewById(R.id.toggleNewPassword);
        toggleConfirmPassword = findViewById(R.id.toggleConfirmPassword);

        // Add password rules watcher
        newPassword.addTextChangedListener(passwordWatcher);

        // Confirm button click listener
        confirmButton.setOnClickListener(v -> {
            String currentPass = currentPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(ForgotPassword.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validatePassword(newPass)) {
                Toast.makeText(ForgotPassword.this, "Password does not meet the requirements.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(ForgotPassword.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // If everything is valid
            Toast.makeText(ForgotPassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ForgotPassword.this, Login.class));
        });

        // Toggle password visibility
        toggleNewPassword.setOnClickListener(v ->
                togglePasswordVisibility(newPassword, toggleNewPassword));

        toggleConfirmPassword.setOnClickListener(v ->
                togglePasswordVisibility(confirmPassword, toggleConfirmPassword));
    }

    // Password validation rules
    private boolean validatePassword(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        boolean hasMinimumLength = password.length() >= 8;
        return hasUppercase && hasSpecial && hasMinimumLength;
    }

    // Live feedback for password rules
    private final TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String password = s.toString();
            StringBuilder rulesText = new StringBuilder();

            if (!password.equals(password.toLowerCase())) {
                rulesText.append("✓  1 uppercase character\n");
            } else {
                rulesText.append("✗ 1 uppercase character\n");
            }

            if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                rulesText.append("✓ 1 special character\n");
            } else {
                rulesText.append("✗ 1 special character\n");
            }

            if (password.length() >= 8) {
                rulesText.append("✓ Minimum 8 characters");
            } else {
                rulesText.append("✗ Minimum 8 characters");
            }

            passwordRules.setText(rulesText.toString());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    // Toggle visibility of password field
    private void togglePasswordVisibility(EditText passwordField, ImageView toggleIcon) {
        if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_open); // Eye open icon
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_closed); // Eye closed icon
        }
        passwordField.setSelection(passwordField.getText().length());
    }
}