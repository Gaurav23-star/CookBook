package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cookbook.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        //retrieve user passed in
        currentUser = (User) getIntent().getSerializableExtra("current_user");

        handleNavigationChange();
    }

    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_person);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_person:
                    return true;
                case R.id.bottom_settings:
                    Intent intent_Settings = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent_Settings.putExtra("current_user",currentUser);
                    startActivity(intent_Settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();


                    return true;
                case R.id.bottom_home:
                      Intent intent_Home = new Intent(getApplicationContext(), HomeActivity.class);
                      intent_Home.putExtra("current_user",currentUser);
                      startActivity(intent_Home);
                      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                      finish();

                    return true;
            }
            return false;
        });
    }
}