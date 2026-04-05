package com.example.fastchat.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;

    // Constructor for PreferenceManager, initializes SharedPreferences instance
    public PreferenceManager(Context context)
    {
        // Get SharedPreferences instance using the provided context and preference name
          sharedPreferences =context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    // Method to store a boolean value in SharedPreferences
    public void putBoolean(String key, Boolean value)
    {
        // Start editing the SharedPreferences
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put the boolean value with the provided key
        editor.putBoolean(key,value);
        // Apply changes
        editor.apply();

    }

    // Method to retrieve a boolean value from SharedPreferences
    public Boolean getBoolean(String key)
    {
        // Retrieve boolean value associated with the provided key, default value is false
        return sharedPreferences.getBoolean(key,false);

    }

    // Method to store a string value in SharedPreferences
    public void putString(String key,String value)
    {
        // Start editing the SharedPreferences
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        // Put the string value with the provided key
        editor.putString(key, value);
        // Apply changes
        editor.apply();
    }
    // Method to retrieve a string value from SharedPreferences
    public String getString(String key){
        // Retrieve string value associated with the provided key, default value is null
        return sharedPreferences.getString(key,null);
    }

    // Method to clear all stored preferences
    public void clean()
    {
        // Start editing the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all stored preferences
        editor.clear();
        // Apply changes
        editor.apply();
    }

}
