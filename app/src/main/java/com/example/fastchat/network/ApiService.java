package com.example.fastchat.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

// Retrofit interface for defining API endpoints
public interface ApiService {

    // Annotation for POST request to "send" endpoint
    @POST("send")

    // Method to send a message
    // Accepts headers and message body as parameters
    Call<String> sendMessage(

            // HeaderMap annotation for dynamic headers
            @HeaderMap HashMap<String, String> headers,
            // Body annotation for message body
            @Body String messageBody

    );
}
