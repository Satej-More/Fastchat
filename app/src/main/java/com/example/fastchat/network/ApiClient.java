package com.example.fastchat.network;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    // Retrofit instance for making network requests
    private static Retrofit retrofit = null;

    // Method to get the Retrofit client instance
    public static Retrofit getClient() {

        // Check if Retrofit instance is null
        if (retrofit == null) {

            // Create a new Retrofit instance
            retrofit = new Retrofit.Builder()

                    // Set the base URL for the API endpoints
                    .baseUrl("https://fcm.googleapis.com/fcm/")

                    // Add a converter factory for Scalars (converts to plain text)
                    .addConverterFactory (ScalarsConverterFactory.create())

                    // Build the Retrofit instance
                    .build();

        }

        // Return the Retrofit instance
        return retrofit;

    }
}
