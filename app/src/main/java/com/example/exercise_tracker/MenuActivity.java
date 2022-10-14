package com.example.exercise_tracker;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MenuActivity extends AppCompatActivity {
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();
    ViewPager viewPager ;
    Button down;
    String steps_data;
    FirebaseStorage firebaseStorage;
    StorageReference str_ref;
    StorageReference ref;
    ImageView walking , jogging ,cycling ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        down = findViewById(R.id.btn_menu_Download_N_File);
        jogging = findViewById(R.id.imv_menu_jogging);
        walking = findViewById(R.id.imv_menu_walking);
        cycling = findViewById(R.id.imv_menu_cycling);

       walking.setOnClickListener(view -> {
           Intent in = new Intent(MenuActivity.this , WalkActivity.class);
           startActivity(in);
       });
        jogging.setOnClickListener(view -> {
            Intent inte = new Intent(MenuActivity.this , JogActivity.class);
            startActivity(inte);
        });
        cycling.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this , CycleActivity.class);
            startActivity(intent);
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // To check if Internet is Working or not
                boolean connected = internetIsConnected();

//              If Internet Not Connected then return back to home page
                if(!connected){
                    Context context = getApplicationContext();
                    CharSequence text = "No Internet Connection!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return;
                }
                Download();
                File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/"+steps_data);
                if(f.exists() && !f.isDirectory()) {
                    Intent intent = new Intent(MenuActivity.this, ShowDataActivity.class);
                    intent.putExtra("file_name", steps_data);
                    startActivity(intent);
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = "Gathering Nutrition Files. Click Again to View data.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
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


    // Function To download Required Files
    //BEGIN
    public void Download(){
        str_ref = firebaseStorage.getInstance().getReference();
        steps_data = gatherLocalData() + ".pdf";
        ref = str_ref.child(steps_data);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://exercise-tracker-rc-126ba.appspot.com").child(steps_data);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("Main", "File uri: " + uri.toString());
                downloadFile(uri.toString());
            }
        });
    }

    public void downloadFile( String url) {
        try {
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.toString(), steps_data);
            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/"+steps_data);
            if(f.exists() && !f.isDirectory()) return;
            long reference = manager.enqueue(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //End


    //Function to Gather Step count from Local Storage
    public String gatherLocalData(){
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String calories = sh.getString("calories", "");
        int a;
        try {
            a = Integer.parseInt(calories);
            if(a<=5){
                return "low";
            }
            if(a>5 && a<=15){
                return "medium";
            }
            else{
                return "high";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "demoFile";
        }
    }

    private String GetString(String filepath) throws IOException {
        InputStream inputStream = new FileInputStream(filepath);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
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