package com.example.abalone.play.Control;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.abalone.play.GameActivity;
import com.example.abalone.play.Logic.AI;
import com.example.abalone.play.Logic.Board;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Stone;

import java.util.ArrayList;

public class Control {
    private Stone currentStone;
    private static Control single_Instance = null;
    private int deadBlue, deadRed;

    public static User selectedUser;

    public static Drawable current;

    public GameActivity gameUI;

    private AI ai;
    private Board board = null;

    public static Control getInstance() {
        if (single_Instance == null)
            single_Instance = new Control();
        return single_Instance;
    }

    public static Control createInstance(int num, GameActivity activity) {
        single_Instance = new Control(num, activity);
        return single_Instance;
    }


    public static void destroy() {
        single_Instance = null;
    }

    private Control() {
        currentStone = null;
        if (board == null)
            board = new Board(true, 1);
        deadBlue = board.deadBlue;
        deadRed = board.deadRed;
        ai = AI.getInstance(board.getPlayer() * -1);
    }

    private Control(int num, GameActivity activity) {
        currentStone = null;
        if (board == null)
            board = new Board(true, num);
        deadBlue = board.deadBlue;
        deadRed = board.deadRed;
        ai = AI.getInstance(board.getPlayer() * -1);
        this.gameUI = activity;
    }

    public Board setUpBoard(int num) {
        board = new Board(true, num);
        return board;
    }

    public Stone getCurrentStone() {
        return currentStone;
    }


    public boolean setCurrentStone(Stone currentStone) {
        this.currentStone = currentStone;
        return onChangeStone();
    }

    private boolean onChangeStone() {
        return board.makeMove(this.currentStone);
    }

    public boolean AIMoveMaybe(boolean AiTurn) {
        return board.CheckAndMoveAI(AiTurn);
    }

    public int getDeadBlue() {
        return this.deadBlue;
    }

    public int getDeadRed() {
        return this.deadRed;
    }

    public Board getBoard() {
        return this.board;
    }

    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            AI.getInstance(board.getPlayer() * -1);
        }
        else {
            AI.removeInstance();
        }
    }

    public static boolean hasInstance() {
        return single_Instance != null;
    }

    public boolean hasAiInstance() {
        return AI.hasInstance();
    }

    public static void setSelectedUser(User user) {
        selectedUser = user;
    }

    public static User getSelectedUser() {
        if (selectedUser != null)
            return selectedUser;
        return null;
    }

    public static void setCurrentImageView(Drawable imageView) {
        current = imageView;
    }

    public static Drawable getCurrentImageView() {
        return current;
    }

    public void makeInvisible(ArrayList<Stone> selected, ArrayList<Stone> toBe) {
        gameUI.makeTroopsInvisible(selected, toBe);
    }

    public void makeInvisible(ArrayList<Stone> selected) {
        gameUI.makeTroopsInvisible(selected, null);
    }

    public void buildCustomBoard(int[][] hexArray, int player) {
        board.organize(hexArray);
        board.setPlayer(player);
    }

    public int[][] getBoardAsInt() {
        return board.turnStoneToIntArr();
    }

}
