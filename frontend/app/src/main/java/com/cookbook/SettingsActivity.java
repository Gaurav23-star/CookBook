package com.cookbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class SettingsActivity extends AppCompatActivity implements RecyclerViewInterface {


    private static User currentUser;
    private Button logout;
    private TextView fullName;
    private TextView userName;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("LOGOUT");


        logout = findViewById(R.id.logout_button);
        fullName = findViewById(R.id.settings_FullName_TextView);
        userName = findViewById(R.id.settings_UserName_TextView);
        cardView = findViewById(R.id.settings_CardView);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_User();
            }
        });

        //retrieve user passed in
        currentUser = (User) getIntent().getSerializableExtra("current_user");
        fullName.setText(currentUser.getFirst_name() + " " + currentUser.getLast_name());
        userName.setText("@" + currentUser.getUsername());

        handleNavigationChange();

    }

    private void logout_User() {
        SharedPreferences sharedPreferences = getSharedPreferences("Saved User", MODE_PRIVATE);
        sharedPreferences.edit().remove("current_user").apply();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void handleNavigationChange() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_settings);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_settings:
                    return true;
                case R.id.bottom_person:
                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user", currentUser);
                    startActivity(intent_Person);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();

                    return true;
                case R.id.bottom_notifications:
                    Intent intent_Notifications = new Intent(getApplicationContext(), NotificationsActivity.class);
                    intent_Notifications.putExtra("current_user", currentUser);
                    startActivity(intent_Notifications);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_favorites:
                    Intent intent_Favorites = new Intent(getApplicationContext(), FavoriteActivity.class);
                    intent_Favorites.putExtra("current_user", currentUser);
                    startActivity(intent_Favorites);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;

                case R.id.bottom_home:

                    Intent intent_Home = new Intent(getApplicationContext(), HomeActivity.class);
                    intent_Home.putExtra("current_user", currentUser);
                    startActivity(intent_Home);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();


                    return true;
            }
            return false;
        });
    }

    @Override
    public void onItemClick(int position) {


    }
}