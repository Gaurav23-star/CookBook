package com.cookbook;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;

public class MyViewHolder extends RecyclerView.ViewHolder{

    Item item;
    ImageView imageView, adminView;
    TextView nameView, accountView,timeView, ing1View, ing2View, recipeOwner, likesCountTextView, commentsCountTextView;
    ImageButton comment_button;
    ImageButton like_button;
    static User currentUser;
//    private boolean like_clicked = false;
    private boolean like_clicked;
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
        recipeOwner = itemView.findViewById(R.id.nameView);
        likesCountTextView = itemView.findViewById(R.id.likeCount);
        commentsCountTextView = itemView.findViewById(R.id.commentsCount);
        
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

                update_like_status(String.valueOf(item.getRecipe().getRecipe_id()));
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
    public void bind(Item item, User currentUser){
        this.currentUser = currentUser;
        this.item = item;
    }

    public void update_like_status(String recipe_id){

        final Thread thread = new Thread(new Runnable() {

            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                ApiResponse apiResponse;
                ApiResponse apiResponseTwo;

                like_clicked = !like_clicked;

                if(like_clicked){
                    apiResponse = ApiCaller.get_caller_instance().UserLikesRecipe(String.valueOf(currentUser.getUser_id()), recipe_id );
                    apiResponseTwo = ApiCaller.get_caller_instance().postUserNotification("like", String.valueOf(item.getRecipe().getUser_id()), String.valueOf(currentUser.getUser_id()),  recipe_id);
                }else{
                    apiResponse = ApiCaller.get_caller_instance().UserUnlikesRecipe(String.valueOf(currentUser.getUser_id()), recipe_id);
                    apiResponseTwo = ApiCaller.get_caller_instance().removeUserNotification("like", String.valueOf(item.getRecipe().getUser_id()), String.valueOf(currentUser.getUser_id()),  recipe_id);
                }


                if( (apiResponse != null && apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) && (apiResponseTwo != null && apiResponseTwo.getResponse_code() == HttpURLConnection.HTTP_OK) ){

                    try {

                        // Post a Runnable to the main thread to update the UI
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int currentLikeCount = Integer.parseInt((String) likesCountTextView.getText());
                                if(like_clicked){
                                    likesCountTextView.setText(String.valueOf(currentLikeCount+1));
                                    like_button.setImageResource(R.drawable.like_button_filled);
                                }else{
                                    likesCountTextView.setText(String.valueOf(currentLikeCount-1));
                                    like_button.setImageResource(R.drawable.like_button_empty);
                                }
                            }
                        });

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("ERR in update_like_status ApiResponse");
                }
            }
        });
        thread.start();


    }

    public void has_user_favorited_this_recipe(String user_id, String recipe_id){

        System.out.println("user : "+ user_id);
        System.out.println("recipe_id :  " + recipe_id);
        final Thread thread = new Thread(new Runnable() {

            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().UserHasFavoritedRecipe(user_id, recipe_id);

                if(apiResponse == null){
                    System.out.println("ERROR in user-favorited-this-recipe FUNCTION ");
                    return;
                }

                if( (apiResponse != null && apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK)  ){

                    try {

                        JsonElement root = new JsonParser().parse(apiResponse.getResponse_body());
                        if (root.isJsonArray()) {
                            JsonObject firstObject = root.getAsJsonArray().get(0).getAsJsonObject();
                            int hasLoggedInUserFavoritedThisRecipe = firstObject.get("COUNT(*)").getAsInt();

                            if (hasLoggedInUserFavoritedThisRecipe > 0) {
                                like_clicked = true;
                            } else {
                                like_clicked = false;
                            }

                            // Post a Runnable to the main thread to update the UI
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (like_clicked) {
                                        like_button.setImageResource(R.drawable.like_button_filled);
                                    } else {
                                        like_button.setImageResource(R.drawable.like_button_empty);
                                    }
                                }
                            });

                        }else{
                            System.out.println("The root element is not a JSON array");
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("ERR in update_like_status ApiResponse");
                }
            }
        });
        thread.start();


    }


}
