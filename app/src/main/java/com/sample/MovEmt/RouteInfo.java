package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RouteInfo extends AppCompatActivity {
    public String response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_info);

        getInfo();
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
    }

    private void getInfo(){
        try {
            URL url = new URL(String.format(EndPoint.INFO_STOP, 123));
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
            response = new ResponseReader().getResponse(con);
            //parseInfoFromJson(response);
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
