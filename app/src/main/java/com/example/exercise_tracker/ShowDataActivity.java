package com.example.exercise_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ShowDataActivity extends AppCompatActivity {
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("file_name");
        path = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/"+fileName);
        PDFView pdfView = findViewById(R.id.pdfview);
        pdfView.fromFile(path).load();
//        pdfView.fromAsset("fileName").load();
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString("calories", "");
        if(s1.isEmpty()){
            s1 = "0";
        }
        int steps = 0;
        try{
            steps = Integer.parseInt(s1);
        }
        catch (Exception e){
            steps = 0;
            s1 = "0";
        }

        TextView txtView = (TextView)findViewById(R.id.bmi);
        txtView.setText("Calories Burned : " + s1);
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

    @Override
    protected void onPause() {
        super.onPause();
        if(path.exists() && !path.isDirectory()){
            path.delete();
        }
    }
}