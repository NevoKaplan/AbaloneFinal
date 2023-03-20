package com.example.abalone.play.Logic.Data;

import android.graphics.Bitmap;

public class User {

    static private int counter = 0;
    private long id;
    private String name;
    private String surname;
    private String email;
    private Bitmap img;
    private int wins;

    public User(String name, String surname, String email, Bitmap bitmap) {
        counter++;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.img = bitmap;
        this.id = counter;
        this.wins = 0;
    }

    public User(String name, String surname, String email, Bitmap bitmap, int win, long id) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.img = bitmap;
        this.id = id;
        this.wins = win;
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

    public int getWins() {
        return this.wins;
    }

    public void setWins(int _wins) {
        this.wins = _wins;
    }
}
