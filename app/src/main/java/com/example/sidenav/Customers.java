package com.example.sidenav;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.*;

public class Customers extends AppCompatActivity {

    private ArrayList<Customer> customerList = new ArrayList<>();
    private CustomerAdapter adapter;
    private static final int REQUEST_ADD_CUSTOMER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customers);

        RecyclerView recyclerView = findViewById(R.id.recycler_customers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(this, customerList);
        recyclerView.setAdapter(adapter);

        loadCustomers();

        FloatingActionButton fab = findViewById(R.id.fab_add_customer);
        fab.setOnClickListener(v ->
                startActivityForResult(new Intent(Customers.this, EditCustomers.class), REQUEST_ADD_CUSTOMER));
    }

    private void loadCustomers() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseDatabase.getInstance().getReference("users").child(user.getUid())
                .child("customers").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        customerList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Customer customer = ds.getValue(Customer.class);
                            if (customer != null) {
                                customer.setId(ds.getKey());
                                customerList.add(customer);
                            }
                        }
                        Collections.sort(customerList, Comparator.comparing(c -> c.getName().toLowerCase()));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Customers.this, "Failed to load customers", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == REQUEST_ADD_CUSTOMER && resCode == RESULT_OK) {
            Toast.makeText(this, "Customer added", Toast.LENGTH_SHORT).show();
        }
    }
}
