package com.sample.MovEmt.stopInfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;


import java.util.ArrayList;

public class LineItemAdapter extends RecyclerView.Adapter<LineViewHolder> {
    private ArrayList<LineItem> alLinesItems;

    public LineItemAdapter(ArrayList<LineItem> alLinesItems){
        this.alLinesItems = alLinesItems;
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_item, parent, false);
        return new LineViewHolder(itemView);
    }
/*******************/
    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
        LineItem line = alLinesItems.get(position);
        holder.getDest().setText(line.getDest());
        holder.getEnd().setText(line.getEnd());
        holder.getFrequency().setText(line.getFrequency());
        holder.getNumber().setText(line.getNumber());
        holder.getSour().setText(line.getSour());
        holder.getStart().setText(line.getStart());

    }

    @Override
    public int getItemCount() {
        return alLinesItems.size();
    }
}
