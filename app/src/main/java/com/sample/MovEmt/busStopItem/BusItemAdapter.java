package com.sample.MovEmt.busStopItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;

import java.util.ArrayList;

public class BusItemAdapter extends RecyclerView.Adapter<BusViewHolder> {
    private ArrayList<BusItem> albusItems;

    public BusItemAdapter(ArrayList<BusItem> albusItems){
        this.albusItems = albusItems;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        BusItem bus = albusItems.get(position);
        holder.getBusLine().setText(bus.getBus());
        holder.getNextIn().setText(bus.getMinutes() + "'");
    }

    @Override
    public int getItemCount() {
        return albusItems.size();
    }
}
