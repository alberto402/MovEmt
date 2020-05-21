package com.sample.MovEmt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.MovEmt.R;
import com.sample.MovEmt.busStopItem.BusItem;
import com.sample.MovEmt.busStopItem.BusItemAdapter;
import com.sample.MovEmt.emtApi.Authentication;
import com.sample.MovEmt.emtApi.EndPoint;
import com.sample.MovEmt.emtApi.ResponseReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class StopBusesActivity extends AppCompatActivity {
    private Button back;
    private RecyclerView rvBus;
    private ArrayList<BusItem> alBus;
    private int stopNumber;
    private TextToSpeech textToSpeech;
    private String info;
    private Button buttonSpeech;
    private Boolean pulsado;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pulsado = false;
        setContentView(R.layout.activity_stop_buses);
        Intent intent = getIntent();
        stopNumber = intent.getIntExtra("stopNumber", 0);
        info="";
        alBus = new ArrayList<>();
        // remove, its just for test
        /*alBus.add(new BusItem("82", 2));
        alBus.add(new BusItem("133", 3));
        alBus.add(new BusItem("G", 5));*/

        rvBus = findViewById(R.id.rvBus);
        rvBus.setHasFixedSize(true);

        back = findViewById(R.id.Back);
        back.setOnClickListener(this::onClickBack);

        buttonSpeech = findViewById(R.id.buttonS);
        buttonSpeech.setOnClickListener(this::onClickSound);
        //request data on background
        Thread thread = new Thread(() -> {
            getBuses();
            // update view
            runOnUiThread(()->{
                rvBus.setLayoutManager(new LinearLayoutManager(this));
                rvBus.setAdapter(new BusItemAdapter(alBus));
            });
        });

        //toSpeech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.forLanguageTag("es-ES"));

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        thread.start();
    }

    private void onClickBack(View view) {
        finish();
    }
    void onClickSound(View v){

        if(!pulsado) {
            pulsado=true;
            buttonSpeech.setBackgroundResource(R.drawable.ic_altavoz_cancel_foreground);
            textToSpeech.speak(info, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            buttonSpeech.setBackgroundResource(R.drawable.ic_altavoz_foreground);
            pulsado=false;
            textToSpeech.stop();
        }
    }
    private void getBuses(){
        try {
            URL url = new URL(String.format(EndPoint.ARRIVE_LINES, stopNumber));
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseBusesFromJson(String json){
        String []lines = json.split("\"Arrive\":");
        lines = lines[1].split(", ");
        ArrayList<String> alLine = new ArrayList<>();
        ArrayList<Integer> alTime = new ArrayList<>();
        String aux = "";

        for(String line : lines){
            if(line.contains("line")){
                aux = line.split(": ")[1].replace("\"", "");
                alLine.add(aux);
            }
            else if(line.contains("estimateArrive")){
                aux = line.split(": ")[1];
                alTime.add(Integer.parseInt(aux));
            }
        }

        if(alLine.size() == alTime.size()){
            for(int i = 0; i < alLine.size(); i++) {
                String line = alLine.get(i);
                int time = alTime.get(i);
                if(time/60==0){
                    info+="Autobús de la línea " + line + ", llegando a la parada.";

                }
                else
                    info+="Autobús de la línea " + line + ", llegada estimada en " + time/60 + " minutos.";


                if(time < 999999)
                    alBus.add(new BusItem(line, time / 60));
            }
        }
        else{
            Log.e("StopBusesActivity", "Something went wrong during json parse");
        }
    }
}
