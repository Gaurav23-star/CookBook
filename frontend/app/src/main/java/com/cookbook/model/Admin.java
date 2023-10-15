package com.cookbook.model;

public class Admin extends User {

    public Admin (int userID, String firstName, String lastName, String email, boolean isAdmin, boolean isBanned) {
        super(userID, firstName, lastName, email, isAdmin, isBanned);
    }

    //prototypes
    public void banUser(){}
    public void deleteRecipe(){}

}
