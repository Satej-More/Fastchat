package com.example.fastchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastchat.activities.SignInActivity;

public class StartActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Using a Handler to delay the execution of code by 5500 milliseconds (5.5 seconds)
        new Handler().postDelayed(() -> {

            // Creating an Intent to start the SignInActivity
            Intent intent = new Intent(StartActivity.this, SignInActivity.class);

            // Starting the SignInActivity
            startActivity(intent);

            // Finishing the current activity so that the user cannot navigate back to it
            finish();
        }, 5500);// Delay duration in milliseconds




    }


}