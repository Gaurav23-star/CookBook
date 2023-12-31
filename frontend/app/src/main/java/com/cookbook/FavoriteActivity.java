package com.cookbook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
        if (items.size() == 0) {

            get_recipes_from_server();
        }
        //if we already have recipes loaded, then
        // just update the UI, no need to reload from server again
        else {
            add_recipes_to_ui();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //check if the user is banned
                checkAndLogOutUserIfBanned();
                RecyclerView recyclerView = findViewById(R.id.recyclerview);
                MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
                adapter.items.clear();
                adapter.notifyDataSetChanged();
                favoritesPageTextView.setVisibility(View.VISIBLE);
                server_error_text.setVisibility(View.GONE);
                get_recipes_from_server();
                swipeRefreshLayout.setRefreshing(false);
            }

        });

        handleNavigationChange();
    }

    /**
     * Handles the change in the bottom navigation view.
     */
    public void handleNavigationChange() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_favorites);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_favorites:
                    //check if the user is banned
                    checkAndLogOutUserIfBanned();
                    return true;
                case R.id.bottom_person:
                    //check if the user is banned
                    checkAndLogOutUserIfBanned();
                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user", currentUser);
                    startActivity(intent_Person);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_settings:
                    //check if the user is banned
                    checkAndLogOutUserIfBanned();
                    Intent intent_Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent_Settings.putExtra("current_user", currentUser);
                    startActivity(intent_Settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();


                    return true;
                case R.id.bottom_notifications:
                    //check if the user is banned
                    checkAndLogOutUserIfBanned();
                    Intent intent_Notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                    intent_Notifications.putExtra("current_user", currentUser);
                    startActivity(intent_Notifications);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_home:
                    //check if the user is banned
                    checkAndLogOutUserIfBanned();
                    Intent intent_Home = new Intent(getApplicationContext(), HomeActivity.class);
                    intent_Home.putExtra("current_user", currentUser);
                    startActivity(intent_Home);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                    return true;
            }
            return false;
        });
    }

    /**
     * Retrieves recipes from the server.
     */
    private void get_recipes_from_server() {
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getFavoriteRecipes("?user_id=" + currentUser.getUser_id());

                if (apiResponse == null) {
                    display_server_down_error("Server is down, Please Try again");
                    return;
                }

                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {
                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);

                    for (Recipe recipe : recipes) {
                        addItemThreadSafe(recipe);
                    }

                    //update recipe list on main thread
                    handler.post(() -> {
                        add_recipes_to_ui();
                    });

                } else {
                    display_server_down_error("Something went wrong.");
                }
            }
        });

        thread.start();
    }

    /**
     * Adds an item to the list in a thread-safe manner.
     *
     * @param recipe The recipe to add.
     */
    private synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item(recipe));
    }

    /**
     * Updates the UI by adding recipes to the RecyclerView.
     */
    private void add_recipes_to_ui() {
        favoritesPageTextView.setVisibility(View.INVISIBLE);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(getApplicationContext(), items, this, currentUser.getIsAdmin(), currentUser);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Displays a server down error message.
     *
     * @param errorText The error message to display.
     */
    private void display_server_down_error(String errorText) {
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
        }
    }

    /**
     * Changes the activity to RecipeActivity.
     *
     * @param user   The current user.
     * @param recipe The selected recipe.
     */
    private void changeActivityToRecipeActivity(User user, Recipe recipe) {
        final Intent intent = new Intent(FavoriteActivity.this, RecipeActivity.class);
        intent.putExtra("current_user", user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }

    /**
     * Checks if the user is banned and logs them out if banned.
     */
    public void checkAndLogOutUserIfBanned() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isBanned = ApiCaller.get_caller_instance().isUserBanned(String.valueOf(currentUser.getUser_id()));

                if (isBanned) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertUserOfBanAndLogOut();
                        }
                    });
                }
            }
        });

        thread.start();
    }

    /**
     * Alerts the user of the ban and logs them out.
     */
    public void alertUserOfBanAndLogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        String alertMessage = "Admin has banned you from the app. You will be signed out from the app.";
        alertDialog.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences = getSharedPreferences("Saved User", MODE_PRIVATE);
                        sharedPreferences.edit().remove("current_user").apply();
                        Intent intent = new Intent(FavoriteActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.setTitle("YOU HAVE BEEN BANNED");
        alert.show();
    }
}
