package com.cookbook;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView, adminView;
    TextView nameView, accountView,timeView, ing1View, ing2View;
    ImageButton comment_button;
    ImageButton like_button;
    private boolean like_clicked = false;
    public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageview);
        nameView=itemView.findViewById(R.id.name);
        accountView=itemView.findViewById(R.id.account);
        timeView=itemView.findViewById(R.id.time);
        ing1View=itemView.findViewById(R.id.ing1);
        ing2View=itemView.findViewById(R.id.ing2);
        adminView=itemView.findViewById(R.id.admin);
        comment_button = itemView.findViewById(R.id.commentButton);
        like_button = itemView.findViewById(R.id.likeButton);

        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "Comment Clicked", Toast.LENGTH_LONG).show();
                if(recyclerViewInterface!=null){
                    int position = getBindingAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            }
        });

        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //will add server logic later.
                if(like_clicked){
                    like_button.setImageResource(R.drawable.like_button_empty);
                    like_clicked = false;
                }else{
                    like_button.setImageResource(R.drawable.like_button_filled);
                    like_clicked = true;
                }
                System.out.println("VALUE OF LIKE CLICK " + like_clicked);
                Toast.makeText(itemView.getContext(), "Like Clicked", Toast.LENGTH_LONG).show();

            }
        });
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
