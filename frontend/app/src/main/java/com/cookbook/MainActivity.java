package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cookbook.model.User;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                User current_user = load_user();

                //no user saved, prompt login
                if(current_user == null){
                    final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    final Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("current_user", current_user);
                    startActivity(intent);
                    finish();
                }
                //Intent intent = new Intent(MainActivity.this, HomeActivity.class);

            }
        }, 2000);

    }

    public User load_user(){
        SharedPreferences sharedPreferences = getSharedPreferences("Saved User", MODE_PRIVATE);
        String user_json_string = sharedPreferences.getString("current_user", null);

        if(user_json_string != null){
            Gson gson = new Gson();
            return gson.fromJson(user_json_string, User.class);
        }
        return null;
    }
}
