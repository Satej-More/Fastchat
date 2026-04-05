package com.example.fastchat.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fastchat.utilities.Constants;
import com.example.fastchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Initialize PreferenceManager to access SharedPreferences
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());

        // Initialize FirebaseFirestore instance
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Construct the document reference for the current user based on user ID
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)

                .document(preferenceManager.getString(Constants.KEY_USER_ID));
    }

    @Override

    protected void onPause() {

        super.onPause();

        // Update the user's availability status to 0 (offline) when the activity is paused
        documentReference.update(Constants.KEY_AVAILABILITY, 0);

    }

    @Override


    protected void onResume() {

        super.onResume();

        // Update the user's availability status to 1 (online) when the activity is resumed
        documentReference.update(Constants.KEY_AVAILABILITY, 1);

    }

}


