package com.example.fastchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;



import com.example.fastchat.adapters.UserAdapter;
import com.example.fastchat.databinding.ActivityUserBinding;
import com.example.fastchat.listeners.UserListener;
import com.example.fastchat.models.User;
import com.example.fastchat.utilities.Constants;
import com.example.fastchat.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity  extends BaseActivity implements UserListener {

    private ActivityUserBinding binding;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize PreferenceManager
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Set listeners
        setListeners();

        // Fetch users from Firestore
        getUsers();
    }

    // Set click listeners
    private void setListeners() {

        binding.imageBack.setOnClickListener(v -> onBackPressed() );

    }

    // Fetch users from Firestore
    private void getUsers() {



        loading(  true);

        // Get instance of FirebaseFirestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Retrieve users from Firestore
        database.collection(Constants.KEY_COLLECTION_USERS)

                .get()

                .addOnCompleteListener(task -> {

                    loading(  false);
                    // Get current user ID from PreferenceManager
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

                    if(task.isSuccessful() && task.getResult() != null) {

                        List<User> users =new ArrayList<>();
                        // Iterate through each document in the result
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {
                            // Skip the current user
                            if(currentUserId.equals(queryDocumentSnapshot.getId())) {

                                continue;

                            }

                            User user = new User();
                            // Create a User object from the document data
                            user.name= queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email= queryDocumentSnapshot.getString(Constants.KEY_EMAIl);
                            user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();
                            users.add(user);

                        }

                        // If users are found, display them using UserAdapter
                        if ( users.size()> 0) {

                            UserAdapter userAdapter = new UserAdapter(users,this);
                            binding.usersRecyclerView.setAdapter(userAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);

                        }else {

                            // Show error message if no users are available
                            showErrorMessage();
                        }

                    }else {

                        // Show error message if fetching users fails
                        showErrorMessage();
                    }

                });

    }

    // Show error message when no users are available
    private void showErrorMessage() {

        binding.textErrorMessage.setText(String.format("%s", "No user available"));
                binding.textErrorMessage.setVisibility(View.VISIBLE);

    }


    // Show or hide loading progress
    private void loading (Boolean isLoading) {

        if (isLoading) {

            binding.progressBar.setVisibility(View.VISIBLE);

        } else {

            binding.progressBar.setVisibility(View. INVISIBLE);

        }

    }


    // Handle user click event
    @Override
    public void onUserClicked(User user) {

        // Start ChatActivity with selected user
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

        intent.putExtra(Constants.KEY_USER, user);

        startActivity(intent);

        finish();

    }


}