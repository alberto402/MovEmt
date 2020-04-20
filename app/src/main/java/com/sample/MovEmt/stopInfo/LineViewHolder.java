package com.sample.MovEmt.stopInfo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;

public class LineViewHolder  extends RecyclerView.ViewHolder {
    private TextView sour;
    private TextView dest;
    private TextView number;
    private TextView start;
    private TextView end;
    private TextView frequency;

    public LineViewHolder(@NonNull View itemView) {
        super(itemView);
        sour = itemView.findViewById(R.id.sour);
        dest = itemView.findViewById(R.id.dest);
        number = itemView.findViewById(R.id.number);
        start = itemView.findViewById(R.id.start);
        end = itemView.findViewById(R.id.end);
        frequency = itemView.findViewById(R.id.frequency);
    }

    public TextView getDest() {
        return dest;
    }

    public TextView getEnd() {
        return end;
    }

    public TextView getFrequency() {
        return frequency;
    }

    public TextView getNumber() {
        return number;
    }

    public TextView getSour() {
        return sour;
    }

    public TextView getStart() {
        return start;
    }
}
