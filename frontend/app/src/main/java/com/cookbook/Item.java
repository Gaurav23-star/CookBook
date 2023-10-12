package com.cookbook;

public class Item {
    String name;
    String author;
    int image;
    String time;
    String ing1;
    String ing2;
    int admin;
    public Item(String name, String author, int image, String time, String ing1, String ing2) {
        this.name = name;
        this.author = author;
        this.image = image;
        this.time = time;
        this.ing1= ing1;
        this.ing2=ing2;
        this.admin=R.drawable.ic_baseline_settings_24;
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
}
