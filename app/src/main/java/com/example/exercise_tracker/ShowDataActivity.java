package com.example.exercise_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class ShowDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        Intent intent = getIntent();
        String fileName = intent.getExtras().getString("file_name");
        PDFView pdfView = findViewById(R.id.pdfview);
        pdfView.fromAsset(fileName).load();
    }
}