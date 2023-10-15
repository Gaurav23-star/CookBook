package com.cookbook.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

/*
Abstract super class for users and admins.
We will never need to instantiate a "user" object,
so to protect from doing this by mistake.
 */
public class User implements Serializable {

    //fields
    protected int user_id;
    protected String first_name;
    protected String last_name;
    protected String email_id;
    protected String password;
    protected int isAdmin;
    protected int isBanned;

    public int getUser_id() {return user_id;}
    public void setUser_id(int user_id) {this.user_id = user_id;}

    public String getFirst_name() {return first_name;}
    public void setFirst_name(String first_name) {this.first_name = first_name;}

    public String getLast_name() {return last_name;}
    public void setLast_name(String last_name) {this.last_name = last_name;}

    public String getEmail_id() {return email_id;}
    public void setEmail_id(String email_id) {this.email_id = email_id;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public int getIsAdmin() {return isAdmin;}
    public void setIsAdmin(int isAdmin) {this.isAdmin = isAdmin;}

    public int getIsBanned() {return isBanned;}
    public void setIsBanned(int isBanned) {this.isBanned = isBanned;}

    @Override @NonNull
    public String toString() {
        return email_id;
    }

/*
    not sure whats redundant here
    implement this later, add the getters as well

    private ArrayList<Recipe> recipes;
    private ArrayList<User> followedUsers;
*/

}
