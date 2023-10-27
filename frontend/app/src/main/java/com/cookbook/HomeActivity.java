package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity implements RecyclerViewInterface{

    private static User currentUser;
    private static final String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";
    private Gson gson = new Gson();
    List<Item> items = new ArrayList<Item>();
    static Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //retrieve user passed in by login activity
       currentUser = (User) getIntent().getSerializableExtra("current_user");

        System.out.println("Current User " + currentUser.toString());
        setContentView(R.layout.activity_home);
        /*final Thread thread = new Thread(() -> {
            try {
                final URL url = new URL(RECIPE_URL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                int userid = 1;
                final String jsonData = "{\"user_id\":\"" + userid + "\"}";
                System.out.println("Json Payload: " + jsonData);

                final OutputStream os = connection.getOutputStream();
                final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

                osw.write(jsonData);
                osw.flush();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    final InputStream responseBody = connection.getInputStream();

                    String jsonString = convertStreamToString(responseBody);
                    System.out.println("Response body: " + jsonString);

                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject recipeJson = jsonObject.getJSONObject("recipe");

                } else {

                    throw new Exception("HTTP Request Failed with response code: " + responseCode);
                }
            } catch (Exception e) {

                System.out.println("EXCEPTION OCcURRED " + e);

            }
        });

        thread.start();*/

        String jsonData = "{\"recipe\":{\"recipe_id\": 15,\"recipe_name\":\"Apple Pie\",\"servings\": 8,\"preparation_time_minutes\": 60,\"ingredients\":\"Apple, Pie Crust\",\"description\":\"This is my grandma's world famous recipe\",\"instructions\":\"1. Make the Apple Pie\",\"user_id\":smaye }}";
        //System.out.println(jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject recipeJson = jsonObject.getJSONObject("recipe");
            recipe = gson.fromJson(recipeJson.toString(), Recipe.class);
            System.out.println(recipe.getDescription()+" "+recipe.getRecipe_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items.add(new Item(recipe));
        String jsonData2 = "{\"recipe\":{\"recipe_id\": 16,\"recipe_name\":\"Garfield's favorite lasagna\",\"servings\": 1,\"preparation_time_minutes\": 45,\"ingredients\":\"Meat, Pasta\",\"description\":\"Garfield just loves this amazing lasagna recipe!\",\"instructions\":\"1. Make the lasagna\",\"user_id\":jarbuckle }}";
        //System.out.println(jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData2);
            JSONObject recipeJson = jsonObject.getJSONObject("recipe");
            recipe = gson.fromJson(recipeJson.toString(), Recipe.class);
            System.out.println(recipe.getDescription()+" "+recipe.getRecipe_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items.add(new Item(recipe));
        String jsonData3 = "{\"recipe\":{\"recipe_id\": 17,\"recipe_name\":\"One Pot Sausage and Vegs\",\"servings\": 1,\"preparation_time_minutes\": 30,\"ingredients\":\"Sausage, Peppers\",\"description\":\"Easy one pot recipe!\",\"instructions\":\"1. Make the one pot recipe\",\"user_id\":seanny258 }}";
        //System.out.println(jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData3);
            JSONObject recipeJson = jsonObject.getJSONObject("recipe");
            recipe = gson.fromJson(recipeJson.toString(), Recipe.class);
            System.out.println(recipe.getDescription()+" "+recipe.getRecipe_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items.add(new Item(recipe));
        String jsonData4 = "{\"recipe\":{\"recipe_id\": 18,\"recipe_name\":\"Fish and chips\",\"servings\": 1,\"preparation_time_minutes\": 45,\"ingredients\":\"Fish, Potatoes\",\"description\":\"Want to be british? Why? Anyway here the recipe\",\"instructions\":\"1. Make the fish and chips\",\"user_id\":thebrit }}";
        //System.out.println(jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData4);
            JSONObject recipeJson = jsonObject.getJSONObject("recipe");
            recipe = gson.fromJson(recipeJson.toString(), Recipe.class);
            System.out.println(recipe.getDescription()+" "+recipe.getRecipe_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items.add(new Item(recipe));
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());
        //find way to hide admin
        recyclerView.setAdapter(adapter);

        // ------ Navigation Choice ----
        handleNavigationChange();
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
//
//        bottomNavigationView.setOnItemSelectedListener(item ->{
//            switch (item.getItemId()){
//                case R.id.bottom_home:
//                    return true;
//                case R.id.bottom_person:
//                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    finish();
//                    return true;
//                case R.id.bottom_settings:
//                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    finish();
//                    return true;
//            }
//            return false;
//        });


    }

    @Override
    public void onItemClick(int position) {
        System.out.println(items.toString() + " "+recipe.toString());
        if (currentUser.getIsAdmin()==0) {
            changeActivityToUserHome(currentUser, items.get(position).getRecipe());

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
    private void changeActivityToUserHome(User user, Recipe recipe){
        final Intent intent = new Intent(HomeActivity.this, RecipeActivity.class);
        intent.putExtra("current_user",user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
        finish();
    }

    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_home:
                    return true;
                case R.id.bottom_person:

                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user",currentUser);
                    startActivity(intent_Person);
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
}