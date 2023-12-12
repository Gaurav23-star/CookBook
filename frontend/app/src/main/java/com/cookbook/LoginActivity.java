package com.cookbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.User;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    // UI elements
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView errorTextView;

    static User user;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorTextView = findViewById(R.id.ErrorTextView);
    }

    // Click handler for the login button
    public void onLoginClick(final View view) {
        this.errorTextView.setVisibility(View.INVISIBLE);
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();


        // return if input is not valid
        if (!isValidEmail(email) || !isValidPassword(password)) return;


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().login(email, password);

                if (apiResponse == null) {
                    printServerDownFailure();
                    return;
                }

                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {

                    user = gson.fromJson(apiResponse.getResponse_body(), User.class);
                    save_user_to_device(user);
                    changeActivityToUserHome(user);
                } else if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    printInvalidCredentialsLoginFailure(apiResponse.getResponse_body());

                } else {
                    printInvalidCredentialsLoginFailure("Invalid Credentials");
                }
            }
        });
        thread.start();
    }

    // Method to navigate to the home activity with the current user
    private void changeActivityToUserHome(User user) {
        final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("current_user", user);
        startActivity(intent);
        finish();
    }

    // Method to display an error message for invalid credentials
    @SuppressLint("SetTextI18n")
    private void printInvalidCredentialsLoginFailure(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("invalid credentials. please try again.");
                errorTextView.setVisibility(View.VISIBLE);
                passwordEditText.setText("");
            }
        });
    }

    // Method to display an error message for server down failure
    @SuppressLint("SetTextI18n")
    private void printServerDownFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("something went wrong. please try later.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isValidEmail(String email) {
        // can add more checking logic here

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.requestFocus();
            this.emailEditText.setError("Please Enter Valid Email");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        // Can add more checking logic here
        if (password.isEmpty()) {
            this.passwordEditText.requestFocus();
            this.passwordEditText.setError("You must enter a password");
            return false;
        }
        return true;
    }

    public void onSignUpClick(View view) {
        // TODO

        final Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    //save user information to device
    private void save_user_to_device(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences("Saved User", MODE_PRIVATE);
        sharedPreferences.edit().putString("current_user", new Gson().toJson(user)).apply();
    }
}
