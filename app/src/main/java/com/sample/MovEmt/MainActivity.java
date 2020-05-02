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
    private CardView cvBus;
    private CardView cvVoice;
    private CardView cvinfPa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cvStop = findViewById(R.id.cvStop);
        cvPath = findViewById(R.id.cvPath);
        cvBus = findViewById(R.id.cvBus);
        cvVoice = findViewById(R.id.cvVoice);
        cvinfPa = findViewById(R.id.cvinfPa);

        // set text and icons
        ((TextView)cvStop.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.stop);
        ((ImageView)cvStop.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_count);

        ((TextView)cvPath.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.path);
        ((ImageView)cvPath.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_room_black_24dp);

        ((TextView)cvBus.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.buses);
        ((ImageView)cvBus.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_directions_bus_black_24dp);

        ((TextView)cvVoice.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.voice);
        ((ImageView)cvVoice.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_settings_voice_black_24dp);

        ((TextView)cvinfPa.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText("Información parada");
        ((ImageView)cvinfPa.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_launcher_info);

        cvStop.setOnClickListener(this::onClickEnterStop);
        cvPath.setOnClickListener(this::onClickSelectPath);
        cvVoice.setOnClickListener(this::onClickVoiceCommand);
        cvBus.setOnClickListener(this::onClickFindBus);
        cvinfPa.setOnClickListener(this::onClickInfoStop);

        Thread thread = new Thread(this::setAuthentication);
        thread.start();
    }

    private void setAuthentication(){
        // set EMT email and password
        Authentication.email = "acfucm2019@gmail.com";
        Authentication.password = "Acf2019ADR";
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
        intent.putExtra("info", false);
        startActivityForResult(intent, 0);
    }

    void onClickSelectPath(View v){
        Intent intent = new Intent (v.getContext(), SelectRoute.class);
        startActivityForResult(intent, 0);
    }

    void onClickFindBus(View v){
        Intent intent = new Intent(this, BusMapActivity.class);
        startActivity(intent);
    }

    void onClickVoiceCommand(View v){
        Intent intent = new Intent (v.getContext(), SpeechViewActivity.class);
        startActivityForResult(intent, 0);
    }

    void onClickInfoStop(View v){
        Intent intent = new Intent (v.getContext(), CameraToTextActivity.class);
        intent.putExtra("info", true);
        startActivityForResult(intent, 0);
    }
}
