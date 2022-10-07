package com.example.exercise_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    TextView signInOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signInOpt = findViewById(R.id.tv_su_signInOpt);

        signInOpt.setOnClickListener(view -> {
            Intent in = new Intent(SignUpActivity.this , SignInActivity.class);
            startActivity(in);

        });

    }
}