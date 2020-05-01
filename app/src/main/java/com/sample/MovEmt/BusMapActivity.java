package com.sample.MovEmt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BusMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int LOCATION_REQUEST = 500;
    private static final int REQUEST_CODE = 101;

    private Button btBack;
    private Button btUpdate;
    private ProgressBar pbLoad;
    private MapView mvMap;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private boolean isLoading = false;
    private double stopLat = 0;
    private double stopLon = 0;
    private String stopId;
    ArrayList<LatLng> buses;
    ArrayList<String> lineas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);

        btBack = findViewById(R.id.btBack);
        btUpdate = findViewById(R.id.btUpdate);
        pbLoad = findViewById(R.id.pbLoad);
        mvMap = findViewById(R.id.mvMap);
        buses = new ArrayList<>();
        lineas = new ArrayList<>();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        Thread thread = new Thread(() -> {
            getStops();
            getBuses();
            // update view
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);

        mvMap.onCreate(mapViewBundle);
        mvMap.getMapAsync(this);
        btBack.setOnClickListener(this::onClickBack);
        btUpdate.setOnClickListener(this::onClickUpdate);



        //mvMap.getMapAsync(this);
        // show loading
        switchLoadingState();

    }

    private void getBuses() {
        try {
            URL url = new URL(String.format(EndPoint.ARRIVE_LINES, stopId));
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
            parseBusesFromJson(response);

            con.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseBusesFromJson(String response) throws JSONException {
        JSONObject res = new JSONObject(response);
        JSONArray data = res.getJSONArray("data");
        JSONObject arr = data.getJSONObject(0);
        JSONArray arrives = arr.getJSONArray("Arrive");
        for (int i=0; i< arrives.length(); i++){
            JSONObject bus = arrives.getJSONObject(i);
            String line = bus.getString("line");
            JSONObject geometry = bus.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");
            LatLng coords = new LatLng(coordinates.getDouble(1),coordinates.getDouble(0));
            buses.add(coords);
            lineas.add(line);
        }

    }

    private void getStops(){
        try {
            //URL url = new URL(String.format(EndPoint.NEAR_STOPS, String.valueOf(currentLocation.getLongitude()),
                    //String.valueOf(currentLocation.getLatitude()), String.valueOf(200)));
            URL url = new URL(String.format(EndPoint.NEAR_STOPS, String.valueOf(-3.70325),
                    String.valueOf(40.4167), String.valueOf(200)));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            //headers
            con.setRequestProperty("accessToken", Authentication.accessToken);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");


            //con.setDoOutput(true);
            int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                Log.e("BusMapActivity", "Api error while calling near stops");
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
        JSONObject stop = data.getJSONObject(0);
        JSONObject geometry = stop.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");
        stopLat = Double.parseDouble(coordinates.getString(1));
        stopLon = Double.parseDouble(coordinates.getString(0));
        stopId = String.valueOf(stop.getInt("stopId"));
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(),currentLocation.getLatitude() + " " +
                            currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mvMap.onSaveInstanceState(mapViewBundle);
    }


    /**
     * Use this method to show or to hide the loading component
     */
    private void switchLoadingState() {
        runOnUiThread(() -> {
            isLoading = !isLoading;
            if (isLoading) {
                btUpdate.setVisibility(View.GONE);
                mvMap.setVisibility(View.GONE);
                pbLoad.setVisibility(View.VISIBLE);
            } else {
                btUpdate.setVisibility(View.VISIBLE);
                mvMap.setVisibility(View.VISIBLE);
                pbLoad.setVisibility(View.GONE);
            }
        });
    }

    private void onClickBack(View view) {
        finish();
    }

    private void onClickUpdate(View view) {
        mvMap.getMapAsync(this);
        switchLoadingState();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        LatLng nearStop = new LatLng(stopLat,stopLon);
        MarkerOptions nStop = new MarkerOptions().position(nearStop).title("Parada: " + stopId);
        nStop.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        googleMap.addMarker(nStop);
        for (int i=0; i < buses.size(); i++){
            MarkerOptions autobus = new MarkerOptions().position(buses.get(i)).title("Línea: " + lineas.get(i));
            autobus.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker(autobus);
        }


        // show map
        switchLoadingState();
    }

    @Override
    public void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mvMap.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mvMap.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mvMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mvMap.onDestroy();
    }

    public void onLowMemory(){
        super.onLowMemory();
        mvMap.onLowMemory();
    }
}
