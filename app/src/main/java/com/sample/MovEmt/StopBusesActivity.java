package com.sample.MovEmt;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.busStopItem.BusItem;
import com.sample.MovEmt.busStopItem.BusItemAdapter;
import com.sample.MovEmt.emtApi.Authentication;

import java.util.ArrayList;

public class StopBusesActivity extends AppCompatActivity {
    private RecyclerView rvBus;
    private ArrayList<BusItem> alBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_buses);


        alBus = new ArrayList<>();
        // remove, its just for test
        alBus.add(new BusItem("82", 2));
        alBus.add(new BusItem("133", 3));
        alBus.add(new BusItem("G", 5));

        rvBus = findViewById(R.id.rvBus);
        rvBus.setHasFixedSize(true);

        // TODO request data on background
        Thread thread = new Thread(() -> {

            // update view
            runOnUiThread(()->{
                Log.d("StopBusesActivity", Authentication.accessToken);
                rvBus.setLayoutManager(new LinearLayoutManager(this));
                rvBus.setAdapter(new BusItemAdapter(alBus));
            });
        });
        thread.start();
    }
}
