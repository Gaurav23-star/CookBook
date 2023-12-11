package com.cookbook.model;

public class Notification {

    private String id;//notification's id
    private String username; //username of user who gave the notification to logged in user
    private String type;
    private String from_user_id;
    private String to_user_id;
    private String post_id;
    private String created_at;
    private String text;//isn't stored in db we change the value depending on @type


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

    public Notification() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
