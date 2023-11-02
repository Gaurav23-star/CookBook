package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ProfileActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static User currentUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    static Recipe recipe;

    //private static final String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";
    //private static final String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes?user_id=45";
    private static String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";

    private final Gson gson = new Gson();

    private static final List<Item> items = Collections.synchronizedList(new ArrayList<Item>());

    private TextView server_error_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //retrieve user passed in
        //currentUser = (User) getIntent().getSerializableExtra("current_user");
        if(getIntent().getSerializableExtra("current_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("current_user");
        }
        swipeRefreshLayout = findViewById(R.id.profile_refreshLayout);
        server_error_text = findViewById(R.id.profile_serverErrorTextView);


        setContentView(R.layout.activity_profile);
        //user created recipes not loaded from server
        if(items.size()== 0){
            System.out.println("--------LIST IS EMPTY--------");
            get_user_created_recipes_from_server();

        }else{
            add_recipes_to_ui();
        }



        // ------ Navigation Choice ----
        handleNavigationChange();
    }

    private void add_recipes_to_ui(){
        RecyclerView recyclerView = findViewById(R.id.profile_recyclerview);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //MyAdapter adapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());
        //recyclerView.setAdapter(adapter);
        MyAdapter adapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());
        //recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        //gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void get_user_created_recipes_from_server(){
        final Thread thread = new Thread(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            try {
                RECIPE_URL += "?user_id=" + (currentUser.getUser_id());
                final URL url = new URL(RECIPE_URL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //sets type of request
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-length", "0");
                connection.setDoOutput(false);
                //since get we just need to do this i think?
                connection.connect();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    final InputStream responseBody = connection.getInputStream();
                    //get response
                    String jsonString = convertStreamToString(responseBody);
                    System.out.println("Response body: " + jsonString);

                    JSONArray jsonArray = new JSONArray(jsonString);
                    System.out.println(jsonArray.length());
                    items.clear();
                    //add each item in jsonarray to recyclerview
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.toString());
                        recipe = gson.fromJson(jsonObject.toString(), Recipe.class);
                        System.out.println(recipe.getRecipe_name());
                        addItemThreadSafe(recipe);
                        System.out.println("item added");
                    }

                    //load the ui
                    handler.post(this::add_recipes_to_ui);

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            display_server_down_error("Something went wrong.");
                        }
                    });

                }
            } catch (Exception e) {
                System.out.println("EXCEPTION OCcURRED " + e);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        display_server_down_error("Server is down, Please Try again");
                    }
                });
            }
        });
        thread.start();
    }


    //method to ensure thread safety
    public synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item (recipe));
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void display_server_down_error(String errorText){
        server_error_text.setText(errorText);
        server_error_text.setVisibility(View.VISIBLE);
    }

    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_person);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_person:
                    return true;
                case R.id.bottom_settings:
                    Intent intent_Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent_Settings.putExtra("current_user",currentUser);
                    startActivity(intent_Settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();


                    return true;
                case R.id.bottom_notifications:
                    Intent intent_Notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                    intent_Notifications.putExtra("current_user",currentUser);
                    startActivity(intent_Notifications);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_favorites:
                    Intent intent_Favorites = new Intent(getApplicationContext(), FavoriteActivity.class);
                    intent_Favorites.putExtra("current_user",currentUser);
                    startActivity(intent_Favorites);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_home:
                      Intent intent_Home = new Intent(getApplicationContext(), HomeActivity.class);
                      intent_Home.putExtra("current_user",currentUser);
                      startActivity(intent_Home);
                      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                      finish();

                    return true;
            }
            return false;
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}