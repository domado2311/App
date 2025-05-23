package com.example.sidenav;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WalletActivity extends AppCompatActivity {
    private TextView textViewTotalSales, textViewUpdated;
    private TextView textViewTransaction1, textViewTransaction2;
    private TextView textViewSeeAllTransactions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT); // Optional: Transparent status ba
        // Initialize views
        textViewTotalSales = findViewById(R.id.textViewTotalSales);
        textViewUpdated = findViewById(R.id.textViewUpdated);
        textViewTransaction1 = findViewById(R.id.textViewTransaction1);
        textViewTransaction2 = findViewById(R.id.textViewTransaction2);
        textViewSeeAllTransactions = findViewById(R.id.textViewSeeAllTransactions);

        // Fetch wallet data and recent transactions
        fetchWalletTotalAndTransactions();

        // Show all transactions when clicked
        textViewSeeAllTransactions.setOnClickListener(v -> fetchAllTransactionsAndShow());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        fetchWalletTotalAndTransactions();
    }

    private void fetchWalletTotalAndTransactions() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;  // User not logged in

        String userId = currentUser.getUid();

        // Fetch wallet total amount
        FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("wallet_total")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String total = snapshot.getValue(String.class);
                        textViewTotalSales.setText("₱" + total);
                    } else {
                        textViewTotalSales.setText("₱0.00");
                    }
                    updateLastUpdatedTime();
                })
                .addOnFailureListener(e -> {
                    textViewTotalSales.setText("₱0.00");
                    updateLastUpdatedTime();
                });

        // Fetch last 2 transactions
        FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("wallet_transactions")
                .limitToLast(2)
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> transactions = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String desc = snap.getValue(String.class);
                        if (desc != null) transactions.add(desc);
                    }
                    // Reverse to show newest first
                    Collections.reverse(transactions);

                    textViewTransaction1.setText(transactions.size() > 0 ? "• " + transactions.get(0) : "No recent transactions");
                    textViewTransaction2.setText(transactions.size() > 1 ? "• " + transactions.get(1) : "");
                })
                .addOnFailureListener(e -> {
                    textViewTransaction1.setText("No recent transactions");
                    textViewTransaction2.setText("");
                });
    }

    private void updateLastUpdatedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String formattedTime = sdf.format(new Date());
        textViewUpdated.setText("Last updated: " + formattedTime);
    }

    private void fetchAllTransactionsAndShow() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("wallet_transactions")
                .get()
                .addOnSuccessListener(snapshot -> {
                    ArrayList<String> allTransactions = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String val = snap.getValue(String.class);
                        if (val != null) allTransactions.add(val);
                    }
                    Collections.reverse(allTransactions);

                    StringBuilder message = new StringBuilder();
                    for (String transaction : allTransactions) {
                        message.append("• ").append(transaction).append("\n");
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("All Transactions")
                            .setMessage(message.length() > 0 ? message.toString() : "No transactions yet.")
                            .setPositiveButton("Close", null)
                            .show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load transactions", Toast.LENGTH_SHORT).show()
                );
    }
}
