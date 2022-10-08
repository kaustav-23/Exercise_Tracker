package com.example.exercise_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NutritionActivity extends AppCompatActivity {
    Button down;
    String steps_data;
    FirebaseStorage firebaseStorage;
    StorageReference str_ref;
    StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        // To check if Internet is Working or not
        boolean connected = internetIsConnected();
        // If Internet Not Connected then return back to home page
        if(!connected){
            Context context = getApplicationContext();
            CharSequence text = "No Internet Connection!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Intent intent = new Intent(NutritionActivity.this, MainActivity.class);
            startActivity(intent);
        }

        Button ntrbtn = findViewById(R.id.ntrbtn);
        down = findViewById(R.id.down);

        // Button Action to Download Required Files
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Download();
            }
        });
        // Button action to View Downloaded Pdf Files
        ntrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NutritionActivity.this, ShowDataActivity.class);
                intent.putExtra("file_name", steps_data);
                startActivity(intent);
            }
        });
    }

    // Function To download Required Files
    //BEGIN
    public void Download(){
        str_ref = firebaseStorage.getInstance().getReference();
        steps_data = gatherLocalData() + ".pdf";
        ref = storageReference.child(steps_data);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                String url = uri.toString();
                DownloadFiles(NutritionActivity.this, "usr_nutrition", ".pdf" ,"assets/", url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e){

            }
        });
    }
    public void DownloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
    }
    //END

    //Function to Gather Step count from Local Storage
    public String gatherLocalData(){
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String steps = sh.getString("steps", "");
        int a;
        try {
            a = Integer.parseInt(steps);
            if(a<=1000){
                return "low";
            }
            if(a>1000 && a<=10000){
                return "medium";
            }
            else{
                return "high";
            }
        }
        catch (Exception e) {
            return "high";
        }
    }

    // Function to Check Internet Connectivity
    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
}