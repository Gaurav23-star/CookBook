package com.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.User;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity implements RecyclerViewInterface {

    static User current_user;
    private RecyclerView recyclerView;
    private SearchView searchView;


    List<User> userList = Collections.synchronizedList(new ArrayList<User>());
    private final Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_search);

        if (getIntent().getSerializableExtra("current_user") != null) {
            current_user = (User) getIntent().getSerializableExtra("current_user");
        }
        searchView = findViewById(R.id.userSearchView);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getUserListFromSearchQuery(newText);
                return true;
            }
        });


    }

    private void add_users_to_ui() {
        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        UserAdapter userAdapter = new UserAdapter(getApplicationContext(), userList, this);
        recyclerView.setAdapter(userAdapter);
    }


    @Override
    public void onItemClick(int position) {

        Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
        intent_Person.putExtra("visiting_user", userList.get(position));
        intent_Person.putExtra("current_user", current_user);
        startActivity(intent_Person);

    }

    private void getUserListFromSearchQuery(String text) {

        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {

                ApiResponse apiResponse = ApiCaller.get_caller_instance().getUserSearch(text);


                if (apiResponse == null) {


                    return;
                }

                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {

                    userList.clear();
                    User[] users = gson.fromJson(apiResponse.getResponse_body(), User[].class);

                    for (User user : users) {

                        if (((current_user.getUsername()).equals(user.getUsername()))) {
                            continue;
                        }
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


}