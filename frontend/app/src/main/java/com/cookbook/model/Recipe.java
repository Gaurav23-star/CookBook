package com.cookbook.model;

import com.cookbook.ApiCaller;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class Recipe implements Serializable {

    protected int recipe_id;
    protected String recipe_name;
    protected int servings;
    protected int preparation_time_minutes;
    protected String ingredients;
    protected String description;
    protected String instructions;
    protected int user_id;
    private static final String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";

    public Recipe(int recipe_id, String recipe_name, int servings, int preparation_time_minutes, String ingredients, String description, String instructions, int user_id) {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.servings = servings;
        this.preparation_time_minutes = preparation_time_minutes;
        this.ingredients = ingredients;
        this.description = description;
        this.instructions = instructions;
        this.user_id = user_id;
    }
    public Recipe() {

    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getPreparation_time_minutes() {
        return preparation_time_minutes;
    }

    public void setPreparation_time_minutes(int preparation_time_minutes) {
        this.preparation_time_minutes = preparation_time_minutes;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public static void deleteRecipe (Recipe recipe) {

        final Thread thread = new Thread (() -> {
            try {
                String deleteURL = RECIPE_URL + "?recipe_id=" + recipe.getRecipe_id() + "&user_id=" + recipe.getUser_id();
                URL url = new URL(deleteURL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //sets type of request
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-length", "0");
                connection.setDoOutput(false);
                //since get we just need to do this i think as well?
                connection.connect();

                final int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("DELETED SUCCESSFULLY");
                } else {
                    System.out.println("Could not delete for some reason.");
                }

            } catch (Exception e) {
                System.out.println("EXCEPTION OCCURRED " + e);
            }
        });
        thread.start();
    }
}
