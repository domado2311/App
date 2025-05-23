package com.example.sidenav;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> productList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;

        // Sort the product list alphabetically by product name (case-insensitive)
        productList.sort((p1, p2) -> p1.name.compareToIgnoreCase(p2.name));

        this.productList = productList;
    }

    // Optional: method to update product list with sorting
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setProductList(List<Product> newList) {
        newList.sort((p1, p2) -> p1.name.compareToIgnoreCase(p2.name));
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Display product details
        holder.textName.setText(product.name);
        holder.textPrice.setText("Price: ₱" + product.price);
        holder.textQuantity.setText("Qty: " + product.quantity);

        // Calculate and display total (price * quantity)
        try {
            double price = Double.parseDouble(product.price);
            int quantity = Integer.parseInt(product.quantity);
            double total = price * quantity;
            holder.textTotal.setText("Total: ₱" + String.format(Locale.getDefault(), "%.2f", total));
        } catch (NumberFormatException e) {
            holder.textTotal.setText("Total: ₱0.00");
        }

        // Edit button click - open EditProducts activity with product data
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProducts.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.name);
            intent.putExtra("product_price", product.price);
            intent.putExtra("product_quantity", product.quantity);
            context.startActivity(intent);
        });

        // Delete button click - confirm and delete product from Firebase
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(userId)
                                    .child("products")
                                    .child(product.getId())
                                    .removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Sold button click - decrement quantity, update wallet total, add transaction record
        holder.btnSold.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) return;

            String userId = currentUser.getUid();

            int currentQty;
            try {
                currentQty = Integer.parseInt(product.quantity);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentQty <= 0) {
                Toast.makeText(context, "No stock left", Toast.LENGTH_SHORT).show();
                return;
            }

            int updatedQty = currentQty - 1;

            double price;
            try {
                price = Double.parseDouble(product.price);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update product quantity in Firebase
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("products")
                    .child(product.getId())
                    .child("quantity")
                    .setValue(String.valueOf(updatedQty));

            // Update wallet_total (increase by price of sold product)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("wallet_total")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        double currentTotal = 0;
                        if (snapshot.exists()) {
                            try {
                                currentTotal = Double.parseDouble(snapshot.getValue(String.class));
                            } catch (Exception ignored) {
                            }
                        }
                        double newTotal = currentTotal + price;

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .child("wallet_total")
                                .setValue(String.format(Locale.getDefault(), "%.2f", newTotal));

                        // Add transaction record with current date/time (Manila timezone)
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
                        String dateTime = sdf.format(new Date());

                        String transactionDescription = dateTime + " - Sold 1 " + product.name + " - ₱" + String.format(Locale.getDefault(), "%.2f", price);

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .child("wallet_transactions")
                                .push()
                                .setValue(transactionDescription);
                    });

            // Update local product quantity and refresh item view
            product.quantity = String.valueOf(updatedQty);
            notifyItemChanged(holder.getAdapterPosition());

            Toast.makeText(context, "Marked as sold", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice, textQuantity, textTotal;
        TextView btnEdit, btnDelete, btnSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_product_name);
            textPrice = itemView.findViewById(R.id.text_product_price);
            textQuantity = itemView.findViewById(R.id.text_product_quantity);
            textTotal = itemView.findViewById(R.id.text_product_total);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnSold = itemView.findViewById(R.id.btn_sold);
        }
    }
}
