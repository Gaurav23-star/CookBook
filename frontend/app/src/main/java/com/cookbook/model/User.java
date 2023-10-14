package com.cookbook.model;

import androidx.annotation.NonNull;

/*
Abstract super class for users and admins.
We will never need to instantiate a "user" object,
so to protect from doing this by mistake.
 */
public class User {

    //fields
    protected int userID;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected boolean isAdmin;

    //cons
    public User (int userID, String username, String firstName, String lastName, String email, boolean isAdmin) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    //getters
    public int getUserID() {return userID;}
    public String getUsername() {return username;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getEmail() {return email;}
    public boolean getIsAdmin(){return isAdmin;}

    @Override @NonNull
    public String toString() {
        return username;
    }

/*
    not sure whats redundant here
    implement this later, add the getters as well

    private ArrayList<Recipe> recipes;
    private ArrayList<User> followedUsers;
*/

}
