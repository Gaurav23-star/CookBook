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
import java.util.List;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity implements RecyclerViewInterface {
    private static User currentUser;
    private static final List<Item> items = Collections.synchronizedList(new ArrayList<Item>());
    private TextView server_error_text;
    private TextView favoritesPageTextView;
    private final Gson gson = new Gson();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Objects.requireNonNull(getSupportActionBar()).setTitle("FAVORITES");
        //retrieve user passed in
        currentUser = (User) getIntent().getSerializableExtra("current_user");
        server_error_text = findViewById(R.id.serverErrorTextView);
        favoritesPageTextView = findViewById(R.id.favoritesPageTextView);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        //if recipes not loaded from server, then load
        if(items.size() == 0){
            System.out.println("LIST IS EMPTY");
            get_recipes_from_server();
        }
        //if we already have recipes loaded, then
        // just update the UI, no need to reload from server again
        else{
            add_recipes_to_ui();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RecyclerView recyclerView = findViewById(R.id.recyclerview);
                MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
                adapter.items.clear();
                adapter.notifyDataSetChanged();
                favoritesPageTextView.setVisibility(View.VISIBLE);
                server_error_text.setVisibility(View.GONE);
                get_recipes_from_server();
                swipeRefreshLayout.setRefreshing(false);
            }

            /*
            favoritesPageTextView.setVisibility(View.INVISIBLE);
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            MyAdapter adapter = new MyAdapter(getApplicationContext(), items,this, currentUser.getIsAdmin());
            recyclerView.setAdapter(adapter);
             */
        });

        handleNavigationChange();
    }

    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_favorites);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_favorites:
                    return true;
                case R.id.bottom_person:
                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user",currentUser);
                    startActivity(intent_Person);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
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

    private void get_recipes_from_server(){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getFavoriteRecipes("?user_id=" + currentUser.getUser_id());

                if(apiResponse == null){
                    display_server_down_error("Server is down, Please Try again");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {
                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);

                    for (Recipe recipe : recipes) {
                        if (recipe == null) System.out.println("RECIPE IS NULL");
                        else System.out.println("RECIPE IS NOT NULL");
                        addItemThreadSafe(recipe);
                    }

                    //update recipe list on main thread
                    handler.post(() -> {
                        add_recipes_to_ui();
                    });

                } else{
                    display_server_down_error("Something went wrong.");
                }
            }
        });

        thread.start();
    }

    private synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item(recipe));
    }

    private void add_recipes_to_ui(){
        favoritesPageTextView.setVisibility(View.INVISIBLE);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(getApplicationContext(), items,this, currentUser.getIsAdmin(), currentUser);
        recyclerView.setAdapter(adapter);
    }

    private void display_server_down_error(String errorText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                server_error_text.setText(errorText);
                server_error_text.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        System.out.println(items.toString());

        System.out.println("CURRENT USEr CLICK " + currentUser.getUser_id());
        if (currentUser.getIsAdmin()==0) {
            changeActivityToRecipeActivity(currentUser, items.get(position).getRecipe());

        } else {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.admin_dialog);
            final Button banUser = dialog.findViewById(R.id.banUser);
            final Button deleteRecipe = dialog.findViewById(R.id.deleteRecipe);

            dialog.show();
        }
    }

    private void changeActivityToRecipeActivity(User user, Recipe recipe){
        final Intent intent = new Intent(FavoriteActivity.this, RecipeActivity.class);
        intent.putExtra("current_user",user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }
}
