package com.sample.MovEmt.routeInfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;

import java.util.ArrayList;

public class SectionItemAdapter extends RecyclerView.Adapter<SectionViewHolder> {
    private ArrayList<SectionItem> alSectionsItems;

    public SectionItemAdapter(ArrayList<SectionItem> alSectionsItems){ this.alSectionsItems=alSectionsItems; }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_item, parent, false);
        return new SectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        SectionItem section = alSectionsItems.get(position);
        holder.getSectionDistance().setText(section.getSectionDistance());
        holder.getSectionDuration().setText(section.getSectionDuration());
        holder.getOrder().setText(section.getOrder());
        holder.getType().setText(section.getType());
        holder.getIdLine().setText(section.getIdLine());
        holder.getSourceIdStop().setText(section.getSourceIdStop());
        holder.getSourceName().setText(section.getSourceName());
        holder.getSourceDescription().setText(section.getSourceDescription());
        holder.getDestinationName().setText(section.getDestinationName());
    }

    @Override
    public int getItemCount() {
        return alSectionsItems.size();
    }
}
