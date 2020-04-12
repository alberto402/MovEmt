package com.sample.MovEmt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


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
        /*cvBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFindBus(v);
            }
        });*/
        cvVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVoiceCommand(v);
            }
        });
        /*cvFavStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFavStops(v);
            }
        });*/
    }

    void onClickEnterStop(View v){
        Intent intent = new Intent (v.getContext(), CameraToTextActivity.class);
        startActivityForResult(intent, 0);
    }

    void onClickSelectPath(View v){
        // TODO
    }

    /*void onClickFindBus(View v){
        // TODO
    }*/

    void onClickVoiceCommand(View v){
        Intent intent = new Intent (v.getContext(), SpeechViewActivity.class);
        startActivityForResult(intent, 0);
    }

    /*void onClickFavStops(View v){
        // TODO
    }*/
}
