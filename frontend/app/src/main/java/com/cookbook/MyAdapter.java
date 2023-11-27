package com.cookbook;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cookbook.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private RecyclerViewInterface recyclerViewInterface;

    Context context;
    List<Item> items;
    int admin;
    static User currentUser;

    public MyAdapter(@NonNull Context context, List<Item> items, RecyclerViewInterface recyclerViewInterface, int admin, User currentUser) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
        this.admin=admin;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false),recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item,currentUser);
        holder.has_user_favorited_this_recipe(String.valueOf(currentUser.getUser_id()), String.valueOf(item.getRecipe().getRecipe_id()) );

        holder.nameView.setText(items.get(position).getName());
        holder.accountView.setText(items.get(position).getAuthor());
        load_recipe_image(position, holder);
        //holder.imageView.setImageResource(items.get(position).getImage());
        holder.timeView.setText(items.get(position).getTime());
        holder.ing1View.setText(items.get(position).getIng1());
        holder.ing2View.setText(items.get(position).getIng2());
        holder.adminView.setImageResource(items.get(position).getAdmin());
        if(admin==0){
            holder.adminView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private void load_recipe_image(int position, MyViewHolder holder){
        String url = ApiCaller.GET_RECIPE_IMAGE_URL + items.get(position).getRecipe().getRecipe_id();
        System.out.println("REQUEST IMAGE " + url);
        Glide.with(this.context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).dontAnimate().into(holder.imageView);

    }
}
