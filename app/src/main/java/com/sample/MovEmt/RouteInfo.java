package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;
import com.sample.MovEmt.routeInfo.RouteItem;
import com.sample.MovEmt.routeInfo.SectionItem;
import com.sample.MovEmt.routeInfo.SectionItemAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class RouteInfo extends AppCompatActivity {
    private RouteItem routeI;
    public String response;
    public String sourceLat;
    public String sourceLon;
    public String destLat;
    public String destLon;
    public RecyclerView rvSections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        sourceLat = intent.getStringExtra("cLatOrig");
        sourceLon = intent.getStringExtra("cLonOrig");
        destLat = intent.getStringExtra("cLatDest");
        destLon = intent.getStringExtra("cLonDest");
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
                distance.setText(routeI.getDistance());
                TextView departureTime = findViewById(R.id.departureTime);
                departureTime.setText(routeI.getDepartureTime());
                TextView description = findViewById(R.id.description);
                description.setText(routeI.getDescription());
                TextView arrivalTime = findViewById(R.id.arrivalTime);
                arrivalTime.setText(routeI.getArrivalTime());
                TextView duration = findViewById(R.id.duration);
                duration.setText(routeI.getDuration());

                rvSections.setAdapter(new SectionItemAdapter(routeI.getLines()));
            });
        });
        thread.start();
        getInfo();
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("routeType","P");
            params.put("itinerary",true);
            params.put("coordinateXFrom",sourceLat);
            params.put("coordinateYFrom",sourceLon);
            params.put("coordinateXTo",destLat);
            params.put("coordinateYTo",destLon);
            params.put("originName","");
            params.put("destinationName","");
            params.put("polygon",null);
            params.put("day",dia);
            params.put("month",mes);
            params.put("year",anyo);
            params.put("hour",hora);
            params.put("minute",min);
            params.put("culture","es");
            params.put("allowBus",true);
            params.put("allowBike",false);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0)
                    postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()),
                        "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            //headers
            con.setRequestProperty("accessToken", Authentication.accessToken);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");


            con.setDoOutput(true);
            con.getOutputStream().write(postDataBytes);
            Reader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream(), "UTF-8"));

            for (int c = in.read(); c != -1; c = in.read())
                System.out.print((char) c);
            /*int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                Log.e("RoutesActivity", "Api error while calling routes");
                return;
            }
            response = new ResponseReader().getResponse(con);
            parseInfoFromJson(response);
            con.disconnect();*/

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseInfoFromJson(String response) throws JSONException {
        JSONObject res = new JSONObject(response);
        JSONArray data = res.getJSONArray("data");
        String distance = String.valueOf(data.getDouble(0));
        String departureTime = data.getString(1);
        String description = data.getString(2);
        String arrivalTime = data.getString(3);
        String duration = String.valueOf(data.getInt(4));
        ArrayList<SectionItem> sections = parseSectionsFromJson(data.getJSONArray(5));
        routeI = new RouteItem(distance,departureTime,description,arrivalTime,duration,sections);

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
            if(sectionType.equalsIgnoreCase("Bus"))
            {
                sectionIdline = section.getString("idLine");
            }
            JSONObject sectionDestination = section.getJSONObject("destination");
            JSONObject sectionSource = section.getJSONObject("source");
            if (sectionSource != null){
                JSONObject sourceProperties = sectionSource.getJSONObject("properties");
                sourceIdStop = sourceProperties.getString("idStop");
                sourceName = sourceProperties.getString("name");
                sourceDescription = sourceProperties.getString("description");
            }
            if (sectionDestination != null){
                JSONObject destinationProperties = sectionDestination.getJSONObject("properties");
                destinationName = destinationProperties.getString("name");
            }
            aSections.add(new SectionItem(sectionDistance,sectionDuration,sectionOrder,sectionType,sectionIdline,sourceIdStop,sourceName,sourceDescription,destinationName));
        }
        return aSections;
    }
}
