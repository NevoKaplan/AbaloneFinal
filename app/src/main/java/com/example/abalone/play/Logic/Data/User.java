package com.example.abalone.play.Logic.Data;

import android.graphics.Bitmap;

public class User {

    static private int counter = 0;
    private long id;
    private String name;
    private String surname;
    private String email;
    private Bitmap img;

    public User(String name, String surname, String email, Bitmap bitmap) {
        counter++;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.img = bitmap;
        this.id = counter;
    }

    public User(String name, String surname, String email, Bitmap bitmap, long id) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.img = bitmap;
        this.id = id;
    }

    public static int getCounter() {
        return counter;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
