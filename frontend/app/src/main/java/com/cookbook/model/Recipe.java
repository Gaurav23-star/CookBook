package com.cookbook.model;

import com.cookbook.ApiCaller;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class Recipe implements Serializable {

    // Fields representing attributes of a recipe
    protected int recipe_id;
    protected String recipe_name;
    protected int servings;
    protected int preparation_time_minutes;
    protected String ingredients;
    protected String description;
    protected String instructions;
    protected int user_id;
    protected int num_comments;
    protected int num_likes;

    // Base URL for recipe-related API calls
    private static final String RECIPE_URL = ApiCaller.host + "/user-defined-recipes";

    // Constructor for creating a Recipe object
    public Recipe(int recipe_id, String recipe_name, int servings, int preparation_time_minutes, String ingredients, String description, String instructions, int user_id, int num_comments, int num_likes) {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.servings = servings;
        this.preparation_time_minutes = preparation_time_minutes;
        this.ingredients = ingredients;
        this.description = description;
        this.instructions = instructions;
        this.user_id = user_id;
        this.num_comments = num_comments;
        this.num_likes = num_likes;
    }

    // Default constructor
    public Recipe() {

    }

    // Getter method to retrieve the number of comments on the recipe
    public int getNum_comments() {
        return num_comments;
    }

    // Setter method to set the number of comments on the recipe
    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    // Getter method to retrieve the number of likes on the recipe
    public int getNum_likes() {
        return num_likes;
    }

    // Setter method to set the number of likes on the recipe
    public void setNum_likes(int num_likes) {
        this.num_likes = num_likes;
    }

    // Getter method to retrieve the ID of the recipe
    public int getRecipe_id() {
        return recipe_id;
    }

    // Setter method to set the ID of the recipe
    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    // Getter method to retrieve the name of the recipe
    public String getRecipe_name() {
        return recipe_name;
    }

    // Setter method to set the name of the recipe
    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    // Getter method to retrieve the number of servings
    public int getServings() {
        return servings;
    }

    // Setter method to set the number of servings
    public void setServings(int servings) {
        this.servings = servings;
    }

    // Getter method to retrieve the preparation time in minutes
    public int getPreparation_time_minutes() {
        return preparation_time_minutes;
    }

    // Setter method to set the preparation time in minutes
    public void setPreparation_time_minutes(int preparation_time_minutes) {
        this.preparation_time_minutes = preparation_time_minutes;
    }

    // Getter method to retrieve the list of ingredients
    public String getIngredients() {
        return ingredients;
    }

    // Setter method to set the list of ingredients
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    // Getter method to retrieve the description of the recipe
    public String getDescription() {
        return description;
    }

    // Setter method to set the description of the recipe
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter method to retrieve the cooking instructions
    public String getInstructions() {
        return instructions;
    }

    // Setter method to set the cooking instructions
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    // Getter method to retrieve the ID of the user who created the recipe
    public int getUser_id() {
        return user_id;
    }

    // Setter method to set the ID of the user who created the recipe
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    // String representation of the Recipe object
    @Override
    public String toString() {
        return "Recipe{" +
                "recipe_id=" + recipe_id +
                ", recipe_name='" + recipe_name + '\'' +
                ", servings=" + servings +
                ", preparation_time_minutes=" + preparation_time_minutes +
                ", ingredients='" + ingredients + '\'' +
                ", description='" + description + '\'' +
                ", instructions='" + instructions + '\'' +
                ", user_id=" + user_id +
                ", numberOfCommentsOnThisRecipe=" + num_comments +
                ", numberOfLikesOnThisRecipe=" + num_likes +
                '}';
    }

    // Method to delete a recipe using API call
    public static void deleteRecipe(Recipe recipe) {

        final Thread thread = new Thread(() -> {
            try {
                String deleteURL = RECIPE_URL + "?recipe_id=" + recipe.getRecipe_id() + "&user_id=" + recipe.getUser_id();
                URL url = new URL(deleteURL);
                final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //sets type of request
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-length", "0");
                connection.setDoOutput(false);

                connection.connect();
                int response = connection.getResponseCode();

            } catch (Exception ignored) {

            }
        });
        thread.start();
    }
}
