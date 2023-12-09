package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ProfileActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static User currentUser;
    private static User loggedInUser;
    private SwipeRefreshLayout swipeRefreshLayout;
    static Recipe recipe;
    boolean isFollowing;


    private final Gson gson = new Gson();

    private static  List<Item> items = Collections.synchronizedList(new ArrayList<Item>());
    private static  List<Item> currentUserItems = Collections.synchronizedList(new ArrayList<Item>());
    private static  List<Item> visitingUserItems = Collections.synchronizedList(new ArrayList<Item>());


    private TextView server_error_text;

    private TextView postsNumber;
    private TextView followersNumber;
    private TextView followingNumber;
    private TextView userName;
    private TextView fullName;
    private TextView biography;
    private Button followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_profile);
        //retrieve user passed in
        if(getIntent().getSerializableExtra("visiting_user") != null) {
            currentUser = (User) getIntent().getSerializableExtra("visiting_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");

            items = visitingUserItems;
            followButton = findViewById(R.id.follow_button);

            if(currentUser.getUser_id() == loggedInUser.getUser_id()){
                followButton.setVisibility(View.GONE);
            }else {
                followButton.setVisibility(View.VISIBLE);
                is_user_following_visitor(String.valueOf(loggedInUser.getUser_id()), String.valueOf(currentUser.getUser_id()));
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFollowStatus();
                    }
                });

            }


        }else if(getIntent().getSerializableExtra("current_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("current_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");
            items = currentUserItems;
        }

        swipeRefreshLayout = findViewById(R.id.profile_refreshLayout);
        server_error_text = findViewById(R.id.profile_serverErrorTextView);
        userName = findViewById(R.id.userNameTextView);
        fullName = findViewById(R.id.FullNameTextView);
        biography = findViewById(R.id.bioTextView);
        followersNumber = findViewById(R.id.followersNumber);
        followingNumber = findViewById(R.id.followingNumber);
        fullName.setText(currentUser.getFirst_name() + " " + currentUser.getLast_name() );
        userName.setText(currentUser.getUsername());
        postsNumber = findViewById(R.id.postsNumber);

        //get following count
        get_Users_Following_And_FollowingCount();

        //update posts number to the UI
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                postsNumber.setText(String.valueOf(items.size()));
            }
        });

        //user created recipes not loaded from server
//        if(items.size()== 0){
//            System.out.println("--------LIST IS EMPTY--------");
//            get_user_created_recipes_from_server();
//
//        }else{
//            add_recipes_to_ui();
//        }
        add_recipes_to_ui();

        followersNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);

                if(getIntent().getSerializableExtra("visiting_user")!= null){
                    intent.putExtra("visiting_user",currentUser);
                    intent.putExtra("id",String.valueOf(currentUser.getUser_id()) );
                }else{
                    intent.putExtra("id",String.valueOf(loggedInUser.getUser_id()) );
                }

                intent.putExtra("title","followers");
                intent.putExtra("current_user", loggedInUser);

                startActivity(intent);
            }
        });

        followingNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);

                if(getIntent().getSerializableExtra("visiting_user")!= null){
                    intent.putExtra("visiting_user",currentUser);
                    intent.putExtra("id",String.valueOf(currentUser.getUser_id()) );
                }else{
                    intent.putExtra("id",String.valueOf(loggedInUser.getUser_id()) );
                }
                intent.putExtra("title","following");
                intent.putExtra("current_user", loggedInUser);


                startActivity(intent);
            }
        });
        get_user_created_recipes_from_server();//added

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                server_error_text.setVisibility(View.GONE);
                get_user_created_recipes_from_server();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // ------ Navigation Choice ----
        handleNavigationChange();
    }

    private void add_recipes_to_ui(){
        RecyclerView recyclerView = findViewById(R.id.profile_recyclerview);
        ProfileAdapter adapter = new ProfileAdapter(getApplicationContext(),items,this, currentUser.getIsAdmin());// ---
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void get_Users_Following_And_FollowingCount(){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getUsersFollowersAndFollowingCount(String.valueOf(currentUser.getUser_id()));

                if(apiResponse == null){
                    System.out.println("ERROR in USERS_FOLLOWER_FUNCTION ");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    JsonElement root = new JsonParser().parse(apiResponse.getResponse_body());
                    if (root.isJsonArray()) {
                        JsonObject firstObject = root.getAsJsonArray().get(0).getAsJsonObject();
                        int numFollowers = firstObject.get("count").getAsInt();

                        JsonObject secondObject = root.getAsJsonArray().get(1).getAsJsonObject();
                        int numFollowing = secondObject.get("count").getAsInt();

                        handler.post(() ->{
                            followersNumber.setText(String.valueOf(numFollowers));
                            followingNumber.setText(String.valueOf(numFollowing));
                        });
                    } else {
                        System.out.println("The root element is not a JSON array");
                    }

                }else{
                    System.out.println("ERR in USERS_FOLLOWER_FUNCTION ----1 ");
                }

            }
        });
        thread.start();

    }

