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

    private static User currentUser = new User();
    private static final String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";
    private Gson gson = new Gson();
    List<Item> items = new ArrayList<Item>();
    static Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser.setUser_id(1);
        currentUser.setEmail_id("test@gmial.com");
        currentUser.setFirst_name("Joe");
        currentUser.setLast_name("mama");
        currentUser.setPassword("password");
        currentUser.setIsBanned(0);
        currentUser.setIsAdmin(0);
        //retrieve user passed in by login activity
       // User currentUser = (User) getIntent().getSerializableExtra("current_user");

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

        String jsonData = "{\"recipe\":{\"recipe_id\": 15,\"recipe_name\":\"testname\",\"servings\": 1,\"preparation_time_minutes\": 30,\"ingredients\":\"testingredients\",\"description\":\"testdescription\",\"instructions\":\"testinstructions\",\"user_id\":9 }}";
        System.out.println(jsonData);
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject recipeJson = jsonObject.getJSONObject("recipe");
            recipe = gson.fromJson(recipeJson.toString(), Recipe.class);
            System.out.println(recipe.getDescription()+" "+recipe.getRecipe_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        items.add(new Item(recipe));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());
        //find way to hide admin
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position) {
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
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
        finish();
    }
}