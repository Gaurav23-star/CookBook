package com.cookbook;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
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

        if (getIntent().getSerializableExtra("current_user") != null) {
            currentUser = (User) getIntent().getSerializableExtra("current_user");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchView = findViewById(R.id.recipeSearchView);
        recipesRecyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerViewManager = new LinearLayoutManager(this);
        recipesRecyclerView.setLayoutManager(recyclerViewManager);
        recyclerViewAdapter = new MyAdapter(getApplicationContext(), items, this, currentUser.getIsAdmin(), currentUser);
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
                        if (newText.trim().equals("")) {
                            items.clear();
                            recyclerViewAdapter.notifyDataSetChanged();
                        } else getRecipesFromSearchQuery(newText);
                    }
                }, 500);
                return false;
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(int position) {


        if (currentUser.getIsAdmin() == 0) {
            changeActivityToRecipeActivity(currentUser, items.get(position).getRecipe());
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.admin_dialog);
            final Button banUser = dialog.findViewById(R.id.banUser);
            final Button deleteRecipe = dialog.findViewById(R.id.deleteRecipe);

            dialog.show();

            deleteRecipe.setOnClickListener(view -> {

                Recipe dRecipe = (items.get(position).getRecipe());
                Recipe.deleteRecipe(dRecipe);

                //remove deleted recipe from UI
                for (Item recipe : items) {
                    if (recipe.getRecipe().getRecipe_id() == dRecipe.getRecipe_id()) {
                        items.remove(recipe);
                        break;
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged();
                //add_recipes_to_ui();
                dialog.dismiss();
            });

            banUser.setOnClickListener(view -> {

                int ban_id = items.get(position).getRecipe().getUser_id();


                User.banUser(ban_id);
//
//                for (Item recipe : items) {
//                    if (recipe.getRecipe().getUser_id() == ban_id) {
//                        items.remove(recipe);
//                    }
//
//                }

                recyclerViewAdapter.notifyDataSetChanged();
                //add_recipes_to_ui();
                dialog.dismiss();
            });
        }
    }

    private void changeActivityToRecipeActivity(User user, Recipe recipe) {

        final Intent intent = new Intent(RecipeSearchActivity.this, RecipeActivity.class);
        intent.putExtra("current_user", user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }

    private void getRecipesFromSearchQuery(String text) {
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {

                ApiResponse apiResponse = ApiCaller.get_caller_instance().getRecipeSearch(text);

                if (apiResponse == null) {


                    return;
                }

                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {

                    items.clear();
                    Recipe[] recipes = new Gson().fromJson(apiResponse.getResponse_body(), Recipe[].class);

                    for (Recipe recipe : recipes) {
                        addItemThreadSafe(recipe);
                    }

                    //update user list on main thread
                    handler.post(() -> {
                        recyclerViewAdapter.notifyDataSetChanged();
                    });

                } else {

                }
            }
        });

        thread.start();
    }

    public synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item(recipe));
    }
}
