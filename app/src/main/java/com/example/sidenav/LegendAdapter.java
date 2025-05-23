package com.example.sidenav;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.ViewHolder> {

    private List<LegendItem> legendList;

    public LegendAdapter(List<LegendItem> legendList) {
        this.legendList = legendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.legend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LegendItem item = legendList.get(position);
        holder.productName.setText(item.name);
        holder.colorBox.setBackgroundColor(item.color);
    }

    @Override
    public int getItemCount() {
        return legendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        View colorBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            colorBox = itemView.findViewById(R.id.color_box);
        }
    }
}
