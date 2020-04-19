package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;
import com.sample.MovEmt.stopInfo.LineItem;
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
    private ArrayList<LineItem> lineas;
    private int stopNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_info);
        stopNumber=345;
        //request data on background
        Thread thread = new Thread(() -> {
            getInfo();
            // update view
            runOnUiThread(()->{
                //rvBus.setLayoutManager(new LinearLayoutManager(this));
                //rvBus.setAdapter(new BusItemAdapter(alBus));
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
        StopItem myStop = new StopItem(stop.getString("stop"),stop.getString("name"),lines,stop.getString("postalAddress") );
        int a=3;


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
