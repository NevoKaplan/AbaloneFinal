// This is a Control class that manages the game logic for Abalone game
package com.example.abalone.play.Control;

import android.graphics.Bitmap;

import com.example.abalone.play.GameActivity;
import com.example.abalone.play.Logic.AI;
import com.example.abalone.play.Logic.Board;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Data.UserTable;
import com.example.abalone.play.Logic.Stone;

import java.util.ArrayList;

public class Control {
    private Stone currentStone;
    private static Control single_Instance = null;
    private int deadBlue, deadRed;

    // The two users that are currently selected to play the game
    public static User selectedUser1 = null;
    public static User selectedUser2 = null;

    // The current image bitmap that represents the board
    public static Bitmap current = null;

    // The GameActivity UI that displays the game
    public GameActivity gameUI;

    // The game board
    private Board board = null;

    // Private constructor used for the singleton design pattern
    private Control() {
        currentStone = null;
        if (board == null)
            board = new Board(true, 1);
        deadBlue = board.deadBlue;
        deadRed = board.deadRed;
        AI.getInstance(board.getPlayer() * -1);
    }

    // Constructor used for creating a new instance of Control
    private Control(int num, GameActivity activity) {
        currentStone = null;
        if (board == null)
            board = new Board(true, num);
        deadBlue = board.deadBlue;
        deadRed = board.deadRed;
        AI.getInstance(board.getPlayer() * -1);
        this.gameUI = activity;
    }

    // Method used for obtaining the instance of Control
    public static Control getInstance() {
        if (single_Instance == null)
            single_Instance = new Control();
        return single_Instance;
    }

    // Method used for creating a new instance of Control
    public static Control createInstance(int num, GameActivity activity) {
        single_Instance = new Control(num, activity);
        return single_Instance;
    }

    // Method used for setting up a new game board
    public Board setUpBoard(int num) {
        board = new Board(true, num);
        return board;
    }

    // Getter method for the current stone
    public Stone getCurrentStone() {
        return currentStone;
    }

    // Getter method for the GameActivity UI
    public GameActivity getGameUI() {
        return this.gameUI;
    }

    // Setter method for the current stone
    public boolean setCurrentStone(Stone currentStone) {
        this.currentStone = currentStone;
        return onChangeStone();
    }

    // Private helper method for when the current stone changes
    private boolean onChangeStone() {
        return board.makeMove(this.currentStone);
    }

    // Method for checking if AI should move
    public boolean AIMoveMaybe(int AiTurn) {
        return board.CheckAndMoveAI(AiTurn);
    }

    // Getter method for the number of blue stones that have been eliminated
    public int getDeadBlue() {
        return this.deadBlue;
    }

    // Getter method for the number of red stones that have been eliminated
    public int getDeadRed() {
        return this.deadRed;
    }

    // Getter method for the game board
    public Board getBoard() {
        return this.board;
    }

    /**
     * Returns whether a single instance of this class exists.
     */
    public static boolean hasInstance() {
        return single_Instance != null;
    }

    /**
     * Returns whether an instance of the AI class exists.
     */
    public boolean hasAiInstance() {
        return AI.hasInstance();
    }

    /**
     * Sets the selected user for player 1.
     * @param user The user to set as selected for player 1.
     */
    public static void setSelectedUser1(User user) {
        selectedUser1 = user;
    }

    /**
     * Returns the selected user for player 1.
     * @return The selected user for player 1.
     */
    public static User getSelectedUser1() {
        return selectedUser1;
    }

    /**
     * Sets the selected user for player 2.
     * @param user The user to set as selected for player 2.
     */
    public static void setSelectedUser2(User user) {
        selectedUser2 = user;
    }

    /**
     * Returns the selected user for player 2.
     * @return The selected user for player 2.
     */
    public static User getSelectedUser2() {
        return selectedUser2;
    }

    /**
     * Sets the current bitmap.
     * @param bitmap The bitmap to set as current.
     */
    public static void setCurrentBitmap(Bitmap bitmap) {
        current = bitmap;
    }

    /**
     * Returns the current bitmap.
     * @return The current bitmap.
     */
    public static Bitmap getCurrentBitmap() {
        return current;
    }

    /**
     * Makes the selected stones invisible.
     * @param selected The list of stones to make invisible.
     * @param toBe The list of stones to replace the selected stones with.
     */
    public void makeInvisible(ArrayList<Stone> selected, ArrayList<Stone> toBe) {
        gameUI.makeTroopsInvisible(selected, toBe);
    }

    /**
     * Makes the selected stones invisible.
     * @param selected The list of stones to make invisible.
     */
    public void makeInvisible(ArrayList<Stone> selected) {
        gameUI.makeTroopsInvisible(selected, null);
    }

    /**
     * Builds a custom board.
     * @param hexArray The array of integers representing the hexes on the board.
     * @param player The player number to set as the current player.
     */
    public void buildCustomBoard(int[][] hexArray, int player) {
        board.organize(hexArray);
        board.setPlayer(player);
    }

    /**
     * Returns the current board state as an integer array.
     * @return The current board state as an integer array.
     */
    public int[][] getBoardAsInt() {
        return board.turnStoneToIntArr();
    }

    /**
     * Updates the selected user's win count and adds a point to their score.
     * @param player The player number to update the user and score for.
     */
    public void updateUserAndAddPoint(int player) {
        UserTable userTable = new UserTable(gameUI);
        if (player == 1) {
            selectedUser1.setWins(selectedUser1.getWins() + 1);
            userTable.updateByRow(selectedUser1);
        }
        else {
            selectedUser2.setWins(selectedUser2.getWins() + 1);
            userTable.updateByRow(selectedUser2);
        }
    }


}
