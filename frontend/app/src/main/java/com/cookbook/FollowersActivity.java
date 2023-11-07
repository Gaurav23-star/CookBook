package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toolbar;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FollowersActivity extends AppCompatActivity implements RecyclerViewInterface {

    List<User> userList = Collections.synchronizedList(new ArrayList<User>());
    private final Gson gson = new Gson();

    String id;
    String title;
    static User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        if(getIntent().getSerializableExtra("current_user") != null){
            current_user = (User) getIntent().getSerializableExtra("current_user");
        }



       if(userList.size()==0){
           get_Follower_List_from_server(title);
       }else{
           add_users_to_ui();
       }


    }


    private void add_users_to_ui(){
        RecyclerView recyclerView = findViewById(R.id.followers_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        UserAdapter userAdapter = new UserAdapter(getApplicationContext(),userList,this);
        recyclerView.setAdapter(userAdapter);
    }

    //  ---- Depending on title => Could be the users Follower List or the Users Following List
    private void get_Follower_List_from_server(String title) {

        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {

                ApiResponse apiResponse = ApiCaller.get_caller_instance().getUsersNetworkList( String.valueOf((current_user).getUser_id()), title);


                if(apiResponse == null){
                    System.out.println("Server is down, Please Try again");
                    System.out.println("-0230--03940-2940-392-4923---------");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){

                    User[] users = gson.fromJson(apiResponse.getResponse_body(), User[].class);

                    for(User user : users){

                        addUserThreadSafe(new User(user));

                    }

                    //update user list on main thread
                    handler.post(() ->{
                        add_users_to_ui();
                    });

                }else{
                    System.out.println("Server is down, Please Try again");
                }
            }
        });

        thread.start();


    }


    //method to ensure thread safety
    public synchronized void addUserThreadSafe(User user) {
        userList.add(user);
    }

    @Override
    public void onItemClick(int position) {
        System.out.println(position );
        System.out.println("OIFOISDJFNIODSNFDIONF");
    }
}