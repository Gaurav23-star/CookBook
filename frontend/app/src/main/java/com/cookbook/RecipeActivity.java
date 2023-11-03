package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Comment;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class RecipeActivity extends AppCompatActivity {
    private User currentUser;
    private Recipe currentRecipe;
    private static final String UPDATE_RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";
    private static Recipe updated_recipe;

    private ScrollView scrollView;
    private Button edit_button;
    private Button save_button;
    private ImageView recipePicture;
    private EditText titleView;
    private EditText nameView;
    private EditText ingredientsView;
    private EditText instructionsView;
    private EditText descriptionView;
    private RecyclerView commentsView;
    private EditText commentTextView;
    private Button commentButton;
    private final List<Comment> commentList = new ArrayList<>();
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
        commentsView = findViewById(R.id.commentsView);
        commentTextView = findViewById(R.id.commentText);
        commentButton = findViewById(R.id.addCommentButton);
        scrollView = findViewById(R.id.recipeScrollView);

        System.out.println("CURRENT USER ID IN RECIPE " + currentRecipe.getUser_id());
        System.out.println("CURRENT USER ID " + currentUser.getUser_id());
        if(currentRecipe.getUser_id() == currentUser.getUser_id()){
            System.out.println("SET EDIT VISIBLE");
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


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEntry(commentTextView)){
                    Comment comment = new Comment(currentUser.getUser_id(), currentRecipe.getRecipe_id(), currentUser.getUsername(),commentTextView.getText().toString());
                    postCommentToServer(comment);


                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(commentTextView.getWindowToken(), 0);
                    commentTextView.clearFocus();
                    commentTextView.setText("");
                    scrollView.scrollTo(commentsView.getBottom(), commentsView.getRight());
                }
            }
        });

        if(commentList.size() == 0){
            getCommentsFromServer(currentRecipe.getRecipe_id());
        }else{
            display_comments_on_ui();
        }
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

    private void getCommentsFromServer(int recipe_id){
        System.out.println("GETTING COMMENTS FROM SERVER");
        System.out.println("RECIPE ID = " + recipe_id);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getAllComments(recipe_id);
                if(apiResponse == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG GETTING COMMENTS", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    Comment[] comments = new Gson().fromJson(apiResponse.getResponse_body(), Comment[].class);
                    System.out.println(Arrays.toString(comments));
                    commentList.addAll(Arrays.asList(comments));

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            display_comments_on_ui();
                        }
                    });
                }
            }
        });

        thread.start();
    }

    private void postCommentToServer(Comment comment){
        System.out.println("POSTING COMMENT TO SERVER " + comment.getComment());
        System.out.println("USER ID = " + comment.getUser_id() + " Recipe ID = " + comment.getRecipe_id());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().postComment(comment);
                //error posting comment to server
                if(apiResponse == null || apiResponse.getResponse_code() == 400 || apiResponse.getResponse_code() == 500){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG POSTING COMMENT", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    commentList.add(comment);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            display_comments_on_ui();
                        }
                    });
                }
            }
        });

        thread.start();

    }


    private void display_comments_on_ui(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        CommentsViewAdapter commentsViewAdapter = new CommentsViewAdapter(getApplicationContext(),commentList);
        commentsView.setLayoutManager(layoutManager);
        commentsView.setAdapter(commentsViewAdapter);
    }
}