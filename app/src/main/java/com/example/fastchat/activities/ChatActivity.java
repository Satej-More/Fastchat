package com.example.fastchat.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.fastchat.adapters.ChatAdapter;
import com.example.fastchat.databinding.ActivityChatBinding;
import com.example.fastchat.models.ChatMessage;
import com.example.fastchat.models.User;
import com.example.fastchat.network.ApiClient;
import com.example.fastchat.network.ApiService;
import com.example.fastchat.utilities.Constants;
import com.example.fastchat.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity{

   // View binding for this activity
    private ActivityChatBinding binding;

   // User object representing the receiver of messages
    private User receiverUser;

    // List to store chat messages
    private List<ChatMessage> chatMessages;

    // Adapter for managing chat message list
    private ChatAdapter chatAdapter;

    // PreferenceManager instance for managing shared preferences
    private PreferenceManager preferenceManager;

    // Firebase Firestore instance for database interaction
    private FirebaseFirestore database;

    // Variables for message encoding and decoding
    String msg,msg1;

    // Unique ID representing the conversation
    private String conversionID = null;

    // Flag to indicate receiver availability
    private Boolean isReceiverAvailable = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());

        // Start Python if not already started
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        // Set content view to the root view of the binding
        setContentView(binding.getRoot());

        // Load receiver details from intent extras
        loadReceiverDetails();

        // Set listeners for UI elements
        setListeners();

        // Initialize chat-related components
        init();

        // Start listening for incoming messages
        listenMessages();

    }

    // Initialize chat-related components
    private void init() {

        // Initialize PreferenceManager instance
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Initialize chat message list
        chatMessages = new ArrayList<>();

        // Initialize chat adapter with chat message list, receiver image, and current user ID
        chatAdapter = new ChatAdapter(

                chatMessages,

                getBitmapFromEncodedString(receiverUser.image),

                preferenceManager.getString(Constants.KEY_USER_ID)

        );

        // Set the adapter to the RecyclerView
        binding.chatRecycleView.setAdapter(chatAdapter);

        // Initialize Firebase Firestore instance
        database= FirebaseFirestore.getInstance();

    }

    // Method to send a message
    private void sendMessage() {
        // Create a message object
        HashMap<String, Object> message = new HashMap<>();
        // Add sender ID, receiver ID, and encoded message
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));

        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);

        msg =binding.inputMessage.getText().toString();

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("FInal_Project");
        PyObject obj = pyObject.callAttr("encode", msg);
        msg = obj.toString();

        message.put(Constants.KEY_MESSAGE, msg);

        // Add timestamp
        message.put(Constants.KEY_TIMESTAMP, new Date());

        // Add message to Firestore
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        // Update or add conversation based on conversation ID
        if(conversionID != null){
            updateConversion(binding.inputMessage.getText().toString());
        }else {
            // Create a new conversation
            // Add sender and receiver details, last message, and timestamp
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString() );
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversion(conversion);
        }

        // Send notification if receiver is not available
        if (!isReceiverAvailable) {

        try {
            // Prepare notification payload
            JSONArray tokens=new JSONArray();

            tokens.put(receiverUser.token);

            JSONObject data=new JSONObject();

            data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
            data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));

            data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

            JSONObject body=new JSONObject();

            body.put(Constants.REMOTE_MSG_DATA, data);

            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            // Send notification
            sendNotification(body.toString());

        } catch (Exception exception) {

            showToast(exception.getMessage());

        }

    }
       // Clear input message field
       binding.inputMessage.setText(null);

}


    // Method to show a toast message
    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    // Method to send a notification
    private void sendNotification(String messageBody) {

        ApiClient.getClient().create(ApiService.class).sendMessage(

                Constants.getRemoteMsgHeaders(),

                messageBody

        ).enqueue(new Callback<String>() {

            @Override

            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if(response.isSuccessful()) {

                    // Handle successful response
                    try {

                        if(response.body() != null) {

                            JSONObject responseJson =new JSONObject(response.body());

                            JSONArray results=responseJson.getJSONArray( "results");

                            if(responseJson.getInt( "failure") ==1) {

                                JSONObject error=(JSONObject)results.get(0);
                                showToast(error.getString( "error"));

                                return;

                            }

                        }

                    }catch (JSONException e) {

                        e.printStackTrace();

                    }

                    showToast("Notification sent successfully");

                }else {

                  showToast("Error: " + response.code());

                }

            }
            @Override
            public void onFailure( @NonNull Call<String> call, @NonNull Throwable t) {

                showToast(t.getMessage());

            }
            });
    }

    // Method to listen for receiver availability
    private void listenAvailabilityOfReceiver() {

        database.collection(Constants.KEY_COLLECTION_USERS).document(

                receiverUser.id

        ).addSnapshotListener( ChatActivity.this, (value, error) -> {

            if(error != null) {

                return;

            }

            if(value != null) {
                // Check receiver availability
                if(value.getLong (Constants.KEY_AVAILABILITY) != null) {
                    int availability= Objects.requireNonNull(

                        value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();

                    isReceiverAvailable = availability == 1;

                }
                // Update receiver details if available
                receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);

                if(receiverUser.image == null) {

                    receiverUser.image = value.getString(Constants.KEY_IMAGE);

                    chatAdapter.setReceiverProfileImage(getBitmapFromEncodedString(receiverUser.image));

                    chatAdapter.notifyItemRangeChanged(  0, chatMessages.size());

                }

            }

            // Update UI based on receiver availability
            if (isReceiverAvailable) {

                binding.textAvailability.setVisibility(View.VISIBLE);

            } else {

                binding.textAvailability.setVisibility(View.GONE);

            }



        });

    }


    // Method to listen for incoming chat messages
    private void listenMessages() {
        // Listen for sender messages
        database.collection(Constants.KEY_COLLECTION_CHAT)

                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        // Listen for receiver messages
                 database.collection(Constants.KEY_COLLECTION_CHAT)
                        .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                        .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                        .addSnapshotListener(eventListener);
    }

    // Event listener for Firestore document changes
    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener =(value , error)  -> {

        if( error != null) {


            return;

        }

        if(value != null) {

            int count=chatMessages.size();

            for (DocumentChange documentChange: value.getDocumentChanges()) {

                if(documentChange.getType()==DocumentChange.Type.ADDED) {
                    // Process added chat messages
                ChatMessage chatMessage = new ChatMessage();

                chatMessage.senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);

                chatMessage.receiverId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                msg1 = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    Python py1 = Python.getInstance();
                    PyObject pyObject1 = py1.getModule("FInal_Project");
                    PyObject obj1 = pyObject1.callAttr("decode", msg1);
                    msg1 = obj1.toString();
                chatMessage.message= msg1;

                chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));

                chatMessage.dataObject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);

                chatMessages.add(chatMessage);

            }

            }

            // Sort messages based on timestamp
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dataObject.compareTo(obj2.dataObject));
            // Notify adapter about data changes
            if (count == 0) {

                chatAdapter.notifyDataSetChanged();

            }else {

                chatAdapter.notifyItemRangeInserted (chatMessages.size(), chatMessages.size());

                binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() - 1);

            }
            // Make chat RecyclerView visible
            binding.chatRecycleView.setVisibility(View.VISIBLE);

        }
        // Hide progress bar
        binding.progressBar.setVisibility(View.GONE);

        // Check for conversation if not already exist
        if(conversionID == null)
        {
            checkForConversion();
        }

    };

    // Method to decode Base64 encoded image string to Bitmap
    private Bitmap getBitmapFromEncodedString(String encodedImage) {

        if (encodedImage != null) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(bytes,  0, bytes.length);

        } else {

            return null;

        }

    }

    // Method to load receiver details from intent extras
    private void loadReceiverDetails() {

        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        assert receiverUser != null;
        binding.textName.setText(receiverUser.name);

    }
    // Method to set listeners for UI elements
    private void setListeners() {
        // Back button click listener
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        // Send button click listener
        binding.layoutSend.setOnClickListener(v -> sendMessage());

    }
    // Method to get readable date and time from Date object
    private String getReadableDateTime (Date date){

        return new SimpleDateFormat("MMMM dd, yyyy hh:mm a",Locale.getDefault()).format(date);

}
    // Method to add a conversation to Firestore
    private void addConversion (HashMap <String, Object> conversion ){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add( conversion )
                .addOnSuccessListener( documentReference -> conversionID = documentReference.getId());
    }

    // Method to update a conversation in Firestore
    private void updateConversion( String message ){
        DocumentReference documentReference =
                database.collection( Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionID);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }


    // Method to check for conversation existence
    public void checkForConversion(){
        if(chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    // Method to check for conversation existence remotely
    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    // Completion listener for checking conversation existence
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionID = documentSnapshot.getId();
  }
};
    @Override

    protected void onResume() {

        super.onResume();
        // Start listening for receiver availability
        listenAvailabilityOfReceiver();

    }
}
