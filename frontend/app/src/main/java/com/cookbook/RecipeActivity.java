package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class RecipeActivity extends AppCompatActivity {
    private static User currentUser;
    private static Recipe currentRecipe;
    private static final String UPDATE_RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";
    private static Recipe updated_recipe;

    private Button edit_button;
    private Button save_button;
    private ImageView recipePicture;
    private EditText titleView;
    private EditText nameView;
    private EditText ingredientsView;
    private EditText instructionsView;
    private EditText descriptionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRecipe = (Recipe) getIntent().getSerializableExtra("current_recipe");
        currentUser = (User) getIntent().getSerializableExtra("current_user");
        setContentView(R.layout.activity_recipe);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");

        edit_button = findViewById(R.id.editButton);
        save_button = findViewById(R.id.saveButton);


        recipePicture = findViewById(R.id.recipeView);
        titleView = findViewById(R.id.titleView);
        nameView = findViewById(R.id.nameView);
        ingredientsView = findViewById(R.id.ingredientsView);
        instructionsView = findViewById(R.id.instructionsView);
        descriptionView = findViewById(R.id.descriptionView);
        recipePicture.setImageResource(R.drawable.foodplaceholder);

        titleView.setText(currentRecipe.getRecipe_name());
        nameView.setText(Integer.toString(currentRecipe.getUser_id()));
        ingredientsView.setText(currentRecipe.getIngredients());
        instructionsView.setText(currentRecipe.getInstructions());
        descriptionView.setText(currentRecipe.getDescription());

        if(currentRecipe.getUser_id() == currentUser.getUser_id()){
            edit_button.setVisibility(View.VISIBLE);
        }
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_button.setVisibility(View.GONE);
                save_button.setVisibility(View.VISIBLE);

                makeFieldsEditable(titleView);
                makeFieldsEditable(ingredientsView);
                makeFieldsEditable(instructionsView);
                makeFieldsEditable(descriptionView);

            }
        });


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidEntry(titleView)
                && isValidEntry(ingredientsView)
                && isValidEntry(instructionsView)
                && isValidEntry(descriptionView)){

                    Recipe updatedRecipe = new Recipe(
                            currentRecipe.getRecipe_id(),
                            titleView.getText().toString(),
                            currentRecipe.getServings(),
                            currentRecipe.getPreparation_time_minutes(),
                            ingredientsView.getText().toString(),
                            descriptionView.getText().toString(),
                            instructionsView.getText().toString(),
                            currentRecipe.getUser_id()
                    );
                    updated_recipe = updatedRecipe;
                    update_recipe_on_server(updatedRecipe);

                    makeFieldsNonEditable(titleView);
                    makeFieldsNonEditable(ingredientsView);
                    makeFieldsNonEditable(instructionsView);
                    makeFieldsNonEditable(descriptionView);

                    edit_button.setVisibility(View.VISIBLE);
                    save_button.setVisibility(View.GONE);
                }

            }
        });

    }

    private void update_recipe_on_server(Recipe updatedRecipe) {
        final Thread thread = new Thread(() -> {

            try {
                final URL url = new URL(UPDATE_RECIPE_URL + "?recipe_id=" + currentRecipe.getRecipe_id());
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                final String jsonData = new Gson().toJson(updatedRecipe);

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
                    recipe_updated_success();

                } else {
                    System.out.println("Response code is " + responseCode);
                    unable_to_update_recipe();
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION OCCURRED " + e);
                unable_to_update_recipe();
            }
        });
        thread.start();
    }

    private void recipe_updated_success(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Recipe Edited Successfully", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void unable_to_update_recipe(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Unable To Edit Recipe", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeFieldsEditable(EditText view){
        view.setBackground(getResources().getDrawable(R.drawable.edittext_border));
        view.setClickable(true);
        view.setInputType(InputType.TYPE_CLASS_TEXT);
        view.setCursorVisible(true);
        view.requestFocus();
        view.setEnabled(true);
    }

    private void makeFieldsNonEditable(EditText view){
        view.setBackground(null);
        view.setClickable(false);
        view.setInputType(InputType.TYPE_NULL);
        view.setCursorVisible(false);
        view.setEnabled(false);
    }

    private boolean isValidEntry(EditText view){
        if(view.getText().toString().trim().equals("")){
            view.setError("cannot be empty");
            return false;
        }
        return true;
    }

    private void changeActivityToUserHome(User user){
        final Intent intent = new Intent(RecipeActivity.this, HomeActivity.class);
        intent.putExtra("current_user", user);
        startActivity(intent);
        finish();
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


    private void update_home_activity_recipe_list(Recipe recipe){
        SharedPreferences sharedPreferences = getSharedPreferences("Updated_recipe", MODE_PRIVATE);
        sharedPreferences.edit().putString("updated_recipe", new Gson().toJson(recipe)).apply();
    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("CHILD IS BEING PAUSED");
        if(updated_recipe != null) {
            System.out.println("SENDING RECIPE TO HOME");
            update_home_activity_recipe_list(updated_recipe);
        }
    }
}