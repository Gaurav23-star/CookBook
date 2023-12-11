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
    Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    List<User> users = Collections.synchronizedList(new ArrayList<User>());

    public UserAdapter(Context context, List<User> users, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.users = users;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.userNameTextView.setText("@" + users.get(position).getUsername());
        holder.user_ProfileImageView.setImageResource(R.drawable.chef_profile);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
