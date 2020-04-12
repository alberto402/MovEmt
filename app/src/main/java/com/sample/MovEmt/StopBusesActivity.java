package com.sample.MovEmt;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class StopBusesActivity extends AppCompatActivity {
    private RecyclerView rvBus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_buses);

        rvBus = findViewById(R.id.rvBus);
    }
}
