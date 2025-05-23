package com.example.sidenav;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Products extends AppCompatActivity {

    private ArrayList<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;
    private static final int REQUEST_ADD_PRODUCT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);

        RecyclerView recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        loadProductsFromFirebase();

        FloatingActionButton fabAddProduct = findViewById(R.id.fab_add_product);
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(Products.this, EditProducts.class);
            startActivityForResult(intent, REQUEST_ADD_PRODUCT);
        });
    }

    private void loadProductsFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("products")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Product product = dataSnapshot.getValue(Product.class);
                                if (product != null) {
                                    product.setId(dataSnapshot.getKey());
                                    productList.add(product);
                                }
                            }

                            // ✅ Sort alphabetically by name (case-insensitive)
                            Collections.sort(productList, new Comparator<Product>() {
                                @Override
                                public int compare(Product p1, Product p2) {
                                    return p1.name.toLowerCase().compareTo(p2.name.toLowerCase());
                                }
                            });

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Products.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PRODUCT && resultCode == RESULT_OK) {
            Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show();
        }
    }
}
