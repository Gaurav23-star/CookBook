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

    // Fields representing attributes of a user
    protected int user_id;
    protected String first_name;
    protected String last_name;
    protected String email_id;
    protected String password;
    protected int isAdmin;
    protected int isBanned;
    protected String username;

    // Base URL for user-related API calls
    private static final String USER_URL = ApiCaller.host + "/update-user";

    // Constructor for creating a User object with specified attributes
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

    // Default constructor
    public User() {

    }

    // Getter method to retrieve the user ID
    public int getUser_id() {
        return user_id;
    }



    // Getter method to retrieve the first name of the user
    public String getFirst_name() {
        return first_name;
    }



    // Getter method to retrieve the last name of the user
    public String getLast_name() {
        return last_name;
    }


    // Getter method to retrieve the email ID of the user
    public String getEmail_id() {
        return email_id;
    }


    // Getter method to retrieve the password of the user
    public String getPassword() {
        return password;
    }


    // Getter method to retrieve the isAdmin status (1 if admin, 0 otherwise)
    public int getIsAdmin() {
        return isAdmin;
    }


    // Getter method to retrieve the isBanned status (1 if banned, 0 otherwise)
    public int getIsBanned() {
        return isBanned;
    }



    // Getter method to retrieve the username of the user
    public String getUsername() {
        return username;
    }

    // String representation of the User object (email ID)
    @Override
    @NonNull
    public String toString() {
        return email_id;
    }


    // Method to ban a user using API call
    public static void banUser(int user_id) {
        final Thread thread = new Thread(() -> {

            try {
                final URL url = new URL(USER_URL + "?user_id=" + user_id);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("PATCH");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                final String jsonData = "{\"isBanned\": 1 }";


                final OutputStream os = connection.getOutputStream();
                final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

                osw.write(jsonData);
                osw.flush();
                final int responseCode = connection.getResponseCode();

            } catch (Exception ignored) {


            }
        });
        thread.start();

    }

}
