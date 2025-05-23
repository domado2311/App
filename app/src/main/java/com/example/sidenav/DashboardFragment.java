package com.example.sidenav;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DashboardFragment extends Fragment {

    private PieChart pieChart;
    private RecyclerView legendRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        pieChart = view.findViewById(R.id.pie_chart);
        legendRecycler = view.findViewById(R.id.legend_recycler);
        legendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        setupPieChart();
        loadProductsIntoChart();

        return view;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        // Hide product names inside slices
        pieChart.setDrawEntryLabels(false); // <-- This line disables the labels inside the slices

        pieChart.getLegend().setEnabled(false); // Hide default legend
    }

    private void loadProductsIntoChart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid())
                .child("products")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PieEntry> entries = new ArrayList<>();
                        List<Integer> colors = new ArrayList<>();
                        List<LegendItem> legendItems = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Product product = data.getValue(Product.class);
                            if (product != null) {
                                try {
                                    int quantity = Integer.parseInt(product.quantity.trim());
                                    if (quantity > 0) {
                                        entries.add(new PieEntry(quantity)); // No label here

                                        int color = getRandomColor();
                                        if (quantity <= 2) {
                                            color = Color.argb(60, Color.red(color), Color.green(color), Color.blue(color));
                                        }
                                        colors.add(color);
                                        legendItems.add(new LegendItem(product.name, color));
                                    }
                                } catch (NumberFormatException e) {
                                    // Ignore invalid quantity
                                }
                            }
                        }

                        PieDataSet dataSet = new PieDataSet(entries, "");
                        dataSet.setColors(colors);
                        dataSet.setValueTextSize(18f);
                        dataSet.setValueTextColor(Color.BLACK);

                        PieData data = new PieData(dataSet);
                        pieChart.setData(data);
                        pieChart.invalidate(); // refresh

                        LegendAdapter adapter = new LegendAdapter(legendItems);
                        legendRecycler.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Optional: handle Firebase error
                    }
                });
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
