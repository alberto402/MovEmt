package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private CardView cvStop;
    private CardView cvPath;
    //private CardView cvBus;
    private CardView cvVoice;
    //private CardView cvFavStops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cvStop = findViewById(R.id.cvStop);
        cvPath = findViewById(R.id.cvPath);
        //cvBus = findViewById(R.id.cvBus);
        cvVoice = findViewById(R.id.cvVoice);
        //cvFavStops = findViewById(R.id.cvFavStops);

        // set text and icons
        ((TextView)cvStop.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.stop);
        ((ImageView)cvStop.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_count);

        ((TextView)cvPath.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.path);
        ((ImageView)cvPath.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_room_black_24dp);

        //((TextView)cvBus.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.busses);
        //((ImageView)cvBus.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_directions_bus_black_24dp);

        ((TextView)cvVoice.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.voice);
        ((ImageView)cvVoice.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_settings_voice_black_24dp);

        //((TextView)cvFavStops.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.favStops);
        //((ImageView)cvFavStops.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_star_black_24dp);

        cvStop.setOnClickListener(this::onClickEnterStop);
        cvPath.setOnClickListener(this::onClickSelectPath);
        cvVoice.setOnClickListener(this::onClickVoiceCommand);
        //cvBus.setOnClickListener(this::onClickFindBus);
        //cvFavStops.setOnClickListener(this::onClickFavStops);

        Thread thread = new Thread(this::setAuthentication);
        thread.start();
    }

    private void setAuthentication(){
        // set EMT email and password
        Authentication.email = "";
        Authentication.password = "";
        try {
            URL url = new URL(EndPoint.LOGIN);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("email", Authentication.email);
            con.setRequestProperty("password", Authentication.password);

            int status = con.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK){
                Log.e("MainActivity", "Api auth error");
                return;
            }

            String response = new ResponseReader().getResponse(con);
            // parse response
            Authentication.setAccessTokenFromJson(response);

            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void onClickEnterStop(View v){
        Intent intent = new Intent (v.getContext(), CameraToTextActivity.class);
        startActivityForResult(intent, 0);
    }

    void onClickSelectPath(View v){
        Intent intent = new Intent (v.getContext(), SelectRoute.class);
        startActivityForResult(intent, 0);
    }

    /*void onClickFindBus(View v){
    }*/

    void onClickVoiceCommand(View v){
        Intent intent = new Intent (v.getContext(), SpeechViewActivity.class);
        startActivityForResult(intent, 0);
    }

    /*void onClickFavStops(View v){
    }*/
}
