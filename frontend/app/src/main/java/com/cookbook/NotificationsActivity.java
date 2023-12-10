package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import com.cookbook.model.Notification;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static User current_user;

    List<Notification> notificationList = Collections.synchronizedList(new ArrayList<Notification>());

    private final Gson gson = new Gson();

    private TextView noNotificationsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    Recipe curr_recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Notifications");

        //retrieve user passed in
        if(getIntent().getSerializableExtra("current_user") != null){
            current_user = (User) getIntent().getSerializableExtra("current_user");
        }
        noNotificationsTextView = findViewById(R.id.noNotificationTextView);
        swipeRefreshLayout = findViewById(R.id.notifications_refresh_layout);

        if(notificationList.size() ==0){
            get_notification_list_from_server();
        }else{
            add_notifications_to_ui();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationList.clear();
                get_notification_list_from_server();
                swipeRefreshLayout.setRefreshing(false);
                add_notifications_to_ui();
            }
        });

        handleNavigationChange();
    }



    //method to ensure thread safety
    public synchronized void addNotificationThreadSafe(Notification notification) {
        notificationList.add(notification);
    }

    private void changeActivityToRecipeActivity(User user, Recipe recipe){
        final Intent intent = new Intent(NotificationsActivity.this, RecipeActivity.class);
        intent.putExtra("current_user",user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }

    private void add_notifications_to_ui(){
        noNotificationsTextView.setVisibility(View.GONE);
        RecyclerView recyclerView = findViewById(R.id.notifications_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter notificationAdapter = new NotificationAdapter(getApplicationContext(), notificationList, this);
        recyclerView.setAdapter(notificationAdapter);

    }

    private void get_notification_list_from_server(){

        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {

                ApiResponse apiResponse = ApiCaller.get_caller_instance().getUsersNotifications( String.valueOf((current_user).getUser_id()));


                if(apiResponse == null){
                    System.out.println("Server is down, Please Try again");
                    System.out.println("-errr in get_notification_list_from_server() ");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){

                        Notification[] notifications = gson.fromJson(apiResponse.getResponse_body(), Notification[].class);
                        notificationList.clear();

                    for(Notification notification : notifications){
                        System.out.println( notification.getText());
                        System.out.println( notification.getId());
                        addNotificationThreadSafe(new Notification(notification));
                        System.out.println("notifications user Data : "+ notification.getFrom_user_id());
                    }

                    //sort by notification id's (instead of creation time, it should be the same)

                    synchronized (notificationList) {
                        Collections.sort(notificationList, new Comparator<Notification>() {
                            @Override
                            public int compare(Notification n1, Notification n2) {
                                String id1 = n1.getId();
                                String id2 = n2.getId();

                                // Default to 0 if id is null
                                int intId1 = (id1 != null) ? Integer.parseInt(id1) : 0;
                                int intId2 = (id2 != null) ? Integer.parseInt(id2) : 0;
                                return intId2 - intId1;
                            }
                        });
                    }


                    //update user list on main thread
                    if(notificationList.size() > 0){
                        handler.post(() ->{
                            add_notifications_to_ui();
                        });
                    }

                }else{
                    System.out.println("Server is down, Please Try again");
                }
            }
        });

        thread.start();

    }

    private void get_recipe_from_server(String recipe_id){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {

                ApiResponse apiResponse = ApiCaller.get_caller_instance().getNotificationRecipe(recipe_id);

                if(apiResponse == null){
                    System.out.println("Server is down, Please Try again");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){

                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);

                    for(Recipe recipe: recipes) {
                        if (recipe == null) System.out.println("Recipe is null");
                        else curr_recipe = recipe;
                    }

                }else{
                    System.out.println("Server is down, Please Try again");
                }
            }
        });

        thread.start();


    }


    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_notifications);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_notifications:
                    return true;
                case R.id.bottom_person:
                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user",current_user);
                    startActivity(intent_Person);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_settings:
                    Intent intent_Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent_Settings.putExtra("current_user",current_user);
                    startActivity(intent_Settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_favorites:
                    Intent intent_Favorites = new Intent(getApplicationContext(), FavoriteActivity.class);
                    intent_Favorites.putExtra("current_user",current_user);
                    startActivity(intent_Favorites);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                    return true;

                case R.id.bottom_home:
                    Intent intent_Home = new Intent(getApplicationContext(), HomeActivity.class);
                    intent_Home.putExtra("current_user",current_user);
                    startActivity(intent_Home);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();

                    return true;
            }
            return false;
        });
    }


    @Override
    public void onItemClick(int position) {

        if(notificationList.get(position).getType().equals("follow")){
            return;
        }

        get_recipe_from_server(notificationList.get(position).getPost_id());

        if ( (curr_recipe != null) ) {
            changeActivityToRecipeActivity(current_user, curr_recipe);
        }


    }

}