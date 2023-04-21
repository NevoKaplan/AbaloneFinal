// Define package and import statements for the AI class.
package com.example.abalone.play.Logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Define the AI class.
public class AI {
    // Declare instance variables for the AI player and instance.
    private int AiPlayer;
    private static AI instance = null;

    // Define a setter for the AI player.
    public void setAiPlayer(int aiPlayer) {
        this.AiPlayer = aiPlayer;
    }

    // Define a getter for the AI player.
    public int getAiPlayer() {
        return this.AiPlayer;
    }

    // Define a constructor for the AI class.
    private AI(int player) {
        AiPlayer = player;
    }

    // Define a static method to get the instance of the AI class.
    public static AI getInstance(int player) {
        if (instance == null) {
            instance = new AI(player);
        }
        return instance;
    }

    // Define a static method to check if an instance of the AI class exists.
    public static boolean hasInstance() {
        return instance != null;
    }

    // Define a static method to remove the instance of the AI class.
    public static void removeInstance() {
        instance = null;
    }

    // Define a method to get the best move for the AI player.
    public AIBoard bestMove(Board board1) {
        // Declare variables for the best move and a copy of the board.
        AIBoard move = null;
        AIBoard board = new AIBoard(board1);
        board.player = AiPlayer;

        // Print the player of the original board.
        System.out.println("Original: " + board.getPlayer());

        // Get a list of child boards for the current board and connect them.
        List<AIBoard> children = connectListsWithoutEval(board.getNextBoards(AiPlayer));

        // Evaluate each child board using the minimax algorithm.
        for (AIBoard child : children) {
            child.setVal(minimax(child, child.getPlayer() * -1, child.getDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        }

        // Choose the best move for the AI player based on the child boards.
        if (board.getPlayer() == 1) {
            int highestValue = Integer.MIN_VALUE;
            for (AIBoard child : children) {
                int val = child.getVal();
                if (val >= highestValue) {
                    highestValue = val;
                    move = child;
                }
            }
            board.setVal(highestValue);
        } else {
            int lowestValue = Integer.MAX_VALUE;
            for (AIBoard child : children) {
                int val = child.getVal();
                if (val <= lowestValue) {
                    lowestValue = val;
                    move = child;
                }
            }
            board.setVal(lowestValue);
        }

        // Print the final sum of the best move.
        System.out.println("final sum: " + move.getVal());

        // Return the best move.
        return move;
    }

    // This method implements the minimax algorithm with alpha-beta pruning to determine the best move for the current player.
// It takes as input the current game state 'board', the 'currentPlayer', the 'depth' of the search, and the 'alpha' and 'beta' values for pruning.
    public int minimax(AIBoard board, int currentPlayer, int depth, int alpha, int beta) {
        int val;
// Check if the game is over
        int winner = board.getWinner();
        if (winner != 0)
            return Integer.MAX_VALUE * winner;
// Check if the search depth has been reached
        if (depth == 0) {
            val = board.evaluate(board.getAmountOfStonesMoved());
            board.setVal(val);
            return val;
        }
        // Generate the next possible game states and sort them by their evaluation score
        ArrayList<AIBoard> nextStates = connectLists(board.getNextBoards(currentPlayer));
        // Apply the minimax algorithm with alpha-beta pruning
        int bestScore;
        if (currentPlayer == 1) {
            bestScore = Integer.MIN_VALUE;
            for (AIBoard nextState : nextStates) {
                int score = minimax(nextState, -currentPlayer, depth - 1, alpha, beta);
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (AIBoard nextState : nextStates) {
                int score = minimax(nextState, -currentPlayer, depth - 1, alpha, beta);
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return bestScore;
    }

    // This method takes an array of lists of possible game states and connects them into a single list, sorted by their evaluation score.
// It returns the top 34 states with the highest score.
    private ArrayList<AIBoard> connectLists(ArrayList<AIBoard>[] nextBoards) {
        ArrayList<AIBoard> fina = new ArrayList<>();
// Add the game states to the 'fina' list and evaluate them
        for (int i = nextBoards.length - 1; i >= 0; i--) {
            for (AIBoard board : nextBoards[i]) {
                board.setVal(board.evaluate(board.getAmountOfStonesMoved()));
                fina.add(board);
            }
        }
        // Sort the 'fina' ArrayList by 'Val'
        fina.sort((b1, b2) -> Integer.compare(b2.getVal(), b1.getVal()));
        Collections.reverse(fina);
        System.out.println("NEW ---------------------------------");
        for (AIBoard ai : fina) {
            System.out.println(ai.getVal());
        }
// Keep only the top 34 with the highest 'Val'
        if (fina.size() > 34)
            fina.subList(35, fina.size()).clear();

        System.out.println("Top here: " + fina.get(0).getVal());
        return fina;
    }

    // This method takes an array of lists of possible game states and connects them into a single list.
// It is used when the evaluation score of the game states is not needed.
    private ArrayList<AIBoard> connectListsWithoutEval(ArrayList<AIBoard>[] nextBoards) {
        ArrayList<AIBoard> fina = new ArrayList<>();
// Add the game states to the 'fina' list
        for (int i = nextBoards.length - 1; i >= 0; i--)
            fina.addAll(nextBoards[i]);
        return fina;
    }
}