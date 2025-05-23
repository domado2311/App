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

public class EditCustomers extends AppCompatActivity {

    EditText editName, editContact, editEmail, editAddress, editBalance;
    Button btnSave;
    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_customers);

        editName = findViewById(R.id.edit_customer_name);
        editContact = findViewById(R.id.edit_customer_phone);
        editEmail = findViewById(R.id.edit_customer_email);
        editAddress = findViewById(R.id.edit_customer_address);
        editBalance = findViewById(R.id.edit_customer_debt);
        btnSave = findViewById(R.id.button_save_customer);

        customerId = getIntent().getStringExtra("customer_id");
        String name = getIntent().getStringExtra("customer_name");
        String contact = getIntent().getStringExtra("customer_contact");
        String email = getIntent().getStringExtra("customer_email");
        String address = getIntent().getStringExtra("customer_address");
        String debt = getIntent().getStringExtra("customer_debt");

        if (customerId != null) {
            editName.setText(name);
            editContact.setText(contact);
            editEmail.setText(email);
            editAddress.setText(address);
            editBalance.setText(debt);
        }

        btnSave.setOnClickListener(v -> saveCustomer());
    }

    private void saveCustomer() {
        String name = editName.getText().toString().trim();
        String contact = editContact.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String Balance = editBalance.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contact) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(address) || TextUtils.isEmpty(Balance)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        Customer customer = new Customer(name, contact, email, address, Balance);

        if (customerId != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("customers").child(customerId)
                    .setValue(customer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Customer updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update customer", Toast.LENGTH_SHORT).show()
                    );
        } else {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("customers")
                    .push()
                    .setValue(customer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Customer saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save customer", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}
