package com.cookbook.model;

public class Admin extends User {

    public Admin (int userID, String username, String firstName, String lastName, String email, boolean isAdmin) {
        super(userID, username, firstName, lastName, email, isAdmin);
    }

    //prototypes
    public void banUser(){}
    public void deleteRecipe(){}

}
