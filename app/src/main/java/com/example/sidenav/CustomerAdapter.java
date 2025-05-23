package com.example.sidenav;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final Context context;
    private final List<Customer> customerList;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.textName.setText(customer.getName());
        holder.textContact.setText("Contact: " + customer.getContact());
        holder.textAddress.setText("Address: " + customer.getAddress());
        holder.textBalance.setText("Balance: " + customer.getDebt());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCustomers.class);
            intent.putExtra("customer_id", customer.getId());
            intent.putExtra("customer_name", customer.getName());
            intent.putExtra("customer_contact", customer.getContact());
            intent.putExtra("customer_email", customer.getEmail());
            intent.putExtra("customer_address", customer.getAddress());
            intent.putExtra("customer_Balance", customer.getDebt());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Customer")
                    .setMessage("Are you sure you want to delete this customer?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(currentUser.getUid())
                                    .child("customers")
                                    .child(customer.getId())
                                    .removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(context, "Customer deleted", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Failed to delete customer", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textContact, textAddress, textBalance;
        TextView btnEdit, btnDelete;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_customer_name);
            textContact = itemView.findViewById(R.id.text_customer_contact);
            textAddress = itemView.findViewById(R.id.text_customer_address);
            textBalance = itemView.findViewById(R.id.text_customer_debt);
            btnEdit = itemView.findViewById(R.id.btn_edit_customer);
            btnDelete = itemView.findViewById(R.id.btn_delete_customer);
        }
    }
}
