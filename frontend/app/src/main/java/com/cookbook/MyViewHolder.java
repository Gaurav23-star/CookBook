package com.cookbook;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView nameView, accountView;
    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageview);
        nameView=itemView.findViewById(R.id.name);
        accountView=itemView.findViewById(R.id.account);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewInterface!=null){
                    int position = getBindingAdapterPosition();

                    if(position!= RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            }
        });
    }
}
