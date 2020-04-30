package com.sample.MovEmt.busStopItem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;

public class BusViewHolder extends RecyclerView.ViewHolder {
    private TextView busLine;
    private TextView nextIn;

    public BusViewHolder(@NonNull View itemView) {
        super(itemView);
        busLine = itemView.findViewById(R.id.busLine);
        nextIn = itemView.findViewById(R.id.nextIn);
    }

    public TextView getBusLine() {
        return busLine;
    }

    public TextView getNextIn() {
        return nextIn;
    }
}
