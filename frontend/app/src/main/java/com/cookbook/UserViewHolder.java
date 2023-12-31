package com.cookbook;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {

    // TextView to display the user name
    TextView userNameTextView;

    // ImageView to display the user profile image
    ImageView user_ProfileImageView;


    // Constructor for the UserViewHolder class
    public UserViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.userName_item_view);
        user_ProfileImageView = itemView.findViewById(R.id.image_profile_item_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewInterface != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            }
        });


    }


}
