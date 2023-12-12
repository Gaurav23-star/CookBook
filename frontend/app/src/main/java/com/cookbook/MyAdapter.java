package com.cookbook;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cookbook.model.ApiResponse;
import com.cookbook.model.User;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    // Interface for handling item click events
    private final RecyclerViewInterface recyclerViewInterface;

    // Context of the adapter
    Context context;
    // List of items to be displayed in the RecyclerView
    List<Item> items;
    int admin;
    static User currentUser;

    // Constructor for the adapter
    public MyAdapter(@NonNull Context context, List<Item> items, RecyclerViewInterface recyclerViewInterface, int admin, User currentUser) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
        this.admin = admin;
        MyAdapter.currentUser = currentUser;
    }

    // Inflates the view for each item in the RecyclerView
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.new_item_view, parent, false), recyclerViewInterface);
    }

    // Binds the data to the ViewHolder for each item
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, currentUser);
        holder.has_user_favorited_this_recipe(String.valueOf(currentUser.getUser_id()), String.valueOf(item.getRecipe().getRecipe_id()));
        loadRecipeOwnerProfile(String.valueOf(item.getRecipe().getUser_id()), holder);
        holder.nameView.setText(items.get(position).getName());
        //holder.accountView.setText(items.get(position).getAuthor());
        load_recipe_image(position, holder);
        //holder.imageView.setImageResource(items.get(position).getImage());
        holder.timeView.setText(items.get(position).getTime());
        holder.ing1View.setText(items.get(position).getIng1());
        holder.ing2View.setText(items.get(position).getIng2());
        holder.adminView.setImageResource(items.get(position).getAdmin());
        holder.likesCountTextView.setText(String.valueOf(item.getRecipe().getNum_likes()));
        holder.commentsCountTextView.setText(String.valueOf(item.getRecipe().getNum_comments()));

        if (admin == 0) {
            holder.adminView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    // Loads the recipe image using Glide
    private void load_recipe_image(int position, MyViewHolder holder) {
        String url = ApiCaller.GET_RECIPE_IMAGE_URL + items.get(position).getRecipe().getRecipe_id();

        Glide.with(this.context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.imageView);

    }

    // Loads the profile of the recipe owner
    private void loadRecipeOwnerProfile(String userId, MyViewHolder viewHolder) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ApiResponse response = ApiCaller.get_caller_instance().getUserFromUserId(userId);
                    if (response != null && response.getResponse_code() == HttpURLConnection.HTTP_OK) {
                        User user = new Gson().fromJson(response.getResponse_body(), User.class);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            //String displayName = recipeOwner.getFirst_name() + " " + recipeOwner.getLast_name();
                            String displayName = user.getUsername();
                            viewHolder.recipeOwner.setText(displayName);
                        });
                    }
                } catch (Exception e) {


                }
            }
        });

        thread.start();
    }

}
