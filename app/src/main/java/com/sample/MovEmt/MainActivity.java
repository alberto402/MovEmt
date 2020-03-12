package com.sample.MovEmt;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
public class MainActivity extends AppCompatActivity {
    private CardView cvStop;
    private CardView cvPath;
    private CardView cvBus;
    private CardView cvVoice;
    private CardView cvFavStops;
    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "YourSubscriptionKey";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "YourServiceRegion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvStop = findViewById(R.id.cvStop);
        cvPath = findViewById(R.id.cvPath);
        cvBus = findViewById(R.id.cvBus);
        cvVoice = findViewById(R.id.cvVoice);
        cvFavStops = findViewById(R.id.cvFavStops);

        // set text and icons
        ((TextView)cvStop.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.stop);
        ((ImageView)cvStop.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_count);

        ((TextView)cvPath.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.path);
        ((ImageView)cvPath.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_room_black_24dp);

        ((TextView)cvBus.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.busses);
        ((ImageView)cvBus.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_directions_bus_black_24dp);

        ((TextView)cvVoice.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.voice);
        ((ImageView)cvVoice.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_settings_voice_black_24dp);

        ((TextView)cvFavStops.findViewById(R.id.llItemOption).findViewById(R.id.tvOption)).setText(R.string.favStops);
        ((ImageView)cvFavStops.findViewById(R.id.llItemOption).findViewById(R.id.ivOption)).setImageResource(R.drawable.ic_star_black_24dp);

        cvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEnterStop(v);
            }
        });
        cvPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelectPath(v);
            }
        });
        cvBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFindBus(v);
            }
        });
        cvVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVoiceCommand(v);
            }
        });
        cvFavStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFavStops(v);
            }
        });
    }

    void onClickEnterStop(View v){
        // TODO
    }

    void onClickSelectPath(View v){
        // TODO
    }

    void onClickFindBus(View v){
        // TODO
    }

    void onClickVoiceCommand(View v){
        // TODO
    }

    void onClickFavStops(View v){
        // TODO
    }
}