private void get_user_created_recipes_from_server(){
        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().getAllUserCreatedRecipes(String.valueOf(currentUser.getUser_id()));

                if(apiResponse == null){
                    display_server_down_error("Server is down, Please Try again");
                    return;
                }

                if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                    Recipe[] recipes = gson.fromJson(apiResponse.getResponse_body(), Recipe[].class);
                    items.clear();

                    for(Recipe recipe : recipes){
                        addItemThreadSafe(recipe);
                    }

                    //update recipe list on main thread
                    handler.post(() ->{
                        add_recipes_to_ui();
                        postsNumber.setText(String.valueOf(items.size()));
                    });


                }else{
                    display_server_down_error("Something went wrong.");
                }

            }
        });
        thread.start();

}


    //method to ensure thread safety
    public synchronized void addItemThreadSafe(Recipe recipe) {
        items.add(new Item (recipe));
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void display_server_down_error(String errorText){
        server_error_text.setText(errorText);
        server_error_text.setVisibility(View.VISIBLE);
    }

    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_person);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_person:
                    Intent intent_self = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_self.putExtra("current_user",loggedInUser);
                    startActivity(intent_self);
                    finish();
                    return true;
                case R.id.bottom_settings:
                    Intent intent_Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent_Settings.putExtra("current_user",loggedInUser);
                    startActivity(intent_Settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_notifications:
                    Intent intent_Notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                    intent_Notifications.putExtra("current_user",loggedInUser);
                    startActivity(intent_Notifications);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_favorites:
                    Intent intent_Favorites = new Intent(getApplicationContext(), FavoriteActivity.class);
                    intent_Favorites.putExtra("current_user",loggedInUser);
                    startActivity(intent_Favorites);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                case R.id.bottom_home:
                      Intent intent_Home = new Intent(getApplicationContext(), HomeActivity.class);
                      intent_Home.putExtra("current_user",loggedInUser);
                      startActivity(intent_Home);
                      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                      finish();
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onItemClick(int position) {
        if(currentUser.getIsAdmin()==0){
            changeActivityToRecipeActivity(currentUser, items.get(position).getRecipe());
        }
    }
    private void changeActivityToRecipeActivity(User user, Recipe recipe){
        final Intent intent = new Intent(ProfileActivity.this, RecipeActivity.class);
        intent.putExtra("current_user",user);
        intent.putExtra("current_recipe", recipe);
        startActivity(intent);
    }



private void is_user_following_visitor(String loggedInUserId, String currentUserId){

    final Thread thread = new Thread(new Runnable() {
        final Handler handler = new Handler(Looper.getMainLooper());


        @Override
        public void run() {
            ApiResponse apiResponse = ApiCaller.get_caller_instance().getUserIsFollowingVisitingUser(loggedInUserId, currentUserId);

            System.out.println("logged in user : " + loggedInUserId);
            System.out.println("currentUserId in user : " + currentUserId);

            if(apiResponse == null){
                System.out.println("ERROR in USERS_FOLLOWER_FUNCTION ");
                return;
            }

            if(apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
                JsonElement root = new JsonParser().parse(apiResponse.getResponse_body());
                if (root.isJsonArray()) {
                    JsonObject firstObject = root.getAsJsonArray().get(0).getAsJsonObject();
                    int isLoggedInUserFollowingCurrentUser = firstObject.get("COUNT(*)").getAsInt();
                    if(isLoggedInUserFollowingCurrentUser > 0){
                        isFollowing = true;
                        System.out.println("---------------- LOGGED IN USER IS FOLLOWING VISITING USER ------");
                    }else{
                        isFollowing = false;
                        System.out.println("---------------- LOGGED IN USER IS NOOOOOOOTTTT FOLLOWING VISITING USER ------");
                    }

                    // Post a Runnable to the main thread to update the UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isFollowing) {
                                followButton.setText("Following");
                            } else {
                                followButton.setText("Follow");
                            }
                        }
                    });

                } else {
                    System.out.println("The root element is not a JSON array");
                }

            }else{
                System.out.println("ERR in USERS_FOLLOWER_FUNCTION ----- 2");
            }

        }
    });
        thread.start();
}


    private void updateFollowStatus(){

        final Thread thread = new Thread(new Runnable() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void run() {
                ApiResponse apiResponse;
                ApiResponse apiResponseTwo;

                isFollowing = !isFollowing;

                if (isFollowing){
                    apiResponse = ApiCaller.get_caller_instance().UserFollowVisitingUser( String.valueOf(loggedInUser.getUser_id()), String.valueOf(currentUser.getUser_id()) );
                    apiResponseTwo = ApiCaller.get_caller_instance().postUserNotification("follow", String.valueOf(currentUser.getUser_id()), String.valueOf(loggedInUser.getUser_id()), null);
                    System.out.println("1111111111111111111--------");
                }else{
                    System.out.println("222221212122222323232323232--------");
                    apiResponse = ApiCaller.get_caller_instance().UserUnfollowVisitingUser( String.valueOf(loggedInUser.getUser_id()), String.valueOf(currentUser.getUser_id()) );
                    apiResponseTwo = ApiCaller.get_caller_instance().removeUserNotification("follow", String.valueOf(currentUser.getUser_id()), String.valueOf(loggedInUser.getUser_id()), null );
                }

                if( (apiResponse != null && apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) && (apiResponseTwo != null && apiResponseTwo.getResponse_code() == HttpURLConnection.HTTP_OK) ){
                    //JsonElement root = new JsonParser().parse(apiResponse.getResponse_body());
                    try {

                        // Post a Runnable to the main thread to update the UI
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                followButton.setText(isFollowing ? "Following" : "Follow");
                                get_Users_Following_And_FollowingCount();
                            }
                        });

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("ERR in USERS_FOLLOWER_FUNCTION");
                }

            }
        });
        thread.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Clear the items list to ensure it's empty before repopulating it
        items.clear();
        if(getIntent().getSerializableExtra("visiting_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("visiting_user");

            items = visitingUserItems;
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");

            followButton = findViewById(R.id.follow_button);

            if(currentUser.getUser_id() == loggedInUser.getUser_id()) {
                followButton.setVisibility(View.GONE);
            }else {
                followButton.setVisibility(View.VISIBLE);

                is_user_following_visitor(String.valueOf(loggedInUser.getUser_id()), String.valueOf(currentUser.getUser_id()));
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFollowStatus();
                    }
                });
            }


        }else if(getIntent().getSerializableExtra("current_user") != null){
            currentUser = (User) getIntent().getSerializableExtra("current_user");
            loggedInUser = (User) getIntent().getSerializableExtra("current_user");
            items = currentUserItems;
        }

        get_user_created_recipes_from_server();
    }
}