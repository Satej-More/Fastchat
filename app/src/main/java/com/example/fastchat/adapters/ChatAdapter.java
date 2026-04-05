package com.example.fastchat.adapters;



import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastchat.databinding.ItemContainerReceivedMessageBinding;
import com.example.fastchat.databinding.ItemContainerSentMessageBinding;
import com.example.fastchat.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // List to store chat messages
    private final List<ChatMessage> chatMessages;

    // Bitmap for receiver's profile image
    private  Bitmap receiverProfileImage;

    // ID of the message sender
    private final String senderId;

    // View types for sent and received messages
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED= 2;

    static String encrypt;

    // Method to set the receiver's profile image
    public void setReceiverProfileImage(Bitmap bitmap) {

        receiverProfileImage=bitmap;

    }

    // Constructor to initialize the adapter
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate view holders based on message type (sent or received)
        if (viewType == VIEW_TYPE_SENT) {

            return new SentMessageViewHolder(

                    ItemContainerSentMessageBinding.inflate(

                    LayoutInflater.from(parent.getContext()),

                      parent,
                      false

                      )
            );


        } else {

            return new ReceivedMessageViewHolder(

                    ItemContainerReceivedMessageBinding.inflate(

                            LayoutInflater.from(parent.getContext()),

                            parent,

                            false
                    )
            );
        }

    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind data to view holders based on message type (sent or received)
        if (getItemViewType(position) == VIEW_TYPE_SENT) {

            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));

        } else {

            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);

        }

    }

    @Override
    public int getItemCount() {

        // Return the number of chat messages
        return chatMessages.size();
    }

    @Override

    public int getItemViewType(int position) {
        // Determine the view type based on the message sender
        if (chatMessages.get(position).senderId.equals(senderId)) {

            return VIEW_TYPE_SENT; // If sender ID matches, it's a sent message

        } else {

            return VIEW_TYPE_RECEIVED; // If not, it's a received message

        }
    }

    // View holder for sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        // Method to set data for sent messages
        void setData(ChatMessage chatMessage) {

            binding.textMessage.setText(chatMessage.message); // Set message text
            binding.textDateTime.setText(chatMessage.dateTime); // Set message date and time

        }


    }

    // View holder for received messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder (ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding)
        {
            super(itemContainerReceivedMessageBinding.getRoot());

            binding = itemContainerReceivedMessageBinding;

        }

        // Method to set data for received messages
        void setData (ChatMessage chatMessage, Bitmap receiverProfileImage){

            binding.textMessage.setText(chatMessage.message); // Set message text
            binding.textDateTime.setText(chatMessage.dateTime); // Set message date and time

            if(receiverProfileImage != null) {

                binding.imageProfile.setImageBitmap(receiverProfileImage);  // Set receiver's profile image

            }

        }
    }


}
