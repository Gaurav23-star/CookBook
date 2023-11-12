package com.cookbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
    ImageButton user_search_button;
    private FloatingActionButton addMenuButton;
    private FloatingActionButton addNewFriendButton;
    private FloatingActionButton addNewRecipeButton;
    boolean isMenuShowing = false;
    private BottomSheetDialog bottomSheetDialog;
    private ActivityResultLauncher<PickVisualMediaRequest> pickRecipeImageLauncher;
    private ImageView newRecipeImageView;
    private Uri newRecipeImageUri;

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
        addMenuButton = findViewById(R.id.addMenuButton);
        addNewRecipeButton = findViewById(R.id.addNewRecipeButton);
        addNewFriendButton = findViewById(R.id.addNewFriendButton);
        bottomSheetDialog = new BottomSheetDialog(this);

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
        pickRecipeImageLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), result -> {
            System.out.println("NEW IMAGE IS " + result);
            if(result != null){
                newRecipeImageUri = result;
                newRecipeImageView.setImageURI(result);
            }
        });

        addMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuShowing){
                    addNewFriendButton.hide();
                    addNewRecipeButton.hide();
                    addMenuButton.setImageResource(R.drawable.baseline_add_24);
                    isMenuShowing = false;
                }else{
                    addNewRecipeButton.show();
                    addNewFriendButton.show();
                    addMenuButton.setImageResource(R.drawable.baseline_close_24);
                    isMenuShowing = true;
                }
            }
        });

        addNewRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateNewRecipeDialog();
                bottomSheetDialog.show();
            }
        });

        // ------ Navigation Choice ----
        handleNavigationChange();

        //LEGACY CODE
        /*
        user_search_button = (ImageButton) findViewById(R.id.search_user_button);

        user_search_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final Intent intent_UserSearch = new Intent(getApplicationContext(), UserSearchActivity.class);
                intent_UserSearch.putExtra("current_user",currentUser);
                startActivity(intent_UserSearch);
            }
        });

         */

        addNewFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent_UserSearch = new Intent(getApplicationContext(), UserSearchActivity.class);
                intent_UserSearch.putExtra("current_user",currentUser);
                startActivity(intent_UserSearch);
            }
        });



    }

    private void displayCreateNewRecipeDialog() {
        View view = getLayoutInflater().inflate(R.layout.create_recipe_activity, null, false);

        ImageView recipeImage = view.findViewById(R.id.recipeImageView);
        newRecipeImageView = recipeImage;
        ImageView recipeImageEditor = view.findViewById(R.id.add_recipe_image);
        EditText recipeTitle = view.findViewById(R.id.add_recipe_title);
        EditText recipeDescription = view.findViewById(R.id.add_recipe_description);
        EditText recipeIngredients = view.findViewById(R.id.add_recipe_ingredients);
        EditText recipeInstructions = view.findViewById(R.id.add_recipe_instructions);
        Button addRecipeButton = view.findViewById(R.id.add_recipe_button);
        EditText recipeServings = view.findViewById(R.id.add_servings);
        EditText recipePrepareTime = view.findViewById(R.id.add_preparation_time);
        recipeImageEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("WANT TO EDIT RECIPE IMAGE");
                pickRecipeImageLauncher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
                System.out.println("GOT IMAGE URI");

            }
        });

        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidEntry(recipeTitle) && isValidEntry(recipeIngredients)
                && isValidEntry(recipeInstructions)){
                    System.out.println("ALL ENTRIES ARE VALID");
                    String recipeName = recipeTitle.getText().toString();
                    String servings = recipeServings.getText().toString();
                    String prepareTime = recipePrepareTime.getText().toString();
                    String desc = recipeDescription.getText().toString();
                    String ingredients = recipeIngredients.getText().toString();
                    String instructions = recipeInstructions.getText().toString();

                    post_recipe_to_server(recipeName, desc, servings, prepareTime, ingredients, instructions);
                    bottomSheetDialog.dismiss();
                }
            }
        });

       bottomSheetDialog.setContentView(view);
    }

    private void post_recipe_to_server(String recipeName, String desc, String servings, String prepareTime, String ingredients, String instructions){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse response = ApiCaller.get_caller_instance().postNewRecipe(
                        recipeName,
                        desc,
                        servings,
                        prepareTime,
                        ingredients,
                        instructions,
                        currentUser.getUser_id()
                );


                if(response != null && response.getResponse_code() == HttpURLConnection.HTTP_OK){
                    try {
                        int recipeId = new JSONObject(response.getResponse_body()).getInt("insertId");

                        //add newly created recipe to home feed
                        Recipe recipe = new Recipe(
                                recipeId,
                                recipeName,
                                Integer.parseInt(servings),
                                Integer.parseInt(prepareTime),
                                ingredients,
                                desc,
                                instructions,
                                currentUser.getUser_id()
                        );
                        items.add(0, new Item(recipe));

                        if(newRecipeImageUri != null){
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String extension = mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(newRecipeImageUri));
                            String imageUrl = recipeId + "." + extension;

                            File file = getImageFile(newRecipeImageUri, imageUrl);
                            ApiCaller.get_caller_instance().uploadRecipeImage(file);
                        }

                        runOnUiThread(() -> add_recipes_to_ui());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }

            }
        });

        thread.start();
    }

    private boolean isValidEntry(EditText view){
        if(view.getText().toString().trim().equals("")){
            view.setError("cannot be empty");
            return false;
        }
        return true;
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
                sharedPreferences.edit().putString("update_recipe", "null").apply();
            }
        }
    }


    private File getImageFile(Uri result, String imageUrl){
        File dir = getApplicationContext().getFilesDir();
        File file = new File(dir, imageUrl);

        try {
            System.out.println("COPYING FILE DATA");
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