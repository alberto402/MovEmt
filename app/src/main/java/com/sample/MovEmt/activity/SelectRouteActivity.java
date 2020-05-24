package com.sample.MovEmt.activity;

import android.location.Location;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sample.MovEmt.R;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;
import com.sample.MovEmt.fragment.LoadingDialogFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;

public class SelectRouteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mMapView;
    ArrayList<LatLng> listPoints;
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyCkBe0q8de_cNuJHTfxEVbxqWbYOSnTYmU";
    private static final int LOCATION_REQUEST = 500;
    private String cLatOrig;
    private String cLonOrig;
    private String cLatDest;
    private String cLonDest;
    private Button calculateRoute;
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);


        listPoints = new ArrayList<>();
        cLatOrig = "";
        cLonOrig = "";
        cLatDest = "";
        cLonDest = "";

        calculateRoute = findViewById(R.id.getRouteButton);
        calculateRoute.setOnClickListener(this::onClickCalculate);

        Button btn_back = (Button) findViewById(R.id.Back);
        btn_back.setOnClickListener((v) -> {
            onBackPressed();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null){
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    public void  onMapReady(GoogleMap map){
        this.map = map;
        map.getUiSettings().setZoomControlsEnabled(true);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Reset marker when already 2
                if (listPoints.size() == 2){
                    listPoints.clear();
                    cLatOrig = "";
                    cLonOrig = "";
                    cLatDest = "";
                    cLonDest = "";
                    map.clear();
                    calculateRoute.setEnabled(false);
                }
                //Save first point select
                listPoints.add(latLng);
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1){
                    //Add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                map.addMarker(markerOptions);

                if (listPoints.size() == 2){

                    //Create URL to get request from first marker to second marker
                    cLatOrig = String.valueOf(listPoints.get(0).latitude);
                    cLonOrig = String.valueOf(listPoints.get(0).longitude);
                    cLatDest = String.valueOf(listPoints.get(1).latitude);
                    cLonDest = String.valueOf(listPoints.get(1).longitude);
                    calculateRoute.setEnabled(true);
                    LoadingDialogFragment loadingDialog = new LoadingDialogFragment();
                    loadingDialog.show(getSupportFragmentManager(), "LoadingDialogFragment");
                    Executors.newSingleThreadExecutor().execute(() -> {
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
                                    "\"coordinateXFrom\":"+ cLonOrig  + "," +
                                    "\"coordinateYFrom\":"+ cLatOrig + "," +
                                    "\"coordinateXTo\":"+ cLonDest + "," +
                                    "\"coordinateYTo\":"+ cLatDest  + "," +
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

                            JSONObject response = new JSONObject(new ResponseReader().getResponse(con));
                            if ("00".equals(response.getString("code"))) {
                                JSONArray sections = response.getJSONObject("data").getJSONArray("sections");
                                ArrayList<LatLng> routePoints = new ArrayList<>();
                                for (int i = 0; i < sections.length(); i++) {
                                    JSONArray coordinates = sections.getJSONObject(i).getJSONObject("itinerary").getJSONArray("coordinates");
                                    for (int j = 0; j < coordinates.length(); j++) {
                                        routePoints.add(new LatLng(coordinates.getJSONArray(j).getDouble(1), coordinates.getJSONArray(j).getDouble(0)));
                                    }
                                }
                                con.disconnect();
                                runOnUiThread(() -> {
                                    map.addPolyline(new PolylineOptions().addAll(routePoints).color(getColor(R.color.colorPrimary)));
                                    loadingDialog.dismiss();
                                });
                            }
                            else {
                                runOnUiThread(() -> {
                                    calculateRoute.setEnabled(false);
                                    loadingDialog.dismiss();
                                    Toast noRoutes = Toast.makeText(SelectRouteActivity.this, R.string.recognize_route_error, Toast.LENGTH_SHORT);
                                    noRoutes.show();
                                });
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    public void onLowMemory(){
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    public void onClickCalculate(View view){
        Intent i = new Intent(this, RouteInfoActivity.class);
        i.putExtra("cLatOrig", cLatOrig);
        i.putExtra("cLonOrig", cLonOrig);
        i.putExtra("cLatDest", cLatDest);
        i.putExtra("cLonDest", cLonDest);
        startActivity(i);
    }
}
