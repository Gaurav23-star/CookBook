package com.cookbook.model;

import com.cookbook.ApiCaller;

import java.net.HttpURLConnection;
import java.net.URL;

public class Comment {
    private int user_id;
    private int recipe_id;
    private String username;
    private String comment;
    private int comment_id;

    private static final String COMMENT_URL = ApiCaller.host + "/user-defined-recipes/comments";


    public Comment(int user_id, int recipe_id, String username, String comment, int comment_id) {
        this.user_id = user_id;
        this.recipe_id = recipe_id;
        this.username = username;
        this.comment = comment;
        this.comment_id = comment_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user_id=" + user_id +
                ", recipe_id=" + recipe_id +
                ", user_name='" + username + '\'' +
                ", comment_content='" + comment + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

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
