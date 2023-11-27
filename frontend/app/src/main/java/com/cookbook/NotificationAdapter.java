package com.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.cookbook.model.Notification;
import com.cookbook.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications = Collections.synchronizedList(new ArrayList<Notification>());
    private RecyclerViewInterface recyclerViewInterface;

    public NotificationAdapter(Context context, List<Notification> notifications, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.notifications = notifications;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText("@" + notifications.get(position).getUsername());
        holder.image_profile.setImageResource(R.drawable.chef_profile);
        holder.post_image.setImageResource(R.drawable.foodplaceholder);
        holder.text.setText(notifications.get(position).getText());

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image;
        public TextView username, text;


        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.notification_image_profile);
            post_image = itemView.findViewById(R.id.notification_post_image);
            username = itemView.findViewById(R.id.notification_userName);
            text = itemView.findViewById(R.id.notification_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getBindingAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

        }
    }


}
