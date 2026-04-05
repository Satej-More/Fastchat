package com.example.fastchat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastchat.databinding.ItemContainerUserBinding;
import com.example.fastchat.listeners.UserListener;
import com.example.fastchat.models.User;

import java.util.List;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    // List to store user data
    private final List<User> users;

    // Listener for user clicks
    private final UserListener userListener;

    // Constructor to initialize the adapter
    public UserAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    // Inflating the layout for the view holder
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(

                LayoutInflater.from(parent.getContext()),

                parent,

                 false

);

        return new UserViewHolder(itemContainerUserBinding);
    }

    // Binding data to the view holder
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(users.get(position));

    }

    // Returning the number of items in the list
    @Override
    public int getItemCount() {
        return users.size();
    }

    // View holder class for user items
    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        // Constructor to initialize the view holder
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {

            super(itemContainerUserBinding.getRoot());

            binding = itemContainerUserBinding;

        }

        // Method to set data to the view holder
        void setUserData(User user) {

            // Setting user name
            binding.textName.setText(user.name);

            // Setting user email
            binding.textEmail.setText(user.email);

            // Setting user profile image
            Objects.requireNonNull(binding.imageProfile).setImageBitmap(getUserImage(user.image));

            // Handling click on the item
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));

        }

    }
    // Method to decode the Base64 encoded image string and return the bitmap
    private Bitmap getUserImage(String encodedImage) {

        byte[] bytes;
        bytes = Base64.decode(encodedImage, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(bytes,  0, bytes.length);

    }
}
