package com.example.fastchat.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIl = "email";

    public static final String KEY_PASSWORD = "password";

    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";

    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String KEY_USER_ID = "userId";

    public static final String KEY_IMAGE = "image";

    public static final String KEY_FCM_TOKEN = "fcmToken";

    public static final String KEY_USER = "user";

    public static final String KEY_COLLECTION_CHAT = "chat";

    public static final String KEY_SENDER_ID = "senderId";

    public static final String KEY_RECEIVER_ID = "receiverId";

    public static final String KEY_MESSAGE = "message";

    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";

    public static final String KEY_SENDER_NAME = "senderName";

    public static final String KEY_RECEIVER_NAME = "receiverName";

    public static final String KEY_SENDER_IMAGE = "senderImage";

    public static final String KEY_RECEIVER_IMAGE = "receiverImage";

    public static final String KEY_LAST_MESSAGE = "lastMessage";

    public static final String KEY_AVAILABILITY = "availability";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";

    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_DATA = "data";

    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;

    /**
     * Method to get the headers required for sending remote messages.
     * If the headers have not been initialized yet, they are created and stored in a HashMap.
     * These headers include authorization and content type information.
     *
     * @return HashMap containing the remote message headers
     */
    public static HashMap<String, String> getRemoteMsgHeaders() {
        // Check if the headers have been initialized
        if (remoteMsgHeaders == null) {

            // If not initialized, create a new HashMap to store the headers
            remoteMsgHeaders = new HashMap<>();

            // Add the authorization header with its value
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    " " + com.example.fastchat.BuildConfig.FCM_SERVER_KEY
            );

            // Add the content type header with its value
            remoteMsgHeaders.put(

                    REMOTE_MSG_CONTENT_TYPE, "application/json"

            );


        }
        // Return the HashMap containing the remote message headers
        return remoteMsgHeaders;
    }

}
