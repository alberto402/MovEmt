package com.sample.MovEmt;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.busStopItem.BusItem;
import com.sample.MovEmt.busStopItem.BusItemAdapter;

import java.util.ArrayList;

public class StopBusesActivity extends AppCompatActivity {
    private RecyclerView rvBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_buses);

        // remove its just for test
        ArrayList<BusItem> alBus = new ArrayList<>();
        alBus.add(new BusItem("82", 2));
        alBus.add(new BusItem("133", 3));
        alBus.add(new BusItem("G", 5));

        rvBus = findViewById(R.id.rvBus);
        rvBus.setHasFixedSize(true);
        rvBus.setLayoutManager(new LinearLayoutManager(this));
        rvBus.setAdapter(new BusItemAdapter(alBus));
    }
}
