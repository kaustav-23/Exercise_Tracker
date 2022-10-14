package com.example.exercise_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    TextView signInOpt;
    Button signUp;
    TextInputLayout layoutUsername ,layoutEmailId , layoutPassword ,layoutContactNo ,
            layoutAge ,layoutWeight,layoutHeight;
    String name ,gender ,contact , email , password , age , height ,weight ;
    TextInputEditText name_tiet , email_tiet , password_tiet , contactNo_tiet ,
            age_tiet ,height_tiet ,weight_tiet ;

    RadioGroup radioGroup;

    FirebaseAuth auth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signInOpt = findViewById(R.id.tv_su_signInOpt);

        signInOpt = findViewById(R.id.tv_su_signInOpt);
        signUp = findViewById(R.id.btn_su_submit);

        layoutUsername = findViewById(R.id.layout_su_userName);
        layoutEmailId = findViewById(R.id.layout_su_email);
        layoutPassword = findViewById(R.id.layout_su_password);
        layoutContactNo = findViewById(R.id.layout_su_phoneNo);
        layoutAge = findViewById(R.id.layout_su_age);
        layoutWeight = findViewById(R.id.layout_su_weight);
        layoutHeight = findViewById(R.id.layout_su_height);

        name_tiet = findViewById(R.id.tiet_su_userName);
        email_tiet = findViewById(R.id.tiet_su_email);
        password_tiet = findViewById(R.id.tiet_su_password);
        contactNo_tiet = findViewById(R.id.tiet_su_phoneNumber);
        age_tiet = findViewById(R.id.tiet_su_age);
        height_tiet = findViewById(R.id.tiet_su_height);
        weight_tiet = findViewById(R.id.tiet_su_weight);


        //Firebase instance creation
        auth = FirebaseAuth.getInstance();

        //FirebaseFirestore Instance creation
        db = FirebaseFirestore.getInstance();

        gender = "male";



        signInOpt.setOnClickListener(view -> {
            Intent in = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(in);

        });

        signUp.setOnClickListener(view -> {

            if (validateFields()) {
                doRegister();
            }
        });
    }
        private void doRegister() {
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnSuccessListener(task -> {
                FirebaseUser fb = auth.getCurrentUser();
                if (fb != null)
                {
                    String uId = fb.getUid();
                    fb.sendEmailVerification();
                    storeInfo(uId);
                }
                else
                {
                    Toast.makeText(this, "Try later..", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(e -> Toast.makeText(this, "Sorry. " +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }

        private void storeInfo(String usrId) {
            Map<String, String> userMap = new HashMap<>();

            userMap.put("name" , name); //key - "name" , value - name
            userMap.put("email" , email);
            userMap.put("contact" , contact);
            userMap.put("gender" , gender);
            userMap.put("height" , height);
            userMap.put("weight" , weight);
            userMap.put("age" , age);
            userMap.put("password" , password);



            db.collection("user_info")
                    .document(usrId)
                    .set(userMap)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(SignUpActivity.this , "Registration Successful" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this , SignInActivity.class);
                        startActivity(intent);
                        SignUpActivity.this.finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity.this , e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }

        private boolean validateFields() {

            layoutUsername.setError("");
            layoutEmailId.setError("");
            layoutPassword.setError("");
            layoutContactNo.setError("");
            layoutAge.setError("");
            layoutWeight.setError("");
            layoutHeight.setError("");


            contact = contactNo_tiet.getText().toString();
            password = password_tiet.getText().toString();
            age = age_tiet.getText().toString();
            height = height_tiet.getText().toString();
            weight = weight_tiet.getText().toString();
            name = name_tiet.getText().toString();
            email = email_tiet.getText().toString();
            boolean flag = false ;

            if (gender == null) {
                Toast.makeText(SignUpActivity.this, "choose gender", Toast.LENGTH_SHORT).show();
                flag = true ;
            }
            if(email.length() == 0 || !(Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            {
                layoutEmailId.setError("Please enter your valid Email I'd");
                flag = true ;
            }

            if(password.length() < 8)
            {
                layoutPassword.setError("Password should contain at least 8 characters");
                flag = true ;
            }
            else if(!password.matches("^(?=.*[_.()$&@]).*$"))
            {
                layoutPassword.setError("Password should contain at least one special symbol");
                flag = true ;
            }
            else if(!password.matches("(.*[0-9].*)"))
            {
                layoutPassword.setError("Password should contain at least one digit");
                flag = true ;
            }
            else if(!password.matches("(.*[A-Z].*)"))
            {
                layoutPassword.setError("Password should contain at least one upper case letter");
                flag = true ;
            }

            if(age.isEmpty() ){
                layoutAge.setError("Please enter Age");
                flag = true ;
            }
            else if(Integer.parseInt(age) > 80 || Integer.parseInt(age) < 10 ){
                layoutAge.setError("Your age should be between 10 - 80");
                flag = true ;
            }

            if(height.isEmpty() ){
                layoutHeight.setError("Please enter Height");
                flag = true ;
            }
            else if(Float.parseFloat(height) > 3 || Float.parseFloat(height)  <= 0 ){
                layoutHeight.setError("Your height should be between 0.1 - 3 M");
                flag = true ;
            }

            if(weight.isEmpty() ){
                layoutWeight.setError("Please enter Weight");
                flag = true ;
            }
            else if(Float.parseFloat(weight) > 200 || Float.parseFloat(weight)  <= 20 ){
                layoutWeight.setError("Your weight should be between 20 - 200 Kg");
                flag = true ;
            }

            if(name.isEmpty() ){
                layoutUsername.setError("Please enter Name");
                flag = true ;
            }

            if(contact.isEmpty() ){
                layoutContactNo.setError("Please enter Contact No");
                flag = true ;
            }
            else if(contact.length() != 10 ){
                layoutContactNo.setError("Please enter a valid Contact No");
                flag = true ;
            }

            return !flag ;

        }





    //-------------------------------Broadcast Receiver ------------------------------------

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




//------------------------------------------------------------------------------------------------------------

