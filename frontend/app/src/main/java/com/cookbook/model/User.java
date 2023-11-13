package com.cookbook.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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

    private static final String USERS_URL = "http://172.16.122.20:8080/update-user";

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

    public int getUser_id() {return user_id;}
    public void setUser_id(int user_id) {this.user_id = user_id;}

    public String getFirst_name() {return first_name;}
    public void setFirst_name(String first_name) {this.first_name = first_name;}

    public String getLast_name() {return last_name;}
    public void setLast_name(String last_name) {this.last_name = last_name;}

    public String getEmail_id() {return email_id;}
    public void setEmail_id(String email_id) {this.email_id = email_id;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public int getIsAdmin() {return isAdmin;}
    public void setIsAdmin(int isAdmin) {this.isAdmin = isAdmin;}

    public int getIsBanned() {return isBanned;}
    public void setIsBanned(int isBanned) {this.isBanned = isBanned;}

    public String getUsername() {return username;}

    @Override @NonNull
    public String toString() {
        return email_id;
    }

    public static void banUser (int user_id) {

        final Thread thread = new Thread (() -> {
            try {

               String updateURL = USERS_URL + "?user_id=" + user_id;
               URL url = new URL(updateURL);
               final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

               connection.setRequestMethod("PATCH");
               connection.setRequestProperty("Content-Length", "0");
               connection.setDoOutput(false);

               //final String jsonData = new Gson().toJson(user);

               //System.out.println("Ban Status of " + user.getUser_id() + ":" + user.getIsBanned());

               //final OutputStream os = connection.getOutputStream();
               //final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

               //osw.write(jsonData);
               //osw.flush();

               final int responseCode = connection.getResponseCode();
               if (responseCode == HttpURLConnection.HTTP_OK) {

                   System.out.println("Banned Successfully");
                   //final InputStream responseBody = connection.getInputStream();

                   //String jsonString = User.convertStreamToString(responseBody);
                   //System.out.println("Response body: " + jsonString);

               } else {
                   System.out.println("Could not ban");
               }

            } catch (Exception e) {
                System.out.println("EXCEPTION OCCURRED" + e);
            }
        });
        thread.start();
    }

    private static String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}
