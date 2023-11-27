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

    private RecyclerViewInterface recyclerViewInterface;

    Context context;
    List<Item> items;
    int admin;

    public ProfileAdapter(@NonNull Context context, List<Item> items, RecyclerViewInterface recyclerViewInterface, int admin) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
        this.admin=admin;
    }

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

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        load_recipe_image(position, holder);
        //holder.imageView.setImageResource(items.get(position).getImage());
        holder.adminView.setImageResource(items.get(position).getAdmin());
        if(admin==0){
            holder.adminView.setVisibility(View.GONE);
        }
    }

    private void load_recipe_image(int position, MyViewHolder holder){
        String url = ApiCaller.GET_RECIPE_IMAGE_URL + items.get(position).getRecipe().getRecipe_id();
        System.out.println("REQUEST IMAGE " + url);
        Glide.with(this.context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).fitCenter().into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
