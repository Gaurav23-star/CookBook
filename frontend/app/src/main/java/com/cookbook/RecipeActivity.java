package com.cookbook;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cookbook.model.ApiResponse;
import com.cookbook.model.Comment;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class RecipeActivity extends AppCompatActivity implements RecyclerViewInterface {
    private User currentUser;
    private Recipe currentRecipe;
    private static final String UPDATE_RECIPE_URL = ApiCaller.host + "/user-defined-recipes";
    private static Recipe updated_recipe;

    private ScrollView scrollView;
    private Button edit_button;
    private Button save_button;
    private ImageView recipePicture;
    private ImageView recipePictureEdit;
    private EditText titleView;
    private EditText nameView;
    private EditText ingredientsView;
    private EditText instructionsView;
    private EditText descriptionView;
    private EditText prepTimeView;
    private EditText servingsView;
    private RecyclerView commentsView;
    private EditText commentTextView;
    private Button commentButton;
    private Button delete_comment_button;
    private ConstraintLayout recipeOwnerView;
    private User recipeOwner;
    private final List<Comment> commentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRecipe = (Recipe) getIntent().getSerializableExtra("current_recipe");
        currentUser = (User) getIntent().getSerializableExtra("current_user");
        setContentView(R.layout.activity_recipe);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recipe");

        edit_button = findViewById(R.id.editButton);
        save_button = findViewById(R.id.saveButton);

        recipePicture = findViewById(R.id.recipeView);
        recipePictureEdit = findViewById(R.id.recipe_image_edit);
        titleView = findViewById(R.id.titleView);
        nameView = findViewById(R.id.nameView);
        ingredientsView = findViewById(R.id.ingredientsView);
        instructionsView = findViewById(R.id.instructionsView);
        descriptionView = findViewById(R.id.descriptionView);
        prepTimeView = findViewById(R.id.prepTimeEditText);
        servingsView = findViewById(R.id.servingsEditText);
        recipeOwnerView = findViewById(R.id.recipeOwnerView);
        //recipePicture.setImageResource(R.drawable.foodplaceholder);
        loadRecipeImage(recipePicture);

        titleView.setText(currentRecipe.getRecipe_name());
        nameView.setText(Integer.toString(currentRecipe.getUser_id()));
        ingredientsView.setText(currentRecipe.getIngredients());
        instructionsView.setText(currentRecipe.getInstructions());
        descriptionView.setText(currentRecipe.getDescription());
        commentsView = findViewById(R.id.commentsView);
        commentTextView = findViewById(R.id.commentText);
        commentButton = findViewById(R.id.addCommentButton);
        scrollView = findViewById(R.id.recipeScrollView);

        prepTimeView.setText(Integer.toString(currentRecipe.getPreparation_time_minutes()));
        servingsView.setText(Integer.toString(currentRecipe.getServings()));


        if (currentRecipe.getUser_id() == currentUser.getUser_id()) {

            edit_button.setVisibility(View.VISIBLE);
        }
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_button.setVisibility(View.GONE);
                save_button.setVisibility(View.VISIBLE);
                recipePictureEdit.setVisibility(View.VISIBLE);
                makeFieldsEditable(prepTimeView);
                makeFieldsEditable(servingsView);
                makeFieldsEditable(titleView);
                makeFieldsEditable(ingredientsView);
                makeFieldsEditable(instructionsView);
                makeFieldsEditable(descriptionView);

            }
        });

        if (currentRecipe.getUser_id() != currentUser.getUser_id()) {
            loadRecipeOwnerProfile(Integer.toString(currentRecipe.getUser_id()));
        } else {
            //String displayName = currentUser.getFirst_name() + " " + currentUser.getLast_name();
            String displayName = currentUser.getUsername();
            nameView.setText(displayName);
        }

        recipeOwnerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recipeOwner != null) {
                    loadUserProfile(recipeOwner);
                }
            }
        });


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipePictureEdit.setVisibility(View.GONE);

                if (isValidEntry(titleView)
                        && isValidEntry(ingredientsView)
                        && isValidEntry(instructionsView)
                        && isValidEntry(descriptionView)) {

                    Recipe updatedRecipe = new Recipe(
                            currentRecipe.getRecipe_id(),
                            titleView.getText().toString(),
                            Integer.parseInt(servingsView.getText().toString()),
                            Integer.parseInt(prepTimeView.getText().toString()),
                            ingredientsView.getText().toString(),
                            descriptionView.getText().toString(),
                            instructionsView.getText().toString(),
                            currentRecipe.getUser_id(),
                            currentRecipe.getNum_comments(),
                            currentRecipe.getNum_likes()
                    );
                    updated_recipe = updatedRecipe;
                    update_recipe_on_server(updatedRecipe);

                    makeFieldsNonEditable(prepTimeView);
                    makeFieldsNonEditable(servingsView);
                    makeFieldsNonEditable(titleView);
                    makeFieldsNonEditable(ingredientsView);
                    makeFieldsNonEditable(instructionsView);
                    makeFieldsNonEditable(descriptionView);

                    edit_button.setVisibility(View.VISIBLE);
                    save_button.setVisibility(View.GONE);
                }

            }
        });

        ActivityResultLauncher<PickVisualMediaRequest> pickPhoto = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                if (result != null) {
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    String extension = mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(result));
                    String imageUrl = currentRecipe.getRecipe_id() + "." + extension;

                    File file = getImageFile(result, imageUrl);
                    ApiResponse apiResponse = ApiCaller.get_caller_instance().uploadRecipeImage(file);
                    recipePicture.setImageURI(result);
                    if (apiResponse != null && apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {

                        recipePicture.setImageURI(result);
                    }
                }
            }
        });
        recipePictureEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickPhoto.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEntry(commentTextView)) {
                    Comment comment = new Comment(currentUser.getUser_id(), currentRecipe.getRecipe_id(), currentUser.getUsername(), commentTextView.getText().toString(), 0);
                    postCommentToServer(comment);

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(commentTextView.getWindowToken(), 0);
                    commentTextView.clearFocus();
                    commentTextView.setText("");
                }
            }
        });

        if (commentList.size() == 0) {
            getCommentsFromServer(currentRecipe.getRecipe_id());
        } else {
            display_comments_on_ui();
        }
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

    private void loadRecipeOwnerProfile(String userId) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ApiResponse response = ApiCaller.get_caller_instance().getUserFromUserId(userId);
                    if (response != null && response.getResponse_code() == HttpURLConnection.HTTP_OK) {
                        User user = new Gson().fromJson(response.getResponse_body(), User.class);
                        recipeOwner = user;
                        new Handler(Looper.getMainLooper()).post(() -> {
                            //String displayName = recipeOwner.getFirst_name() + " " + recipeOwner.getLast_name();
                            String displayName = recipeOwner.getUsername();
                            nameView.setText(displayName);
                        });
                    }
                } catch (Exception e) {


                }
            }
        });

        thread.start();
    }

    private void loadCommentOwnerProfile(String userId) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ApiResponse response = ApiCaller.get_caller_instance().getUserFromUserId(userId);
                    if (response != null && response.getResponse_code() == HttpURLConnection.HTTP_OK) {
                        User user = new Gson().fromJson(response.getResponse_body(), User.class);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            loadUserProfile(user);
                        });
                    }
                } catch (Exception e) {


                }
            }
        });

        thread.start();
    }

    private void loadUserProfile(User user) {
        Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
        intent_Person.putExtra("visiting_user", user);
        intent_Person.putExtra("current_user", currentUser);
        startActivity(intent_Person);
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


                final OutputStream os = connection.getOutputStream();
                final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

                osw.write(jsonData);
                osw.flush();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    final InputStream responseBody = connection.getInputStream();

                    String jsonString = convertStreamToString(responseBody);

                    recipe_updated_success();

                } else {

                    unable_to_update_recipe();
                }
            } catch (Exception e) {

                unable_to_update_recipe();
            }
        });
        thread.start();
    }

    private void recipe_updated_success() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Recipe Edited Successfully", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void unable_to_update_recipe() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Unable To Edit Recipe", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeFieldsEditable(EditText view) {
        //view.setBackground(getResources().getDrawable(R.drawable.edittext_border));
        int originalLines = view.getLineCount();

        view.setClickable(true);
        view.setCursorVisible(true);
        view.requestFocus();
        view.setEnabled(true);
        view.setLines(originalLines);
        view.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        view.setVerticalScrollBarEnabled(true);

        view.requestLayout();
    }

    private void makeFieldsNonEditable(EditText view) {
        //view.setBackground(null);
        int originalLines = view.getLineCount();
        view.setClickable(false);
        view.setCursorVisible(false);
        view.setEnabled(false);
        view.setLines(originalLines);
        view.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        view.setVerticalScrollBarEnabled(true);
    }

    private boolean isValidEntry(EditText view) {
        if (view.getText().toString().trim().equals("")) {
            view.setError("cannot be empty");
            return false;
        }
        return true;
    }


    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


    private void update_home_activity_recipe_list(Recipe recipe) {
        HomeActivity.updateItem(recipe);
        //SharedPreferences sharedPreferences = getSharedPreferences("Updated_recipe", MODE_PRIVATE);
        //sharedPreferences.edit().putString("updated_recipe", new Gson().toJson(recipe)).apply();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (updated_recipe != null) {

            update_home_activity_recipe_list(updated_recipe);
        }
    }

    private void getCommentsFromServer(int recipe_id) {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getAllComments(recipe_id);
                if (apiResponse == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG GETTING COMMENTS", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {
                    Comment[] comments = new Gson().fromJson(apiResponse.getResponse_body(), Comment[].class);

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

    private void postCommentToServer(Comment comment) {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().postComment(comment);


                ApiResponse apiResponseTwo = ApiCaller.get_caller_instance().postUserNotification("comment", String.valueOf(currentRecipe.getUser_id()), String.valueOf(currentUser.getUser_id()), String.valueOf(currentRecipe.getRecipe_id()));

                //error posting comment to server
                if (apiResponse == null || apiResponse.getResponse_code() == 400 || apiResponse.getResponse_code() == 500 || ((apiResponseTwo == null || apiResponseTwo.getResponse_code() == 400 || apiResponseTwo.getResponse_code() == 500))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "SOMETHING WENT WRONG POSTING COMMENT", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {
                    try {
                        int commentId = new JSONObject(apiResponse.getResponse_body()).getInt("insertId");
                        comment.setComment_id(commentId);
                        commentList.add(comment);
                        currentRecipe.setNum_comments(currentRecipe.getNum_comments() + 1);
                        update_home_activity_recipe_list(currentRecipe);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                display_comments_on_ui();
                                scrollView.scrollToDescendant(commentsView);
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        thread.start();

    }


    private void display_comments_on_ui() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        CommentsViewAdapter commentsViewAdapter = new CommentsViewAdapter(getApplicationContext(), commentList, this);
        commentsView.setLayoutManager(layoutManager);
        commentsView.setAdapter(commentsViewAdapter);

    }

    private void loadRecipeImage(ImageView imageView) {
        String url = ApiCaller.GET_RECIPE_IMAGE_URL + currentRecipe.getRecipe_id();
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).dontAnimate().into(imageView);
    }

    @Override
    public void onItemClick(int position) {
        if (currentUser.getIsAdmin() == 0) {
            loadCommentOwnerProfile(Integer.toString(commentList.get(position).getUser_id()));
        } else {
            final Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.admin_comment_dialog);
            final Button deleteComment = dialog.findViewById(R.id.deleteComment);
            final Button viewProfile = dialog.findViewById(R.id.viewProfile);

            dialog.show();

            deleteComment.setOnClickListener(view -> {

                Comment.deleteComment(commentList.get(position).getComment_id());

                commentList.clear();
                getCommentsFromServer(currentRecipe.getRecipe_id());
                currentRecipe.setNum_comments(currentRecipe.getNum_comments() - 1);
                update_home_activity_recipe_list(currentRecipe);


                dialog.dismiss();
            });

            viewProfile.setOnClickListener(view -> {

                loadCommentOwnerProfile(Integer.toString(commentList.get(position).getUser_id()));

                dialog.dismiss();
            });
        }
    }

    private File getImageFile(Uri result, String imageUrl) {
        File dir = getApplicationContext().getFilesDir();
        File file = new File(dir, imageUrl);

        try {

            InputStream inputStream = getContentResolver().openInputStream(result);
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            while (len != -1) {
                outputStream.write(buffer, 0, len);
                len = inputStream.read(buffer);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }


}