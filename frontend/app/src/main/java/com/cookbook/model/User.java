package com.cookbook.model;

import androidx.annotation.NonNull;

import com.cookbook.ApiCaller;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/*
Abstract super class for users and admins.
We will never need to instantiate a "user" object,
so to protect from doing this by mistake.
 */
public class User implements Serializable {

    //fields
    protected int user_id;
    protected String first_name;
    protected String last_name;
    protected String email_id;
    protected String password;
    protected int isAdmin;
    protected int isBanned;
    protected String username;

    private static final String USER_URL = ApiCaller.host + "/update-user";

    public User(int user_id, String first_name, String last_name, String email_id, String password, int isAdmin, int isBanned, String username) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_id = email_id;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
        this.username = username;
    }

    // Copy Constructor
    public User(User user) {
        this.user_id = user.getUser_id();
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name();
        this.email_id = user.getEmail_id();
        this.password = user.getPassword();
        this.isAdmin = user.getIsAdmin();
        this.isBanned = user.getIsBanned();
        this.username = user.getUsername();
    }

    public User() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(int isBanned) {
        this.isBanned = isBanned;
    }

    public String getUsername() {
        return username;
    }

    @Override
    @NonNull
    public String toString() {
        return email_id;
    }

    public static void banUser(int user_id) {
        final Thread thread = new Thread(() -> {

            try {
                final URL url = new URL(USER_URL + "?user_id=" + user_id);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                final String jsonData = "{\"isBanned\": 1 }";

                System.out.println("Json Payload: " + jsonData);

                final OutputStream os = connection.getOutputStream();
                final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

                osw.write(jsonData);
                osw.flush();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // final InputStream responseBody = connection.getInputStream();

                    //String jsonString = convertStreamToString(responseBody);
                    System.out.println("Banned User");

                } else {
                    System.out.println("Response code is " + responseCode);
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION OCCURRED " + e);

            }
        });
        thread.start();

    }

}
