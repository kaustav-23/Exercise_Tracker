package com.example.exercise_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignInActivity extends AppCompatActivity {
    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();

    TextView signUpOpt;
    Button login_btn ;
    TextInputLayout email_layout,password_layout;
    TextInputEditText password_login,email_login;
    String email, password;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login_btn = findViewById(R.id.btn_si_signIn);
        email_layout = findViewById(R.id.layout_si_email);
        password_layout = findViewById(R.id.layout_signIn_pwd);
        password_login = findViewById(R.id.tiet_si_pwd);
        email_login = findViewById(R.id.tiet_si_email);
        signUpOpt = findViewById(R.id.tv_si_signUpOpt);

        signUpOpt.setOnClickListener(view -> {
            Intent in = new Intent(SignInActivity.this , SignUpActivity.class);
            startActivity(in);
        });

        login_btn.setOnClickListener(view -> {

            if(validateFields())
            {
                doLogIn();
            }

        });



    }

    private void doLogIn() {
        try {
            firebaseAuth  = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser fb = authResult.getUser();

                        if (fb != null) {

  //                         if (fb.isEmailVerified()) {
                                Intent in = new Intent(SignInActivity.this, MenuActivity.class);
                                startActivity(in);
                                SignInActivity.this.finish();
   //                         } else {
   //                             Toast.makeText(this, "Please verify your email.", Toast.LENGTH_SHORT).show();
   //                         }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Sorry " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        email_layout.setError("");
        password_layout.setError("");
        password = password_login.getText().toString().trim();
        email = email_login.getText().toString().trim();
//        email = "Tom@gmail.com";
//        password = "T@12345678";
        boolean flag = false ;
        if (email.isEmpty()){
            email_layout.setError("Enter valid email address");
            flag = true ;
        }
        if (password.isEmpty()){
            password_layout.setError("Enter valid password");
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


