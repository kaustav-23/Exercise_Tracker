package com.example.exercise_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {
    TextView signUpOpt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signUpOpt = findViewById(R.id.tv_si_signUpOpt);

        signUpOpt.setOnClickListener(view -> {
            Intent in = new Intent(SignInActivity.this , SignUpActivity.class);
            startActivity(in);

        });


    }
}