package com.cookbook;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.User;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private RecyclerViewInterface recyclerViewInterface;

    Context context;
    List<Item> items;
    int admin;

    public MyAdapter(@NonNull Context context, List<Item> items, RecyclerViewInterface recyclerViewInterface, int admin) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
        this.admin=admin;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false),recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameView.setText(items.get(position).getName());
        holder.accountView.setText(items.get(position).getAuthor());
        holder.imageView.setImageResource(items.get(position).getImage());
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
}
