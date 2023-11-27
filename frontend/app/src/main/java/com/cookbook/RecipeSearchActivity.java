package com.cookbook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeSearchActivity extends AppCompatActivity implements RecyclerViewInterface {

    static User currentUser;
    private RecyclerView recipesRecyclerView;
    private SearchView searchView;
    private MyAdapter recyclerViewAdapter;
    private LinearLayoutManager recyclerViewManager;
    private static final List<Item> items = Collections.synchronizedList(new ArrayList<Item>());
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        if(getIntent().getSerializableExtra("current_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("current_user");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchView = findViewById(R.id.recipeSearchView);
        recipesRecyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerViewManager = new LinearLayoutManager(this);
        recipesRecyclerView.setLayoutManager(recyclerViewManager);
        recyclerViewAdapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());
        recipesRecyclerView.setAdapter(recyclerViewAdapter);


        final Handler handler = new Handler();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //get_recipes_from_server();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // do stuff
                        if(newText.trim().equals("")){
                            items.clear();
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                        else getRecipesFromSearchQuery(newText);
                    }
                }, 500);
                return false;
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void changeActivityToRecipeActivity(User user, Recipe recipe){

        final Intent intent = new Intent(RecipeSearchActivity.this, RecipeActivity.class);
        intent.putExtra("current_user",user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }

    private void getRecipesFromSearchQuery(String text){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {

                ApiResponse apiResponse = ApiCaller.get_caller_instance().getRecipeSearch(text);

                if(apiResponse == null){
                    System.out.println("Server is down, Please Try again");
                    System.out.println("-0230--03940-2940-392-4923---------");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){

                    items.clear();
                    Recipe[] recipes = new Gson().fromJson(apiResponse.getResponse_body(), Recipe[].class);

                    for(Recipe recipe : recipes){
                        addItemThreadSafe(recipe);
                    }

                    //update user list on main thread
                    handler.post(() ->{
                        recyclerViewAdapter.notifyDataSetChanged();
                    });

                }else{
                    System.out.println("Server is down, Please Try again");
                }
            }
        });

        thread.start();
    }
    private void get_recipes_from_server(){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getAllRecipes();

                if(apiResponse == null){
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    Gson gson = new Gson();
                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);
                    int startPosition = items.size();
                    for(Recipe recipe : recipes){
                        addItemThreadSafe(recipe);
                    }

                    //update recipe list on main thread
                    handler.post(() ->{
                        recyclerViewAdapter.notifyDataSetChanged();
                       // progressBar.setVisibility(View.GONE);
                    });

                }else{
                    //display_server_down_error("Something went wrong.");
                }
            }
        });

        thread.start();
    }

    public synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item (recipe));
    }
}
