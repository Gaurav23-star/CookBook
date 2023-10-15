package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.cookbook.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    //private static final String LOGIN_URL = "http://127.0.0.1:8080/login";

    private static final String LOGIN_URL = "http://172.16.122.20:8080/login";
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView errorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorTextView = findViewById(R.id.ErrorTextView);


    }

    public void onLoginClick(final View view) {
        this.errorTextView.setVisibility(View.INVISIBLE);
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        //return if input is not valid
        if(!isValidEmail(email) || !isValidPassword(password)) return;

        System.out.println("Email: " + email + ", Password: " + password);

        final Thread thread = new Thread(() -> {
            try {
                final URL url = new URL(LOGIN_URL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                final String jsonData = "{\"email_id\":\"" + email + "\", \"password\":\"" + password + "\"}";
                System.out.println("Json Payload: " + jsonData);

                final OutputStream os = connection.getOutputStream();
                final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

                osw.write(jsonData);
                osw.flush();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    final InputStream responseBody = connection.getInputStream();
                    String response = convertStreamToString(responseBody);
                    User user = getUserFromJson(response);
                    System.out.println("Response body: " + response);
                    System.out.println(user.toString());
                    changeActivityToUserHome(user);

                } else {
                    System.out.println("ERROR " + responseCode);
                    printInvalidCredentialsLoginFailure();
                }
            } catch (Exception e) {
                printServerDownFailure();
                System.out.println("EXCEPTION OCcURRED " + e);
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private void changeActivityToUserHome(User user){
        final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("current_user", user);
        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void printInvalidCredentialsLoginFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("invalid credentials. please try again.");
                errorTextView.setVisibility(View.VISIBLE);
                passwordEditText.setText("");
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void printServerDownFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextView.setText("something went wrong. please try later.");
                errorTextView.setVisibility(View.VISIBLE);
            }
        });


    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private User getUserFromJson(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject userJson = jsonObject.getJSONObject("user");
            User user = new User(
            userJson.getInt("user_id"),
            userJson.getString("first_name"),
            userJson.getString("last_name"),
            userJson.getString("email_id"),
                    userJson.getInt("isAdmin") == 1,
            userJson.getInt("isBanned") == 1
            );
            return user;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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



    public void onSignUpClick(View view){
        //TODO
        System.out.println("Signup clicked!");

    }
}
