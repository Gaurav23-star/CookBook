package com.cookbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
    private ProgressBar progressBar;
    boolean isMenuShowing = false;
    private BottomSheetDialog bottomSheetDialog;
    private ActivityResultLauncher<PickVisualMediaRequest> pickRecipeImageLauncher;
    private ImageView newRecipeImageView;
    private Uri newRecipeImageUri;
    private MyAdapter recyclerViewAdapter;
    private LinearLayoutManager recyclerViewManager;
    private RecyclerView recipesRecyclerView;
    private boolean isScrolling;
    private int currentItem, totalItems, scrollOutItems;
    private static int recipePageNumber = 1;
    private static int currentItemBeingViewed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("HOME");
        //retrieve user passed in by login activity
        if(getIntent().getSerializableExtra("current_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("current_user");
        }
        System.out.println("VALUE OF CURRENT USER = " + currentUser);
        setContentView(R.layout.activity_home);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        server_error_text = findViewById(R.id.serverErrorTextView);
        addMenuButton = findViewById(R.id.addMenuButton);
        addNewRecipeButton = findViewById(R.id.addNewRecipeButton);
        addNewFriendButton = findViewById(R.id.addNewFriendButton);
        bottomSheetDialog = new BottomSheetDialog(this);
        recipesRecyclerView = findViewById(R.id.recyclerview);
        recyclerViewManager = new LinearLayoutManager(this);

        recipesRecyclerView.setLayoutManager(recyclerViewManager);
        recyclerViewAdapter = new MyAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin(), currentUser);
        recipesRecyclerView.setAdapter(recyclerViewAdapter);

        //check if the user is banned
        checkAndLogOutUserIfBanned();
        //if recipes not loaded from server, then load
        if(items.size() == 0){
            System.out.println("LIST IS EMPTY");
            get_recipes_from_server();
        }

        //Let recipes list refreshable, reload data from server if user refreshes
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //check if the user is banned
                checkAndLogOutUserIfBanned();
                server_error_text.setVisibility(View.GONE);
                int totalCount = items.size();
                for(int i = items.size()- 1; i >= 0; i--) items.remove(i);
                recyclerViewAdapter.notifyItemRangeRemoved(0, totalCount);
                System.out.println("ITEM SIZE BEFORE REFRESH " + items.size());
                recipePageNumber = 1;
                currentItemBeingViewed = 0;
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


        addNewFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent_UserSearch = new Intent(getApplicationContext(), UserSearchActivity.class);
                intent_UserSearch.putExtra("current_user",currentUser);
                startActivity(intent_UserSearch);
            }
        });


        recipesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                    //System.out.println("SCROLL CHANGED" + newState);
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem = recyclerViewManager.getChildCount();
                totalItems = recyclerViewManager.getItemCount();
                scrollOutItems = recyclerViewManager.findFirstCompletelyVisibleItemPosition();
                //System.out.println("CURRENTLY " + currentItem + " DISPLAYED");
                //System.out.println("VIEWED " + scrollOutItems + " OUT OF " + totalItems);
                currentItemBeingViewed = recyclerViewManager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItem + scrollOutItems > totalItems)){
                    //System.out.println("ORDERING MORE RECIPES");
                    isScrolling = false;
                    get_recipes_from_server();
                }
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                System.out.println("SEARCH CLICKED");
                changeActivityToRecipeSearch();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void changeActivityToRecipeSearch(){
        final Intent intent = new Intent(HomeActivity.this, RecipeSearchActivity.class);
        intent.putExtra("current_user",currentUser);
        startActivity(intent);
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
                                currentUser.getUser_id(),
                                0,
                                0
                        );

                        if(newRecipeImageUri != null){
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String extension = mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(newRecipeImageUri));
                            String imageUrl = recipeId + "." + extension;

                            File file = getImageFile(newRecipeImageUri, imageUrl);
                            ApiCaller.get_caller_instance().uploadRecipeImage(file);
                        }
                        items.add(0, new Item(recipe));
                        runOnUiThread(() -> {
                            recyclerViewAdapter.notifyItemInserted(0);
                            recipesRecyclerView.scrollToPosition(0);
                        }
                        );

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


    private void get_recipes_from_server(){
        progressBar.setVisibility(View.VISIBLE);
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getRecipePages(recipePageNumber++);

                if(apiResponse == null){
                    display_server_down_error("Server is down, Please Try again");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);
                    int startPosition = items.size();
                    System.out.println("RECIPE PAGE NUMBER " + recipePageNumber);
                    System.out.println("CURRENTLY HAVE " + startPosition + " GOT " + recipes.length);
                    for(Recipe recipe : recipes){
                        addItemThreadSafe(recipe);
                    }

                    //update recipe list on main thread
                    handler.post(() ->{
                        recyclerViewAdapter.notifyItemInserted(startPosition);
                        progressBar.setVisibility(View.GONE);
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
            currentItemBeingViewed = recyclerViewManager.findFirstVisibleItemPosition();
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
                System.out.println("deleteRecipe selected");
                Recipe dRecipe = (items.get(position).getRecipe());
                Recipe.deleteRecipe(dRecipe);

                //remove deleted recipe from UI
                for(Item recipe : items){
                    if(recipe.getRecipe().getRecipe_id() == dRecipe.getRecipe_id()){
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

                System.out.println("banUser selected, banning user with id: " + ban_id);
                User.banUser(ban_id);

                recyclerViewAdapter.notifyDataSetChanged();
                //add_recipes_to_ui();
                dialog.dismiss();
            });
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
        //check if the user is banned
        checkAndLogOutUserIfBanned();
        System.out.println("CURRENT ITEM BEING VIEWED = " + currentItemBeingViewed);
        System.out.println("SIZE OF ITEMS IS " + items.size());
        if(items.size() >= currentItemBeingViewed){
            recyclerViewAdapter.notifyItemChanged(currentItemBeingViewed);
            recyclerViewManager.scrollToPosition(currentItemBeingViewed);
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

    public static void updateItem(Recipe recipe){
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getRecipe().getRecipe_id() == recipe.getRecipe_id()){
                items.get(i).update_item(recipe);
                System.out.println("CHILD UPDATED RECIPES< " + i);
                break;
            }
        }
    }

    public void checkAndLogOutUserIfBanned(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isBanned = ApiCaller.get_caller_instance().isUserBanned(String.valueOf(currentUser.getUser_id()));

                if(isBanned){
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

    public void alertUserOfBanAndLogOut(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        String alertMessage = "Admin has banned you from the app. You will be signed out from the app.";
        alertDialog.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("DIALOG CLOSED");
                        SharedPreferences sharedPreferences = getSharedPreferences("Saved User", MODE_PRIVATE);
                        sharedPreferences.edit().remove("current_user").apply();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.setTitle("YOU HAVE BEEN BANNED");
        alert.show();
    }

}