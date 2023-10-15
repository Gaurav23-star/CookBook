package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

    }

    public void onLoginClick(final View view) {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
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
                    System.out.println("Response body: " + response);
                    System.out.println(getUserFromJson(response).toString());

                } else {
                    System.out.println("ERROR " + responseCode);
                    throw new Exception("HTTP Request Failed with response code: " + responseCode);

                }
            } catch (Exception e) {
                System.out.println("EXCEPTION OCcURRED " + e);
                e.printStackTrace();
            }
        });

        thread.start();
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



    public void onSignUpClick(View view){
        //TODO
        System.out.println("Signup clicked!");

    }
}
