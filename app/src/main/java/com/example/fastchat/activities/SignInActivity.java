package com.example.fastchat.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fastchat.databinding.ActivitySignInBinding;
import com.example.fastchat.utilities.Constants;
import com.example.fastchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//import java.util.HashMap;


public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    private  PreferenceManager preferenceManager;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize preference manager
        preferenceManager = new PreferenceManager(getApplicationContext());

        // If the user is already signed in, redirect to MainActivity
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Inflate the layout and set it as the content view
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listeners for sign-up button and sign-in button
        setListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        // Click listener for "Create New Account" text view
        binding.textCreateNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);

        });
        // Click listener for sign-in button
        binding.buttonSignIn.setOnClickListener(v -> {
            // Check if input details are valid, then attempt sign-in
            if(isvalidSignUpDetails()){
                singIn();
            }
        });
//        binding.buttonSignIn.setOnClickListener(v -> addDataToFireStore());

    }

    private void singIn()
    {
        // Show progress bar while signing in
        loading(true);
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIl,binding.InputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.InputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null &&
                            task.getResult().getDocuments().size() > 0)
                    {
                        // User found, save user details and redirect to MainActivity
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    }else {

                        // User not found, show error message
                        loading(false);
                        showToast("Unable to sign in");

                    }
                });

    }

    private void loading(Boolean isLoading)
    {
        // Show or hide progress bar based on isLoading parameter
        if(isLoading) {
        binding.buttonSignIn.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }
    else {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.buttonSignIn.setVisibility(View.VISIBLE);
    }
    }


    private void showToast(String message)
    {
        // Display a toast message
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private Boolean isvalidSignUpDetails() {

        // Validate email and password fields
        if (binding.InputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.InputEmail.getText().toString()).matches()) {
            showToast("Enter Valid Email");
            return false;
        } else if (binding.InputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {

            return true;
        }
    }


//    private void addDataToFireStore(){
//
//        FirebaseFirestore database=FirebaseFirestore.getInstance();
//        HashMap<String,Object> data = new HashMap<>();
//        data.put("first_name","HHH");
//        data.put("last_name","H");
//        database.collection("User")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext() ,"Data Inserted",Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(Exception ->{
//                    Toast.makeText(getApplicationContext(),Exception.getMessage(),Toast.LENGTH_SHORT).show();
//                });
//
//    }
}