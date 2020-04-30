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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class BusMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int LOCATION_REQUEST = 500;

    private Button btBack;
    private Button btUpdate;
    private ProgressBar pbLoad;
    private MapView mvMap;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_map);

        btBack = findViewById(R.id.btBack);
        btUpdate = findViewById(R.id.btUpdate);
        pbLoad = findViewById(R.id.pbLoad);
        mvMap = findViewById(R.id.mvMap);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);

        mvMap.onCreate(mapViewBundle);
        mvMap.getMapAsync(this);
        btBack.setOnClickListener(this::onClickBack);
        btUpdate.setOnClickListener(this::onClickUpdate);

        // show loading
        switchLoadingState();
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
