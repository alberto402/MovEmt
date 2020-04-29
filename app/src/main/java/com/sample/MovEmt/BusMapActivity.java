package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.maps.MapView;

public class BusMapActivity extends AppCompatActivity {

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

        // show loading
        switchLoadingState();

        btBack.setOnClickListener(this::onClickBack);
        btUpdate.setOnClickListener(this::onClickUpdate);
    }


    private void switchLoadingState(){
        runOnUiThread(() -> {
            isLoading = !isLoading;
            if(isLoading){
                btUpdate.setVisibility(View.GONE);
                mvMap.setVisibility(View.GONE);
                pbLoad.setVisibility(View.VISIBLE);
            }
            else {
                btUpdate.setVisibility(View.VISIBLE);
                mvMap.setVisibility(View.VISIBLE);
                pbLoad.setVisibility(View.GONE);
            }
        });
    }

    private void onClickBack(View view){
        finish();
    }

    private void onClickUpdate(View view){

    }
}
