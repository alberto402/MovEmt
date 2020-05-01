package com.sample.MovEmt.routeInfo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;

public class SectionViewHolder  extends RecyclerView.ViewHolder{
    private TextView sectionDistance;
    private TextView sectionDuration;
    private TextView order;
    private TextView type;
    private TextView idLine;
    private TextView sourceIdStop;
    private TextView sourceName;
    private TextView sourceDescription;
    private TextView destinationName;
    private TextView destinationDescription;

    public SectionViewHolder (@NonNull View itemView){
        super(itemView);
        sectionDistance=itemView.findViewById(R.id.sectionDistance);
        sectionDuration=itemView.findViewById(R.id.sectionDuration);
        order=itemView.findViewById(R.id.order);
        type=itemView.findViewById(R.id.type);
        idLine=itemView.findViewById(R.id.idLine);
        sourceIdStop=itemView.findViewById(R.id.sourceIdStop);
        sourceName=itemView.findViewById(R.id.sourceName);
        sourceDescription=itemView.findViewById(R.id.sourceDescription);
        destinationName=itemView.findViewById(R.id.destinationName);
        destinationDescription=itemView.findViewById(R.id.destinationDescription);
    }

    public TextView getSectionDistance() { return sectionDistance; }
    public TextView getSectionDuration() { return sectionDuration; }
    public TextView getOrder() { return order; }
    public TextView getType() { return type; }
    public TextView getIdLine() { return idLine; }
    public TextView getSourceIdStop() { return sourceIdStop; }
    public TextView getSourceName() { return sourceName; }
    public TextView getSourceDescription() { return sourceDescription; }
    public TextView getDestinationName() { return destinationName; }
    public TextView getDestinationDescription() { return destinationDescription; }
}
