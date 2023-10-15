package com.cookbook.model;

import androidx.annotation.NonNull;

/*
Abstract super class for users and admins.
We will never need to instantiate a "user" object,
so to protect from doing this by mistake.
 */
public class User {

    //fields
    private int userID;
    //We don't have username in our database schema yet
    //private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isAdmin;
    private boolean isBanned;

    //cons
    public User (int userID, String firstName, String lastName, String email, boolean isAdmin, boolean isBanned) {
        this.userID = userID;
        //this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
    }
    public User (){

    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
               // ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", isBanned=" + isBanned +
                '}';
    }

    //getters and setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    /*
    not sure whats redundant here
    implement this later, add the getters as well

    private ArrayList<Recipe> recipes;
    private ArrayList<User> followedUsers;
*/

}
