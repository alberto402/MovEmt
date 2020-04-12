package com.sample.MovEmt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.busStopItem.BusItem;
import com.sample.MovEmt.busStopItem.BusItemAdapter;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StopBusesActivity extends AppCompatActivity {
    private RecyclerView rvBus;
    private ArrayList<BusItem> alBus;
    private int stopNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_buses);
        Intent intent = getIntent();
        stopNumber = intent.getIntExtra("stopNumber", 0);

        alBus = new ArrayList<>();
        // remove, its just for test
        alBus.add(new BusItem("82", 2));
        alBus.add(new BusItem("133", 3));
        alBus.add(new BusItem("G", 5));

        rvBus = findViewById(R.id.rvBus);
        rvBus.setHasFixedSize(true);

        // TODO request data on background
        Thread thread = new Thread(() -> {
            getBuses();
            // update view
            runOnUiThread(()->{
                rvBus.setLayoutManager(new LinearLayoutManager(this));
                rvBus.setAdapter(new BusItemAdapter(alBus));
            });
        });
        thread.start();
    }

    private void getBuses(){
        try {
            URL url = new URL(String.format(EndPoint.ARRIVE_LINES, stopNumber));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            //headers
            con.setRequestProperty("accessToken", Authentication.accessToken);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            // enable write content
            con.setDoOutput(true);

            // define JSON
            String args = "{\"statistics\":\"N\"," +
                    "\"cultureInfo\":\"ES\"," +
                    "\"Text_StopRequired_YN\":\"N\"," +
                    "\"Text_EstimationsRequired_YN\":\"Y\"," +
                    "\"Text_IncidencesRequired_YN\":\"N\"," +
                    "\"DateTime_Referenced_Incidencies_YYYYMMDD\":\"20190923\"}";

            // write them to connection
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = args.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                Log.e("StopBusesActivity", "Api error while calling buses arrival");
                return;
            }

            String response = new ResponseReader().getResponse(con);
            Log.d("StopBusesActivity", response);

            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
