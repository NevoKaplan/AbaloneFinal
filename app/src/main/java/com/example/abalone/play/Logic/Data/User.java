package com.example.abalone.play.Logic.Data;

import android.graphics.Bitmap;

public class User {

    static private int counter = 0; // static counter to count the number of user instances
    private long id; // unique id of the user
    private String name; // user's name
    private String surname; // user's surname
    private String email; // user's email
    private Bitmap img; // user's profile picture
    private int wins; // user's number of wins

    /**
     * Constructor for creating a new user instance.
     *
     * @param name    the name of the user
     * @param surname the surname of the user
     * @param email   the email of the user
     * @param bitmap  the profile picture of the user
     */
    public User(String name, String surname, String email, Bitmap bitmap) {
        counter++;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.img = bitmap;
        this.id = counter;
        this.wins = 0;
    }

    /**
     * Constructor for creating a user instance with specified id and number of wins.
     *
     * @param name    the name of the user
     * @param surname the surname of the user
     * @param email   the email of the user
     * @param bitmap  the profile picture of the user
     * @param win     the number of wins the user has
     * @param id      the unique id of the user
     */
    public User(String name, String surname, String email, Bitmap bitmap, int win, long id) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.img = bitmap;
        this.id = id;
        this.wins = win;
    }

    /**
     * Static method for getting the number of user instances created.
     *
     * @return the number of user instances created
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * Getter for the unique id of the user.
     *
     * @return the unique id of the user
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for the unique id of the user.
     *
     * @param id the unique id of the user
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for the name of the user.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the user.
     *
     * @param name the name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the surname of the user.
     *
     * @return the surname of the user
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter for the surname of the user.
     *
     * @param surname the surname of the user
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Getter for the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the email of the user.
     *
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    /**
     * This function is used to return how many times the
     * user has won.
     * @return number of user wins.
     */
    public int getWins() {
        return this.wins;
    }

    public void setWins(int _wins) {
        this.wins = _wins;
    }
}
