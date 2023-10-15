package com.cookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
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
                    System.out.println("Response body: " + convertStreamToString(responseBody));
                } else {
                    throw new Exception("HTTP Request Failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
