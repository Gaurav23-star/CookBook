package com.cookbook;

import com.cookbook.model.Recipe;

public class Item {
    String name;
    String author;
    int image;
    String time;
    String ing1;
    String ing2;
    int admin;
    Recipe recipe;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIng1() {
        return ing1;
    }

    public void setIng1(String ing1) {
        this.ing1 = ing1;
    }

    public String getIng2() {
        return ing2;
    }

    public void setIng2(String ing2) {
        this.ing2 = ing2;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
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
