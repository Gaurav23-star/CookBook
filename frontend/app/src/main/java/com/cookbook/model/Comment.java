package com.cookbook.model;

import com.cookbook.ApiCaller;

import java.net.HttpURLConnection;
import java.net.URL;

public class Comment {

    // Fields representing attributes of a comment
    private int user_id;
    private int recipe_id;
    private String username;
    private String comment;
    private int comment_id;

    // API URL for comments
    private static final String COMMENT_URL = ApiCaller.host + "/user-defined-recipes/comments";


    // Constructor for creating a Comment object
    public Comment(int user_id, int recipe_id, String username, String comment, int comment_id) {
        this.user_id = user_id;
        this.recipe_id = recipe_id;
        this.username = username;
        this.comment = comment;
        this.comment_id = comment_id;
    }

    // Getter method to retrieve the comment ID
    public int getComment_id() {
        return comment_id;
    }

    // Setter method to set the comment ID
    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    // Override toString method to provide a string representation of the Comment object
    @Override
    public String toString() {
        return "Comment{" +
                "user_id=" + user_id +
                ", recipe_id=" + recipe_id +
                ", user_name='" + username + '\'' +
                ", comment_content='" + comment + '\'' +
                '}';
    }

    // Getter method to retrieve the username associated with the comment
    public String getUsername() {
        return username;
    }

    // Setter method to set the username associated with the comment
    public void setUsername(String username) {
        this.username = username;
    }



    // Getter method to retrieve the user ID associated with the comment
    public int getUser_id() {
        return user_id;
    }

    // Setter method to set the user ID associated with the comment
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    // Getter method to retrieve the recipe ID associated with the comment
    public int getRecipe_id() {
        return recipe_id;
    }

    // Setter method to set the recipe ID associated with the comment
    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    // Getter method to retrieve the content of the comment
    public String getComment() {
        return comment;
    }

    // Setter method to set the content of the comment
    public void setComment(String comment) {
        this.comment = comment;
    }

    // Method to delete a comment with a specified comment ID
    public static void deleteComment(int comment_id) {

        final Thread thread = new Thread(() -> {
            try {
                String deleteURL = COMMENT_URL + "?commentId=" + comment_id;
                URL url = new URL(deleteURL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //sets type of request
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-length", "0");
                connection.setDoOutput(false);

                connection.connect();

                final int responseCode = connection.getResponseCode();
            } catch (Exception ignored) {

            }
        });
        thread.start();
    }
}
