package com.cookbook.model;

public class Notification {

    // Fields representing attributes of a notification
    private String id;//notification's id
    private String username; //username of user who gave the notification to logged in user
    private String type;
    private String from_user_id;
    private String to_user_id;
    private String post_id;
    private String created_at;
    private String text;//isn't stored in db we change the value depending on @type


    // Constructor for creating a Notification object
    public Notification(String id, String type, String username, String from_user_id, String to_user_id, String post_id, String created_at) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.from_user_id = from_user_id;
        this.to_user_id = to_user_id;
        this.post_id = post_id;
        this.created_at = created_at;

        if (this.type.equals("follow")) {
            this.text = "is following you";
        } else if (type.equals("like")) {
            this.text = "liked your post";
        } else {
            this.text = "commented on your post";
        }

    }

    // Copy constructor for creating a Notification object based on an existing notification
    public Notification(Notification notification) {
        this.id = notification.getId();
        this.username = notification.getUsername();
        this.type = notification.getType();
        this.from_user_id = notification.getFrom_user_id();
        this.to_user_id = notification.getTo_user_id();
        this.post_id = notification.getPost_id();
        this.created_at = notification.getCreated_at();
        if (this.type.equals("follow")) {
            this.text = "is following you";
        } else if (type.equals("like")) {
            this.text = "liked your post";
        } else {
            this.text = "commented on your post";
        }
    }


    // Getter method to retrieve the username associated with the notification
    public String getUsername() {
        return username;
    }

    // Setter method to set the username associated with the notification
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter method to retrieve the notification ID
    public String getId() {
        return id;
    }

    // Setter method to set the notification ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter method to retrieve the type of the notification
    public String getType() {
        return type;
    }



    // Getter method to retrieve the ID of the user who sent the notification
    public String getFrom_user_id() {
        return from_user_id;
    }



    // Getter method to retrieve the ID of the user who received the notification
    public String getTo_user_id() {
        return to_user_id;
    }



    // Getter method to retrieve the text of the notification
    public String getText() {
        return text;
    }

    // Setter method to set the text of the notification
    public void setText(String text) {
        this.text = text;
    }

    // Getter method to retrieve the ID of the post associated with the notification
    public String getPost_id() {
        return post_id;
    }



    // Getter method to retrieve the timestamp indicating when the notification was created
    public String getCreated_at() {
        return created_at;
    }


}
