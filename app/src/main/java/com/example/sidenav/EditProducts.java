package com.example.sidenav;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class EditProducts extends AppCompatActivity {

    EditText editName, editPrice, editQuantity;
    Button btnSave;

    String productId; // for edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_products);

        editName = findViewById(R.id.edit_product_name);
        editPrice = findViewById(R.id.edit_product_price);
        editQuantity = findViewById(R.id.edit_product_quantity);
        btnSave = findViewById(R.id.button_save_product);

        // Check if we're editing an existing product
        productId = getIntent().getStringExtra("product_id");
        String name = getIntent().getStringExtra("product_name");
        String price = getIntent().getStringExtra("product_price");
        String quantity = getIntent().getStringExtra("product_quantity");

        if (productId != null) {
            // Pre-fill fields if editing
            editName.setText(name);
            editPrice.setText(price);
            editQuantity.setText(quantity);
        }

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = editName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String quantity = editQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        Product product = new Product(name, price, quantity);

        if (productId != null) {
            // Update existing product
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("products")
                    .child(productId)
                    .setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show()
                    );
        } else {
            // Add new product
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("products")
                    .push()
                    .setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
