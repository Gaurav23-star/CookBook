package com.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cookbook.model.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private List<Notification> notifications = Collections.synchronizedList(new ArrayList<Notification>());
    private final RecyclerViewInterface recyclerViewInterface;


    public NotificationAdapter(Context context, List<Notification> notifications, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.notifications = notifications;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    // Inflates the layout for each item in the RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false), recyclerViewInterface);
    }

    // Binds data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText("@" + notifications.get(position).getUsername());
        holder.text.setText(notifications.get(position).getText());
        holder.image_profile.setImageResource(R.drawable.chef_profile);
        holder.post_image.setImageResource(0);//set to nothing

        if (!notifications.get(position).getText().equals("is following you")) {
            load_recipe_image(position, holder);
        }

    }

    // Loads a recipe image using Glide
    private void load_recipe_image(int position, ViewHolder holder) {
        String url = ApiCaller.GET_RECIPE_IMAGE_URL + notifications.get(position).getPost_id();

        Glide.with(this.context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.post_image);

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    // ViewHolder class for holding views of each item
    public class ViewHolder extends RecyclerView.ViewHolder {

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


}
