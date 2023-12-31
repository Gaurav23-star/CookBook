package com.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    List<Item> items;
    int admin;

    public ProfileAdapter(@NonNull Context context, List<Item> items, RecyclerViewInterface recyclerViewInterface, int admin) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
        this.admin = admin;
    }

    // Inflates the layout for each item view
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        ViewGroup.LayoutParams p = v.getLayoutParams();
        p.width = parent.getWidth() / 3;
        p.height = p.width;
        v.setLayoutParams(p);

        return new MyViewHolder(v, recyclerViewInterface);
    }

    // Binds the data to the views in each item view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        load_recipe_image(position, holder);
        holder.adminView.setImageResource(items.get(position).getAdmin());
        if (admin == 0) {
            holder.adminView.setVisibility(View.GONE);
        }
    }

    // Method to load the recipe image using Glide
    private void load_recipe_image(int position, MyViewHolder holder) {
        String url = ApiCaller.GET_RECIPE_IMAGE_URL + items.get(position).getRecipe().getRecipe_id();

        Glide.with(this.context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).fitCenter().into(holder.imageView);

    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return items.size();
    }
}
