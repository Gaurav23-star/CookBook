package com.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    // Context of the adapter
    Context context;

    // Interface to handle item clicks
    private final RecyclerViewInterface recyclerViewInterface;

    // List of users to display in the RecyclerView
    List<User> users = Collections.synchronizedList(new ArrayList<User>());

    // Constructor for the UserAdapter
    public UserAdapter(Context context, List<User> users, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.users = users;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    // Called when RecyclerView needs a new ViewHolder
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item, parent, false), recyclerViewInterface);
    }

    // Called by RecyclerView to display data at a specified position
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.userNameTextView.setText("@" + users.get(position).getUsername());
        holder.user_ProfileImageView.setImageResource(R.drawable.chef_profile);
    }

    // Returns the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        return users.size();
    }
}
