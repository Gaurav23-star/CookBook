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
import com.cookbook.model.User;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class SignUpActivity extends AppCompatActivity {

    private static final String SIGNUP_URL = "http://172.16.122.20:8080/create-account";
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private TextView errorTextView;
    private static User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        System.out.println("action bar " + getSupportActionBar().toString());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditTextSignUp);
        passwordEditText = findViewById(R.id.passwordEditTextSignUp);
        signupButton = findViewById(R.id.signupButton);
        errorTextView = findViewById(R.id.errorTextViewSignUp);

        System.out.println("Signup button is " + signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(v);
            }
        });

    }


    private void signup(View v){
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String plain_password = passwordEditText.getText().toString();
        String password = BCrypt.hashpw(plain_password, BCrypt.gensalt());

        if(!isValidName(firstName, lastName) || !isValidEmail(email) || !isValidPassword(password)) return;


        final Thread thread = new Thread(() -> {

            try {
                final URL url = new URL(SIGNUP_URL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                final String jsonData = "{\"first_name\": \"" + firstName + "\"," +
                                        "\"last_name\": \"" + lastName + "\", " +
                                        "\"email_id\":\"" + email + "\", " +
                                        "\"password\":\"" + password + "\"}";

                System.out.println("Json Payload: " + jsonData);

                final OutputStream os = connection.getOutputStream();
                final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

                osw.write(jsonData);
                osw.flush();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    final InputStream responseBody = connection.getInputStream();

                    String jsonString = convertStreamToString(responseBody);
                    System.out.println("Response body: " + jsonString);

                    JSONObject jsonObject = new JSONObject(jsonString);
                    int userId = jsonObject.getInt("insertId");

                    user = new User(userId, firstName, lastName, email, password, 0, 0);
                    changeActivityToUserHome(user);
                } else {
                    System.out.println("Response code is " + responseCode);
                    printUserAlreadyExistsFailure();
                }
            } catch (Exception e) {
                printServerDownFailure();
                System.out.println("EXCEPTION OCcURRED " + e);
            }
        });
        thread.start();





    }

    private void changeActivityToUserHome(User user){
        final Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.putExtra("current_user", user);
        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void printUserAlreadyExistsFailure(){
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

    @SuppressLint("SetTextI18n")
    private void printServerDownFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("something went wrong. try again later.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }




    private boolean isValidEmail(String email){
        //can add more checking logic here

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.emailEditText.requestFocus();
            this.emailEditText.setError("Please Enter Valid Email");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password){
        //Can add more checking logic here
        if(password.isEmpty()){
            this.passwordEditText.requestFocus();
            this.passwordEditText.setError("You must enter a password");
            return false;
        }
        return true;
    }


    private boolean isValidName(String firstName, String lastName){
        if(firstName.isEmpty()){
            this.firstNameEditText.requestFocus();
            this.firstNameEditText.setError("first name required");
            return false;

        }else if(lastName.isEmpty()){
            this.lastNameEditText.requestFocus();
            this.lastNameEditText.setError("last name required");
            return false;
        }
        return true;
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }





}
