package com.example.fastchat.firebase;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Intent;

import android.os.Build;


import androidx.annotation.NonNull;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.fastchat.R;
import com.example.fastchat.activities.ChatActivity;
import com.example.fastchat.models.User;
import com.example.fastchat.utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    // Called when a new FCM token is generated for the device
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // You may handle the new token here, for example, by sending it to your server
    }

    // Called when a new FCM message is received
    @SuppressLint("MissingPermission")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


        super.onMessageReceived(remoteMessage);
        User user = new User();

        // Creating a User object from the received data
        user.id = remoteMessage.getData().get(Constants.KEY_USER_ID);

        user.name = remoteMessage.getData().get(Constants.KEY_NAME);

        user.token = remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        // Generating a random notification ID
        int notificationId = new Random().nextInt();

        // Setting up notification channel ID
        String channelId = "chat_message";

        // Creating an intent to open the ChatActivity when notification is clicked
        Intent intent = new Intent(this, ChatActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER, user);

        // Creating a PendingIntent for the intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        // Building the notification
        builder.setSmallIcon(R.drawable.ic_notification);

        builder.setContentTitle(user.name);

        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));

        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get(Constants.KEY_MESSAGE)

        ));

        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);

        // Creating notification channel (for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence channelName = "Chat Message";

            String channelDescription = "This notification channel is used for chat message notifications";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        // Displaying the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                 notificationManagerCompat.notify(notificationId, builder.build());
    }


    }

