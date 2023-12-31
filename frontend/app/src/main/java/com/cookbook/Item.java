package com.cookbook;

import com.cookbook.model.Recipe;

public class Item {
    // Fields representing properties of a recipe item
    String name;
    String author;
    int image;
    String time;
    String ing1;
    String ing2;
    int admin;
    Recipe recipe;

    // Constructor initializing an Item with a Recipe
    // Extract information from the Recipe and set it to Item fields
    // Used for creating a new Item when a Recipe is retrieved
    public Item(Recipe recipe) {
        this.name = recipe.getRecipe_name();
        this.author = Integer.toString(recipe.getUser_id());
        this.image = R.drawable.foodplaceholder;
        this.time = recipe.getPreparation_time_minutes() + "mins";
        String[] ings = recipe.getIngredients().split(",");
        this.ing1 = ings[0];
        if (ings.length == 1) {
            this.ing2 = "society";
        } else {
            this.ing2 = ings[1];
        }

        this.admin = R.drawable.ic_baseline_settings_24;
        this.recipe = recipe;
    }

    // Method to update an Item with a new Recipe
    // Update fields with information from the new Recipe
    // Used when the underlying Recipe data is modified
    public void update_item(Recipe recipe) {
        this.name = recipe.getRecipe_name();
        this.author = Integer.toString(recipe.getUser_id());
        this.image = R.drawable.foodplaceholder;
        this.time = recipe.getPreparation_time_minutes() + "mins";
        String[] ings = recipe.getIngredients().split(",");
        this.ing1 = ings[0];
        if (ings.length == 1) {
            this.ing2 = "society";
        } else {
            this.ing2 = ings[1];
        }

        this.admin = R.drawable.ic_baseline_settings_24;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }






    public String getTime() {
        return time;
    }


    public String getIng1() {
        return ing1;
    }


    public String getIng2() {
        return ing2;
    }


    public int getAdmin() {
        return admin;
    }


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", image=" + image +
                ", time='" + time + '\'' +
                ", ing1='" + ing1 + '\'' +
                ", ing2='" + ing2 + '\'' +
                ", admin=" + admin +
                ", recipe=" + recipe +
                '}';
    }
}
