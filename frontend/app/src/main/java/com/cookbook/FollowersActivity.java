package com.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.ApiResponse;
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
    private static User currentUser;
    private static User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        if (getIntent().getSerializableExtra("visiting_user") != null) {
            currentUser = (User) getIntent().getSerializableExtra("visiting_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");

        } else if (getIntent().getSerializableExtra("current_user") != null) {
            current_user = (User) getIntent().getSerializableExtra("current_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");

        }

        if (userList.size() == 0) {
            get_Follower_List_from_server(title);
        } else {
            add_users_to_ui();
        }


    }


    private void add_users_to_ui() {
        RecyclerView recyclerView = findViewById(R.id.followers_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        UserAdapter userAdapter = new UserAdapter(getApplicationContext(), userList, this);
        recyclerView.setAdapter(userAdapter);
    }

    //  ---- Depending on title => Could be the users Follower List or the Users Following List
    private void get_Follower_List_from_server(String title) {

        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {

                ApiResponse apiResponse;

                if (getIntent().getSerializableExtra("visiting_user") != null) {
                    apiResponse = ApiCaller.get_caller_instance().getUsersNetworkList(String.valueOf((currentUser).getUser_id()), title);

                } else {
                    apiResponse = ApiCaller.get_caller_instance().getUsersNetworkList(String.valueOf((loggedInUser).getUser_id()), title);

                }

                if (apiResponse == null) {


                    return;
                }

                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {

                    User[] users = gson.fromJson(apiResponse.getResponse_body(), User[].class);
                    userList.clear();
                    for (User user : users) {

                        addUserThreadSafe(new User(user));

                    }

                    //update user list on main thread
                    handler.post(() -> {
                        add_users_to_ui();
                    });

                } else {

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


        Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
        intent_Person.putExtra("visiting_user", userList.get(position));
        intent_Person.putExtra("current_user", current_user);
        startActivity(intent_Person);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear the items list to ensure it's empty before repopulating it
        userList.clear();
        if (getIntent().getSerializableExtra("visiting_user") != null) {
            currentUser = (User) getIntent().getSerializableExtra("visiting_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");

        } else if (getIntent().getSerializableExtra("current_user") != null) {
            currentUser = (User) getIntent().getSerializableExtra("current_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");
        }

        get_Follower_List_from_server(title);

    }

}