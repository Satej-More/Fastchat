package com.example.fastchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.fastchat.adapters.RecentConversationsAdapter;
import com.example.fastchat.databinding.ActivityMainBinding;
import com.example.fastchat.listeners.ConversionListener;
import com.example.fastchat.models.ChatMessage;
import com.example.fastchat.models.User;
import com.example.fastchat.utilities.Constants;
import com.example.fastchat.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;

    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize preference manager
        preferenceManager = new PreferenceManager(getApplicationContext());
        // Initialize UI components and Firebase Firestore
        init();
        // Load user details such as name and profile image
        loadUserDetails();
        // Get FCM token
        getToken();
        // Set click listeners for sign-out button and new chat button
        setListeners();
        // Listen for conversation changes
        listenConversations();
    }

    // Initialize UI components and Firebase Firestore
    public void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    // Set click listeners for sign-out button and new chat button
    private void setListeners() {

        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat
                .setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserActivity.class)));

    }

    // Load user details such as name and profile image
    private void loadUserDetails() {

        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    }

    // Show a toast message
    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    // Listen for conversations changes in Firestore
    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    // Event listener for Firestore snapshot changes
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    // Handle added document
                    // Construct ChatMessage object
                    // Add the chat message to conversations list
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage = documentChange.getDocument()
                                .getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument()
                                .getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.conversionImage = documentChange.getDocument()
                                .getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);

                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dataObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    // Handle modified document
                    // Update the corresponding chat message in conversations list
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderId)
                                && conversations.get(i).receiverId.equals(receiverId)) {
                            conversations.get(i).message = documentChange.getDocument()
                                    .getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dataObject = documentChange.getDocument()
                                    .getDate(Constants.KEY_TIMESTAMP);
                            break;

                        }
                    }
                }
            }
            // Sort conversations by timestamp
            // Notify adapter about data changes
            // Scroll to the top of conversations list
            // Show conversations list and hide progress bar
            Collections.sort(conversations, (obj1, obj2) -> obj2.dataObject.compareTo(obj1.dataObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    // Get FCM token for push notifications
    private void getToken() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    // Update FCM token in Firestore
    private void updateToken(String token) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =

                database.collection(Constants.KEY_COLLECTION_USERS).document(

                        preferenceManager.getString(Constants.KEY_USER_ID)

                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)

                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    // Handle sign-out action
    private void signOut() {

        showToast("Signing out...");

        // Remove FCM token from Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =

                database.collection(Constants.KEY_COLLECTION_USERS).document(

                        preferenceManager.getString(Constants.KEY_USER_ID)

                );
        HashMap<String, Object> updates = new HashMap<>();

        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());

        // Clean preference manager
        // Redirect to sign-in activity
        documentReference.update(updates)

                .addOnSuccessListener(unused -> {

                    preferenceManager.clean();

                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));

                    finish();

                })

                .addOnFailureListener(e -> showToast("Unable to sign-out"));

    }

    // Handle conversation item click
    @Override
    public void OnConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

}