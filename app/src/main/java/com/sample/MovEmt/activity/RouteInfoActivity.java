package com.sample.MovEmt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.MovEmt.R;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;
import com.sample.MovEmt.routeInfo.RouteItem;
import com.sample.MovEmt.routeInfo.SectionItem;
import com.sample.MovEmt.routeInfo.SectionItemAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RouteInfoActivity extends AppCompatActivity {
    private RouteItem routeI;
    public String response;
    public double sourceLat;
    public double sourceLon;
    public double destLat;
    public double destLon;
    public RecyclerView rvSections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        sourceLat = Double.parseDouble(intent.getStringExtra("cLatOrig"));
        sourceLon = Double.parseDouble(intent.getStringExtra("cLonOrig"));
        destLat = Double.parseDouble(intent.getStringExtra("cLatDest"));
        destLon = Double.parseDouble(intent.getStringExtra("cLonDest"));
        setContentView(R.layout.activity_route_info);

        rvSections = findViewById(R.id.rvSections);
        rvSections.setHasFixedSize(true);
        //request data on background
        Thread thread = new Thread(() -> {
            getInfo();
            // update view
            runOnUiThread(()->{
                rvSections.setLayoutManager(new LinearLayoutManager(this));
                TextView distance = findViewById(R.id.distance);
                distance.setText(routeI.getDistance() + " km");
                TextView departureTime = findViewById(R.id.departureTime);
                departureTime.setText(routeI.getDepartureTime());
                TextView description = findViewById(R.id.description);
                description.setText(routeI.getDescription());
                TextView arrivalTime = findViewById(R.id.arrivalTime);
                arrivalTime.setText(routeI.getArrivalTime());
                TextView duration = findViewById(R.id.duration);
                duration.setText(routeI.getDuration() + "min");

                rvSections.setAdapter(new SectionItemAdapter(routeI.getLines()));
            });
        });
        thread.start();
        Button btn_back = (Button) findViewById(R.id.Back);
        btn_back.setOnClickListener((v) -> {
            onClickBack(v);
        });
    }

    private void getInfo(){
        Date date = new Date();
        String anyoDate = new SimpleDateFormat("yyyy").format(date);
        String mesDate = new SimpleDateFormat("MM").format(date);
        String diaDate = new SimpleDateFormat("dd").format(date);
        String horaDate = new SimpleDateFormat("HH").format(date);
        String minDate = new SimpleDateFormat("mm").format(date);
        int anyo = Integer.parseInt(anyoDate);
        int mes = Integer.parseInt(mesDate);
        int dia = Integer.parseInt(diaDate);
        int hora = Integer.parseInt(horaDate);
        int min = Integer.parseInt(minDate);

        try {
            URL url = new URL(String.format(EndPoint.ROUTE));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            //headers
            con.setRequestProperty("accessToken", Authentication.accessToken);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            // enable write content
            con.setDoOutput(true);

            //Define JSON
            String params = "{\"routeType\":\"P\","+
                    "\"itinerary\": true,"+
                    "\"coordinateXFrom\":"+ sourceLon  + "," +
                    "\"coordinateYFrom\":"+ sourceLat + "," +
                    "\"coordinateXTo\":"+ destLon + "," +
                    "\"coordinateYTo\":"+ destLat  + "," +
                    "\"originName\": \"\"," +
                    "\"destinationName\": \"\"," +
                    "\"polygon\": null," +
                    "\"day\":"+ dia + "," +
                    "\"month\":"+ mes + "," +
                    "\"year\":"+ anyo + "," +
                    "\"hour\":"+ hora + "," +
                    "\"minute\":"+ min + "," +
                    "\"culture\": \"es\"," +
                    "\"allowBus\": true," +
                    "\"allowBike\": false }";

            // write them to connection
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                Log.e("RouteInfoActivity", "Api error while calling route info");
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
        JSONObject data = res.getJSONObject("data");
        String distance = String.valueOf(data.getDouble("distance"));
        String departureTime = data.getString("departureTime");
        String description = data.getString("description");
        String arrivalTime = data.getString("arrivalTime");
        String duration = String.valueOf(data.getInt("duration"));
        ArrayList<SectionItem> sections = parseSectionsFromJson(data.getJSONArray("sections"));
        routeI = new RouteItem(distance,departureTime,description,arrivalTime,duration,sections);
        int i = 2;
    }
    private ArrayList<SectionItem> parseSectionsFromJson(JSONArray sections) throws JSONException {
        ArrayList<SectionItem> aSections = new ArrayList<SectionItem>();
        for(int i = 0; i < sections.length(); i++){
            JSONObject section = sections.getJSONObject(i);
            String sectionDistance = String.valueOf(section.getDouble("distance"));
            String sectionDuration = String.valueOf(section.getDouble("duration"));
            String sectionOrder = String.valueOf(section.getInt("order"));
            String sectionType = section.getString("type");
            String sectionIdline = "";
            String sourceIdStop = "";
            String sourceName ="";
            String sourceDescription="";
            String destinationName= "";
            String destinationDescription= "";
            if(sectionType.equalsIgnoreCase("Bus"))
            {
                sectionIdline = section.getString("idLine");
            }
            JSONObject sectionDestination = section.getJSONObject("destination");
            JSONObject sectionSource = section.getJSONObject("source");
            if (!sectionSource.toString().equals("{}")){
                JSONObject sourceProperties = sectionSource.getJSONObject("properties");
                if (!sourceProperties.toString().equals("{}")){
                    sourceIdStop = sourceProperties.getString("idStop");
                    sourceName = sourceProperties.getString("name");
                    sourceDescription = sourceProperties.getString("description");
                }

            }
            if (!sectionDestination.toString().equals("{}")){
                JSONObject destinationProperties = sectionDestination.getJSONObject("properties");
                if (!destinationProperties.toString().equals("{}")){
                    destinationName = destinationProperties.getString("name");
                    destinationDescription = destinationProperties.getString("description");
                }
            }
            aSections.add(new SectionItem(sectionDistance,sectionDuration,sectionOrder,sectionType,sectionIdline,sourceIdStop,sourceName,sourceDescription,destinationName,destinationDescription));
        }
        return aSections;
    }
    void onClickBack(View v){
        Intent intent = new Intent (v.getContext(), SelectRoute.class);
        startActivityForResult(intent, 0);
    }
}
