package com.example.exercise_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class JogActivity extends AppCompatActivity implements SensorEventListener {
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private TextView mStepsSinceReboot;
    private TextView caloriesBurned;
    private boolean isTracking = false;

    private int seconds = 0;
    private boolean running;
    private boolean flagRecordLastSteps = true;
    int calValue = 0;
    private int lastRecordedSteps;

    private Button btnStartTracking;
    private Button btnStopTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jog);

        btnStopTracking = (Button) findViewById(R.id.btn_jog_stop);
        btnStartTracking = (Button) findViewById(R.id.btn_jog_start);
        btnStopTracking.setOnClickListener(stopListener);
        btnStartTracking.setOnClickListener(startListener);


        mStepsSinceReboot =
                (TextView) findViewById(R.id.tv_jog_stepsTaken);
        caloriesBurned = (TextView) findViewById(R.id.tv_jog_kcal);

        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                != null) {
            mSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }

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
            flagRecordLastSteps = true;

            mStepsSinceReboot.setText("0");
            caloriesBurned.setText("0");
            if (isSensorPresent) {
                mSensorManager.registerListener(JogActivity.this, mSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
            isTracking = true;


        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (isSensorPresent) {
                mSensorManager.unregisterListener(JogActivity.this);
            }
            isTracking = false;
            running = false;
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("calories", String.valueOf(calValue));
            myEdit.apply();
        }
    };


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isSensorPresent) {
//            mSensorManager.registerListener(this, mSensor,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
//    }





    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
        if (isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
        isTracking = false;
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("calories", String.valueOf(calValue));
        myEdit.apply();
    }

    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        if(flagRecordLastSteps)
        {
            lastRecordedSteps = (int)event.values[0];
            flagRecordLastSteps = false;
        }
        if(isTracking)
        {
            int steps = (int)event.values[0] - lastRecordedSteps;
            mStepsSinceReboot.setText(String.valueOf(steps));
            calValue = (int)(steps*0.063);
            caloriesBurned.setText(String.valueOf(calValue));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


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

        // Get the text view.
        final TextView timeView
                = (TextView)findViewById(
                R.id.tv_jog_stopwatch);

        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Set the text view text.
                timeView.setText(time);



                // Post the code again
                // with a delay of 1 second.
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



}