package com.cookbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    // API URL for user signup
    private static final String SIGNUP_URL = "http://172.16.122.20:8080/create-account";
    // UI elements
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private TextView errorTextView;
    private static User user;


    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditTextSignUp);
        passwordEditText = findViewById(R.id.passwordEditTextSignUp);
        usernameEditText = findViewById(R.id.usernameEditTextSignUp);
        signupButton = findViewById(R.id.signupButton);
        errorTextView = findViewById(R.id.errorTextViewSignUp);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(v);
            }
        });

    }


    // Handles the signup button click event
    private void signup(View v) {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        if (!isValidName(firstName, lastName) || !isValidEmail(email) || !isValidPassword(password) || !isValidUserName(username))
            return;


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ApiResponse apiResponse = ApiCaller.get_caller_instance().signup(firstName, lastName, email, password, username);
                if (apiResponse == null) {
                    printServerDownFailure();
                    return;
                }
                //User successfully created
                if (apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK) {
                    //redirect user to home activity
                    try {
                        int user_id = new JSONObject(apiResponse.getResponse_body()).getInt("insertId");
                        user = new User(user_id, firstName, lastName, email, password, 0, 0, username);
                        changeActivityToUserHome(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    printUserAlreadyExistsFailure();
                }
            }
        });
        thread.start();


    }

    // Changes the activity to the home activity for the signed-up user
    private void changeActivityToUserHome(User user) {
        final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        //intent.putExtra("current_user", user);
        startActivity(intent);
        finish();
    }

    // Displays an error message for a user already exists failure
    @SuppressLint("SetTextI18n")
    private void printUserAlreadyExistsFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("user already exists with email.");
                errorTextView.setVisibility(View.VISIBLE);
                emailEditText.requestFocus();
                emailEditText.setText("");
            }
        });
    }

    // Displays an error message for a server down failure
    @SuppressLint("SetTextI18n")
    private void printServerDownFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("something went wrong. try again later.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }


    // Validates the email format
    private boolean isValidEmail(String email) {
        //can add more checking logic here

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.requestFocus();
            this.emailEditText.setError("Please Enter Valid Email");
            return false;
        }
        return true;
    }

    // Validates the password
    private boolean isValidPassword(String password) {
        //Can add more checking logic here
        if (password.isEmpty()) {
            this.passwordEditText.requestFocus();
            this.passwordEditText.setError("You must enter a password");
            return false;
        }
        return true;
    }

    private boolean isValidUserName(String username) {
        if (username.isEmpty()) {
            this.usernameEditText.requestFocus();
            this.usernameEditText.setError("You must enter a username");
            return false;
        }
        return true;
    }


    // Validates the username
    private boolean isValidName(String firstName, String lastName) {
        if (firstName.isEmpty()) {
            this.firstNameEditText.requestFocus();
            this.firstNameEditText.setError("first name required");
            return false;

        } else if (lastName.isEmpty()) {
            this.lastNameEditText.requestFocus();
            this.lastNameEditText.setError("last name required");
            return false;
        }
        return true;
    }


}
