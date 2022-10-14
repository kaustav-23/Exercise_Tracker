package com.example.exercise_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class CycleActivity extends AppCompatActivity  {

    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    private TextView caloriesBurned;


    private int seconds = 0;
    private boolean running;
    int calValue = 0;




    private Button btnStartTracking;
    private Button btnStopTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);

        btnStopTracking = (Button) findViewById(R.id.btn_cycle_stop);
        btnStartTracking = (Button) findViewById(R.id.btn_cycle_start);
        btnStopTracking.setOnClickListener(stopListener);
        btnStartTracking.setOnClickListener(startListener);



        caloriesBurned = (TextView) findViewById(R.id.tv_cycle_kcal);



        if (savedInstanceState != null) {

            seconds
                    = savedInstanceState
                    .getInt("seconds");
            running
                    = savedInstanceState
                    .getBoolean("running");

        }
        runTimer();


    }


    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {
            seconds = 0;
            running = true;



            caloriesBurned.setText("0");




        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View v) {


            running = false;

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories", String.valueOf(calValue));
            myEdit.apply();
        }
    };












    @Override
    public void onSaveInstanceState(
            @NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("running", running);
    }

    private void runTimer()
    {


        final TextView timeView
                = (TextView)findViewById(
                R.id.tv_cycle_stopwatch);


        final Handler handler
                = new Handler();


        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;


                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);


                if (running) {
                    seconds++;
                }


                timeView.setText(time);
                calValue = (int)(seconds*0.1275);
                caloriesBurned.setText(String.valueOf(calValue));



                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);

        registerReceiver(airplaneModeChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
    }

}