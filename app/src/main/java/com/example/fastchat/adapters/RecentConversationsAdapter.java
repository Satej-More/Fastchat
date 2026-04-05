package com.example.fastchat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastchat.databinding.ItemContainerRecentConversionBinding;
import com.example.fastchat.listeners.ConversionListener;
import com.example.fastchat.models.ChatMessage;
import com.example.fastchat.models.User;


import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    // List to store recent chat messages
    private  final  List<ChatMessage> chatMessages;

    // Listener for conversion clicks
    private final ConversionListener conversionListener;

    // Constructor to initialize the adapter
    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener ) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    // Inflating the layout for the view holder
    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
               ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                         false
                )
        );
    }

    // Binding data to the view holder
    @Override
    public void onBindViewHolder(@NonNull RecentConversationsAdapter.ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));

    }

    // Returning the number of items in the list
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{

        ItemContainerRecentConversionBinding binding;

        // Constructor to initialize the view holder
        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        // Method to set data to the view holder
        void setData(ChatMessage chatMessage) {

            // Setting profile image
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            // Setting user name
            binding.textName.setText(chatMessage.conversionName);
            // Setting recent message
            binding.textRecentMessage.setText(chatMessage.message);
            // Handling click on the item
            binding.getRoot().setOnClickListener(v ->{

                // Creating a user object
                User user =new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;

                // Triggering the click listener with the user object
                conversionListener.OnConversionClicked(user);
            });
        }

    }

    // Method to decode the Base64 encoded image string and return the bitmap
    private Bitmap getConversionImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length);
}

}
