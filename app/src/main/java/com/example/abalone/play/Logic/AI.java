package com.example.abalone.play.Logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AI {
    private int AiPlayer;
    private static AI instance = null;


    public void setAiPlayer(int aiPlayer) {
        this.AiPlayer = aiPlayer;
    }

    public int getAiPlayer() {
        return this.AiPlayer;
    }

    private AI (int player) {
        AiPlayer = player;
    }

    public static AI getInstance(int player) {
        if (instance == null) {
            instance = new AI(player);
        }
        return instance;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static void removeInstance() {
        instance = null;
    }

    public AIBoard bestMove(Board board1) {
        AIBoard move = null;
        AIBoard board = new AIBoard(board1);
        board.player = AiPlayer;
        System.out.println("Original: " + board.getPlayer());
        List<AIBoard> children = connectListsWithoutEval(board.getNextBoards(AiPlayer));
        for (AIBoard child : children) {
            child.setVal(minimax(child, child.getPlayer() * -1, child.getDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        }

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
        }
        else {
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
        System.out.println("final sum: " + move.getVal());
        return move;
    }

    public int minimax(AIBoard board, int currentPlayer, int depth, int alpha, int beta) {
        int val;
        int winner = board.getWinner();
        if (winner != 0)
            return Integer.MAX_VALUE * winner;
        if (depth == 0) {
            val = board.evaluate(board.getAmountOfStonesMoved());
            board.setVal(val);
            return val;
        }

        ArrayList<AIBoard> nextStates = connectLists(board.getNextBoards(currentPlayer));
        if (currentPlayer == 1) {
            for (AIBoard nextState : nextStates) {
                alpha = Math.max(alpha, minimax(nextState,currentPlayer*-1, depth - 1, alpha, beta));
                if (beta < alpha) {
                    break;
                }
            }
            return alpha;
        } else {
            for (AIBoard nextState : nextStates) {
                beta = Math.min(beta, minimax(nextState, currentPlayer*-1, depth - 1, alpha, beta));
                if (beta < alpha) {
                    break;
                }
            }
            return beta;
        }
    }


    // firsts will be the triples, then the doubles and then the singles
    private ArrayList<AIBoard> connectLists(ArrayList<AIBoard>[] nextBoards) {
        ArrayList<AIBoard> fina = new ArrayList<>();
        for (int i = nextBoards.length-1; i >= 0; i--) {
            for (AIBoard board : nextBoards[i]) {
                board.setVal(board.evaluate(board.getAmountOfStonesMoved()));
                fina.add(board);
            }
        }

        // Sort the 'fina' ArrayList by 'Val'
        fina.sort((b1, b2) -> Integer.compare(b2.getVal(), b1.getVal()));

        // Keep only the top 24  with the highest 'Val'
        if (fina.size() > 34)
            fina.subList(0, 34).clear();

        return fina;
    }

    private ArrayList<AIBoard> connectListsWithoutEval(ArrayList<AIBoard>[] nextBoards) {
        ArrayList<AIBoard> fina = new ArrayList<>();
        for (int i = nextBoards.length-1; i >= 0; i--)
            fina.addAll(nextBoards[i]);
        return fina;
    }

    /*private double checkBoard(AIBoard board, int depth, double alpha, double beta) {
        double val;
        int winner = board.getWinner();
        if (winner != 0) {
            return Double.MAX_VALUE * winner;
        }
        if (depth == 0) {
            val = board.evaluate(AiPlayer, 0);
            board.setVal(val);
            return val;
        }

        ArrayList<AIBoard> nextBoards = connectLists(board.getNextBoards());

        // checking whose turn it is
        if (board.player == AiPlayer) {
            for (AIBoard child : nextBoards) {
                alpha = Math.max(alpha, checkBoard(child, depth - 1, alpha, beta));
                if (beta < alpha)
                    break;
            }
            return alpha;
        } else
        {
            for (AIBoard child : nextBoards) {
                beta = Math.min(beta, checkBoard(child, depth - 1, alpha, beta));
                if (beta < alpha)
                    break;
            }
            return beta;
        }
    }*/

    public AIBoard getMove(Board board) {
        AIBoard aiBoard = new AIBoard(board);
        double bestValue = minimax(aiBoard, 1, AIBoard.MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("bestValue: " + bestValue);
        aiBoard.resetAlreadyBoards();
        ArrayList<AIBoard>[] nextStates = aiBoard.getNextBoards(aiBoard.getPlayer()); // generate the next possible states
        for (int i = nextStates.length-1; i >= 0; i--) {
            for (AIBoard fina : nextStates[i]) {
                fina.print();
                double val = fina.evaluate(fina.getAmountOfStonesMoved());
                System.out.println("Val: " + val);
                if (val == bestValue)
                    return fina;
            }
        }

        System.out.println("ERROR,  Something went wrong");
        return null;
    }

}
