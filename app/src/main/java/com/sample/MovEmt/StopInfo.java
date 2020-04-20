package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;
import com.sample.MovEmt.stopInfo.LineItem;
import com.sample.MovEmt.stopInfo.LineItemAdapter;
import com.sample.MovEmt.stopInfo.StopItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class StopInfo extends AppCompatActivity {
    private StopItem stopI;
    private int stopNumber;
    private RecyclerView rvLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_info);
        stopNumber=173;
        rvLines = findViewById(R.id.rvLines);
        rvLines.setHasFixedSize(true);
        //request data on background
        Thread thread = new Thread(() -> {
            getInfo();
            // update view
            runOnUiThread(()->{
                rvLines.setLayoutManager(new LinearLayoutManager(this));
                TextView idStop = findViewById(R.id.idStop);
                idStop.setText(stopI.getIdStop());
                TextView nameStop = findViewById(R.id.nameStop);
                nameStop.setText(stopI.getNameStop());
                TextView direction = findViewById(R.id.direction);
                direction.setText(stopI.getDirection());

                rvLines.setAdapter(new LineItemAdapter(stopI.getLines()));
            });
        });
        thread.start();


    }
    private void getInfo(){
        try {
            URL url = new URL(String.format(EndPoint.INFO_STOP, stopNumber));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            //headers
            con.setRequestProperty("accessToken", Authentication.accessToken);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");


            //con.setDoOutput(true);
            int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                Log.e("StopBusesActivity", "Api error while calling buses arrival");
                return;
            }
            String response = new ResponseReader().getResponse(con);
            parseInfoFromJson(response);

            con.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseInfoFromJson(String response) throws JSONException {
        JSONObject res = new JSONObject(response);
        JSONArray data = res.getJSONArray("data");
        JSONObject inf = data.getJSONObject(0);
        JSONArray stops = inf.getJSONArray("stops");
        JSONObject stop = stops.getJSONObject(0);
        ArrayList<LineItem> lines = parseLinesFromJson(stop.getJSONArray("dataLine"));
        stopI = new StopItem(stop.getString("stop"),stop.getString("name"),lines,stop.getString("postalAddress") );


    }
    private ArrayList<LineItem> parseLinesFromJson(JSONArray lines) throws JSONException {
        ArrayList<LineItem> aLines = new ArrayList<LineItem>();
        for(int i = 0; i < lines.length(); i++){
           JSONObject line = lines.getJSONObject(i);
           if(line.getString("direction").equalsIgnoreCase("A"))
           {
               aLines.add(new LineItem(line.getString("headerB"),line.getString("headerA"),line.getString("label"),
                    line.getString("startTime"),line.getString("stopTime"),line.getString("minFreq")));
           }
           else{
               aLines.add(new LineItem(line.getString("headerA"),line.getString("headerB"),line.getString("label"),
                       line.getString("startTime"),line.getString("stopTime"),line.getString("minFreq")));
           }

        }


        return aLines;
    }
}
