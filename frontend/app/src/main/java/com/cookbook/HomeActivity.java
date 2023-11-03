package com.cookbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity implements RecyclerViewInterface{

    private SwipeRefreshLayout swipeRefreshLayout;
    private static User currentUser;
    private final Gson gson = new Gson();
    private static final List<Item> items = Collections.synchronizedList(new ArrayList<Item>());

    private TextView server_error_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("HOME");
        //retrieve user passed in by login activity
        if(getIntent().getSerializableExtra("current_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("current_user");
        }
        System.out.println("VALUE OF CURRENT USER = " + currentUser);
        //user id 2 will be admin account / default page for now
        //System.out.println("Current User " + currentUser.toString());
        setContentView(R.layout.activity_home);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        server_error_text = findViewById(R.id.serverErrorTextView);

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

        //Let recipes list refreshable, reload data from server if user refreshes
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                server_error_text.setVisibility(View.GONE);
                get_recipes_from_server();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // ------ Navigation Choice ----
        handleNavigationChange();


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


    private void add_recipes_to_ui(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());
        recyclerView.setAdapter(adapter);
    }


    private void get_recipes_from_server(){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getAllRecipes();

                if(apiResponse == null){
                    display_server_down_error("Server is down, Please Try again");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);

                    for(Recipe recipe : recipes){
                        addItemThreadSafe(recipe);
                    }

                    //update recipe list on main thread
                    handler.post(() ->{
                        add_recipes_to_ui();
                    });

                }else{
                    display_server_down_error("Something went wrong.");
                }
            }
        });

        thread.start();
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
            final Button banButton = dialog.findViewById(R.id.banButton);

            dialog.show();
        }
    }
    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
    private void changeActivityToRecipeActivity(User user, Recipe recipe){
        final Intent intent = new Intent(HomeActivity.this, RecipeActivity.class);
        intent.putExtra("current_user",user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }

    //method to ensure thread safety
    public synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item (recipe));
    }


    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_home:
                    return true;
                case R.id.bottom_favorites:
                    Intent intent_Favorites = new Intent(getApplicationContext(), FavoriteActivity.class);
                    intent_Favorites.putExtra("current_user",currentUser);
                    startActivity(intent_Favorites);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_person:
                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user",currentUser);
                    startActivity(intent_Person);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_notifications:
                    Intent intent_Notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                    intent_Notifications.putExtra("current_user",currentUser);
                    startActivity(intent_Notifications);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_settings:
                    final Intent intent_Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent_Settings.putExtra("current_user",currentUser);
                    startActivity(intent_Settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("HOME ACTIVITY RESUMED");
        SharedPreferences sharedPreferences = getSharedPreferences("Updated_recipe", MODE_PRIVATE);
        if(sharedPreferences.contains("updated_recipe")){
            System.out.println("GOT UPDATED RECIPE");
            System.out.println(sharedPreferences.getString("updated_recipe", "null"));
            String update_recipe = sharedPreferences.getString("updated_recipe", "null");

            //update the recipe list with edited recipe
            if(!update_recipe.equals("null")){
                Recipe uRecipe = new Gson().fromJson(update_recipe, Recipe.class);
                for(Item recipe : items){
                    if(recipe.getRecipe().getRecipe_id() == uRecipe.getRecipe_id()){
                        recipe.update_item(uRecipe);
                        break;
                    }
                }
                add_recipes_to_ui();
            }
        }
    }

}