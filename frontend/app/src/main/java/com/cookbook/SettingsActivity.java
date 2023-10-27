package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.cookbook.Settings.SettingsItem;
import com.cookbook.model.User;
import com.cookbook.Settings.SettingsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements RecyclerViewInterface {


    private static User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        //retrieve user passed in
        currentUser = (User) getIntent().getSerializableExtra("current_user");

        RecyclerView recyclerView = findViewById(R.id.settings_recyclerview);

        List<SettingsItem> settingItems = new ArrayList<SettingsItem>();
        settingItems.add(new SettingsItem("Account"));
        settingItems.add(new SettingsItem("Appearance"));
        settingItems.add(new SettingsItem("Language"));
        settingItems.add(new SettingsItem("Blocked Users"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SettingsAdapter(getApplicationContext(), settingItems, this));

        handleNavigationChange();

    }


    public void onLogOutClick(View view) {
        System.out.println("LOGGED OUT");
    }

    public void handleNavigationChange(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_settings);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            switch (item.getItemId()){
                case R.id.bottom_settings:
                    return true;
                case R.id.bottom_person:
                    Intent intent_Person = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent_Person.putExtra("current_user",currentUser);
                    startActivity(intent_Person);
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

    @Override
    public void onItemClick(int position) {
        System.out.println(position );
        System.out.println("OIFOISDJFNIODSNFDIONF");
    }
}