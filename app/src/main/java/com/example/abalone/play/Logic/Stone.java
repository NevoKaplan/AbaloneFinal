package com.example.abalone.play.Logic;

import java.util.ArrayList;
import java.util.Collections;

public class Stone {

    // Instance variables
    private int mainNum; // the main number of the stone
    public int row, col; // the row and column of the stone
    public boolean isSelected; // whether the stone is currently selected by the user
    private int ogNum; // the original number of the stone

    // Constructor with parameters
    public Stone(int mainNum, int row, int col) {
        this.mainNum = mainNum;
        this.row = row;
        this.col = col;
        this.isSelected = false;
        this.ogNum = mainNum;
    }

    // Copy constructor
    public Stone(Stone stone) {
        this.mainNum = stone.mainNum;
        this.row = stone.row;
        this.col = stone.col;
        this.isSelected = false;
        this.ogNum = this.mainNum;
    }

    // Getter for mainNum
    public int getMainNum() {
        return this.mainNum;
    }

    // Setter for mainNum
    public void setMainNum(int num) {
        this.mainNum = num;
    }

    // Getter for ogNum
    public int getOgNum() {
        return this.ogNum;
    }

    // Setter for ogNum
    public void setOgNum(int num) {
        this.ogNum = num;
    }

    // Returns a string representation of the stone
    public String toString() {
        return this.mainNum + " [" + this.row + "]" + "[" + this.col + "]";
    }

    // Getter for isSelected
    public boolean getSelected() {
        return this.isSelected;
    }

    // Setter for isSelected
    public void setSelected(boolean bool) {
        this.isSelected = bool;
    }

    // Checks if this stone is before the parameter stone
    public boolean isBefore(Stone stone) {
        if (this.row < stone.row)
            return false;
        if (this.row == stone.row)
            return this.col >= stone.col;
        return true;
    }

    // Checks if this stone is equal to the parameter stone
    public boolean equals(Stone stone) {
        return stone.col == this.col && stone.row == this.row;
    }

    // Sorts the given ArrayList of stones using bubble sort
    public static void sort(ArrayList<Stone> stones) {
        int size = stones.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                Stone current = stones.get(j);
                Stone next = stones.get(j+1);
                if (current.equals(next)) {
                    stones.remove(j+1);
                    size--;
                }
                else if (current.isBefore(next)) {
                    Stone stone = stones.get(j);
                    stones.set(j, stones.get(j + 1));
                    stones.set(j+1, stone);
                }
            }
        }
    }

    // Reverses the order of the given ArrayList of stones
    public static void reverseList(ArrayList<Stone> stones) {
        Collections.reverse(stones);
    }

    // Returns an array with the row and column of the stone
    public int[] getPosition() {
        return new int[]{this.row, this.col};
    }

    public void setBothNums(int num) {
        this.mainNum = num;
        this.ogNum = num;
    }

}