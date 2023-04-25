package com.example.abalone.play.Logic;

import java.util.ArrayList;
import java.util.Iterator;

// AIBoard class that extends Board class
public class AIBoard extends Board {

    // A static integer constant MAX_DEPTH with a value of 3
    final static int MAX_DEPTH = 3;
    // Instance variables
    int depth;
    private int val;
    private int preDeadRed, preDeadBlue;
    private ArrayList<Stone>[] madeMove = new ArrayList[2];
    private final ArrayList<Stone[][]> alreadyBoards;
    private int amountOfStonesMoved;

    // A 2D integer array fromMiddle with predefined values
    private static final int[][] fromMiddle =
            {{0, 0, 0, 0, 0  , 0, 0, 0, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 1, 2, 2, 2, 2, 2, 1, 0},
                    {0, 1, 2, 3, 3, 3, 2, 1, 0},
                    {0, 1, 2, 3, 4, 3, 2, 1, 0},
                    {0, 1, 2, 3, 3, 3, 2, 1, 0},
                    {0, 1, 2, 2, 2, 2, 2, 1, 0},
                    {0, 1, 1, 1, 1, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}};

    // Constructor that takes a Board object
    public AIBoard(Board board) {
        // Calls super constructor of Board class
        super(false, -1);
        // Updates the hex board
        updateBoard(board.hex);
        // Sets player attribute
        this.player = board.player;
        // Sets depth attribute
        this.depth = MAX_DEPTH;
        // Initializes madeMove list of arrays
        for (int i = 0; i < madeMove.length; i++) {
            madeMove[i] = new ArrayList<>();
        }
        // Initializes alreadyBoards list
        alreadyBoards = new ArrayList<>();
        // Sets preDeadRed attribute
        preDeadBlue = board.deadBlue;
        // Sets preDeadBlue attribute
        preDeadRed = board.deadRed;
        // Sets deadBlue attribute
        deadBlue = board.deadBlue;
        // Sets deadRed attribute
        deadRed = board.deadRed;
        // Sets AiTurn attribute to true
        this.AiTurn = true;
    }

    public AIBoard(AIBoard board) {  // constructor that takes an instance of AIBoard class as a parameter
        super(false, -1);  // calls the constructor of the superclass and passes two parameters to it
        updateBoard(board.hex);  // calls the method "updateBoard" with the "hex" field of the parameter object as an argument
        this.player = board.player;  // assigns the value of the "player" field of the parameter object to the "player" field of the current object
        this.depth = board.depth - 1;  // assigns the value of the "depth" field of the parameter object decremented by 1 to the "depth" field of the current object
        this.val = 0;  // assigns the value 0 to the "val" field of the current object
        for (int i = 0; i < madeMove.length; i++)  // initializes each element of the "madeMove" array of the current object with a new ArrayList object
            madeMove[i] = new ArrayList<>();
        preDeadBlue = board.preDeadBlue;  // assigns the value of the "preDeadBlue" field of the parameter object to the "preDeadBlue" field of the current object
        preDeadRed = board.preDeadRed;  // assigns the value of the "preDeadRed" field of the parameter object to the "preDeadRed" field of the current object
        deadBlue = board.deadBlue;  // assigns the value of the "deadBlue" field of the parameter object to the "deadBlue" field of the current object
        deadRed = board.deadRed;  // assigns the value of the "deadRed" field of the parameter object to the "deadRed" field of the current object
        alreadyBoards = new ArrayList<>();  // creates a new ArrayList object and assigns it to the "alreadyBoards" field of the current object
        this.AiTurn = true;  // assigns the value true to the "AiTurn" field of the current object
    }

    // Update the board with stones
    private void updateBoard(Stone[][] stones) {
        for (int i = 0; i < this.hex.length; i++) {
            for (int j = 0; j < this.hex[i].length; j++) {
                // Set the main and original numbers of each hex to those of the corresponding stone
                this.hex[i][j].setMainNum(stones[i][j].getMainNum());
                this.hex[i][j].setOgNum(stones[i][j].getOgNum());
            }
        }
    }

    // Return the depth of this board
    public int getDepth() {
        return this.depth;
    }

    // Return the value of this board
    public int getVal() {
        return this.val;
    }

    // Set the value of this board
    public void setVal(int val) {
        this.val = val;
    }

    // Get the made move
    public ArrayList<Stone>[] getMadeMove() {
        return this.madeMove;
    }

    // Set the best selected stones
    public void setBestSelected(ArrayList<Stone> selected, ArrayList<Stone> toBe) {
        // Add each stone in the selected list to the first arraylist of madeMove
        for (Stone stone : selected) {
            this.madeMove[0].add(stone);
        }
        // Add each stone in the toBe list to the second arraylist of madeMove
        for (Stone stone : toBe) {
            this.madeMove[1].add(stone);
        }
    }

    // Overload the setBestSelected method
    public void setBestSelected() {
        // Add each stone in the selected list to the first arraylist of madeMove
        for (Stone stone : this.selected) {
            this.madeMove[0].add(stone);
        }
        // Add each stone in the toBe list to the second arraylist of madeMove
        for (Stone stone : this.toBe) {
            this.madeMove[1].add(stone);
        }
    }

    // Get the next possible boards for the current player
    public ArrayList<AIBoard>[] getNextBoards(int currentPlayer) {
        // Set the current player
        this.player = currentPlayer;
        // Return an array of three arraylists of AIBoard objects generated by iterating the next moves
        return this.IterateNextBoards();
    }

    // Check if the board does not already exist in the alreadyBoards list
    private boolean checkHelper(Stone[][] other) {
        for (Stone[][] og : alreadyBoards) {
            if (hexEquals(og, other))
                return false;
        }
        return true;
    }

    // Check if the board does not already exist in the alreadyBoards list
    private boolean checkNotIfExists(Stone[][] board) {
        // If the board does not exist in the alreadyBoards list, add it to the list and return true
        if (checkHelper(board)) {
            alreadyBoards.add(board);
            return true;
        }
        return false;
    }

    // Check if two boards are the same
    private boolean hexEquals(Stone[][] og, Stone[][] other) {
        // Iterate through each hex in the boards and check if they have the same main numbers
        for (int i = 0; i < og.length; i++) {
            for (int j = 0; j < og[0].length; j++) {
                if (og[i][j].getMainNum() != other[i][j].getMainNum())
                    return false;
            }
        }
        return true;
    }

    private ArrayList<AIBoard>[] IterateNextBoards() {
        // Initialize an array of 3 ArrayLists
        ArrayList<AIBoard>[] nextBoards = new ArrayList[3];
        // Fill the array with new ArrayLists
        for (int i = 0; i < nextBoards.length; i++)
            nextBoards[i] = new ArrayList<>();
        // count for how many troops already checked - to check less
        int count = 0;
        // Increase the count by the number of dead stones for the current player
        if (player == 1)
            count += this.deadBlue;
        else
            count += this.deadRed;
        // Iterate through all hex cells until the maximum number of checked stones is reached
        for (int i = 0; i < this.hex.length && count <= 14; i++) {
            for (int j = 0; j < this.hex[i].length; j++) {
                // Check if the current cell belongs to the current player
                if (this.hex[i][j].getMainNum() == player) {
                    count++;
                    // Find all possible moves for a single stone
                    nextBoards[0].addAll(findMoveSingle(hex[i][j]));
                    // Find all possible moves for two adjacent stones
                    ArrayList<Stone> availableDouble = availableStonesForSingle(hex[i][j]);
                    ArrayList<Stone> tempSelected;
                    for (Stone availlable : availableDouble) {
                        tempSelected = new ArrayList<>();
                        tempSelected.add(hex[i][j]);
                        tempSelected.add(availlable);
                        // Sort the selected stones to ensure that they are in a specific order
                        Stone.sort(tempSelected);
                        // Find all possible moves for two adjacent stones
                        nextBoards[1].addAll(findMoveMultiple(tempSelected, 2));
                        // Find all possible moves for three adjacent stones
                        ArrayList<Stone> availableTriple = availableStonesForDouble(tempSelected);
                        for (Stone available2 : availableTriple) {
                            tempSelected = new ArrayList<>();
                            tempSelected.add(hex[i][j]);
                            tempSelected.add(availlable);
                            tempSelected.add(available2);
                            // Sort the selected stones to ensure that they are in a specific order
                            Stone.sort(tempSelected);
                            // Find all possible moves for three adjacent stones
                            nextBoards[2].addAll(findMoveMultiple(tempSelected, 3));
                        }
                    }
                }
            }
        }
        // Return the array of ArrayLists that contains all the possible moves
        return nextBoards;
    }

    private ArrayList<AIBoard> findMoveSingle(Stone start) {
        ArrayList<AIBoard> boards = new ArrayList<>();
        // Find all possible targets for the single stone
        ArrayList<Stone> targets = availableTargetsForSingle(start);
        // Iterate through all possible targets and add a new board with the move to the array of boards
        for (Stone target : targets) {
            AIBoard aiBoard = new AIBoard(this);
            aiBoard.amountOfStonesMoved = 1;
            aiBoard.changeSelected(aiBoard.hex[start.row][start.col]);
            for (Stone s : aiBoard.selected)
                aiBoard.madeMove[0].add(new Stone(s));
            aiBoard.doMoveSingle(aiBoard.hex[target.row][target.col]);
            aiBoard.cleanSelected();
            if (checkNotIfExists(aiBoard.hex))
                boards.add(aiBoard);
        }
        // Return the array of boards that contains all the possible moves
        return boards;
    }

    /**

     This method finds all possible moves given a starting position and amount of stones to move.

     @param starts the starting positions

     @param amount the amount of stones to move

     @return an ArrayList of AIBoards representing all possible moves
     */
    private ArrayList<AIBoard> findMoveMultiple(ArrayList<Stone> starts, int amount) {

        ArrayList<AIBoard> boards = new ArrayList<>();
        ArrayList<Stone> targets = availableTargetsMultiple(starts);

// loops through all available targets and checks if a move exists for each target
        for (Stone target : targets) {
            AIBoard aiBoard = new AIBoard(this);
            aiBoard.amountOfStonesMoved = amount;
            // selects all starting positions on the current board
            for (Stone start : starts) {
                aiBoard.changeSelected(aiBoard.hex[start.row][start.col]);
            }

            // adds each stone in the current move to the madeMove array list
            for (Stone s : aiBoard.selected) {
                aiBoard.madeMove[0].add(new Stone(s));
            }

            // performs the move and checks if it is a valid move
            aiBoard.doMoveMultiple(aiBoard.hex[target.row][target.col]);
            aiBoard.cleanSelected();
            if (checkNotIfExists(aiBoard.hex)) {
                boards.add(aiBoard);
            }
        }
        return boards;
    }

    /**

     This method performs a single move on the board.

     @param moveTo the Stone to move to
     */
    private void doMoveSingle(Stone moveTo) {
        moveTo.setMainNum(0);

// adds the moved stone to the toBe array list and adds all stones in the toBe list to the madeMove array list
        this.toBe.add(moveTo);
        for (Stone s : this.toBe) {
            this.madeMove[1].add(new Stone(s));
        }

        Stone last = selected.get(0);
        int drow = last.row - moveTo.row;
        int dcol = last.col - moveTo.col;

        beforeLineMove(drow, dcol);
    }

    /**

     This method performs a multiple move on the board.

     @param moveTo the Stone to move to
     */
    private void doMoveMultiple(Stone moveTo) {
        boolean reverse = this.shouldReverse(moveTo);

// adds the moved stone to the madeMove array list
        this.madeMove[1].add(new Stone(moveTo));

// checks if a side move exists
        if (beforeSideMove(moveTo))
            return;

        shouldReverse2(reverse);

        Stone last = selected.get(selectedSize - 1);
        int drow = moveTo.row - last.row;
        int dcol = moveTo.col - last.col;

// adds all stones in the toBe list to the madeMove array list and performs the line move
        this.madeMove[1] = new ArrayList<>();
        for (Stone s : this.toBe) {
            this.madeMove[1].add(new Stone(s));
        }
        this.beforeLineMove(drow, dcol);
    }

    /**

     This method returns the winner of the game.
     @return 1 if blue has won, -1 if red has won, and 0 if there is no winner yet
     */
    public int getWinner() {
        if (this.deadBlue >= 6) {
            return -1;
        }
        else if (this.deadRed >= 6) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public int evaluate(int amount) {
        // Commented out System.out.println statement
        // This method evaluates the current board and returns a score based on the current state of the board.

        // Initialize the sum variable to 1
        int sum = 1;

        // Loop through every element in the hex array
        for (int i = 0; i < hex.length; i++) {
            for (int j = 0; j < hex[i].length; j++) {
                // Check if the element is not equal to 4 and is equal to the current player
                if (hex[i][j].getMainNum() != 4 && hex[i][j].getMainNum() == player) {
                    // Add the fromMiddle value of the current element to the sum variable
                    sum += fromMiddle[i][j];
                }
            }
        }

        // Check the amount of stones that are left
        switch (amount) {
            // If there are 2 stones left, add 10 to the sum variable
            case 2:
                sum += 10;
                break;
            // If there is 1 stone left, add 5 to the sum variable
            case 1:
                sum += 5;
                break;
            default:
                break;
        }

        // Check the number of dead blue stones
        int deadB = deadBlue - preDeadBlue;
        if (deadB >= 1) {
            // Print a debug statement
            System.out.println("IT DOES WORK1");
            if (player == -1) {
                // Multiply the sum variable by 10 times the number of dead blue stones
                sum *= 12 * deadB;
            } else {
                // Divide the sum variable by 5 times the number of dead blue stones
                sum /= 5 * deadB;
            }
        }

        // Check the number of dead red stones
        int deadR = deadRed - preDeadRed;
        if (deadR >= 1) {
            // Print a debug statement
            System.out.println("IT DOES WORK2");
            if (player == 1) {
                // Multiply the sum variable by 10 times the number of dead red stones
                sum *= 12 * deadR;
            } else {
                // Divide the sum variable by 5 times the number of dead red stones
                sum /= 5 * deadR;
            }
        }

        // Multiply the sum variable by the current player
        return sum * player;
    }

    @Override
    protected void merge() {
        // Add all elements from the toBe ArrayList to the selected ArrayList
        selected.addAll(toBe);
        // Add the size of the toBe ArrayList to the selectedSize variable
        selectedSize += toBe.size();
    }

    protected ArrayList<Stone> availableTargetsForSingle(Stone temp) {
        // If code here, can only be 1 selected
        ArrayList<Stone> targets = new ArrayList<>();
        for (int[] var : dirArr) { // All directions list
            try {
                if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() == 0) { // Check if future cell is empty
                    targets.add(hex[temp.row + var[0]][temp.col + var[1]]);
                }
            } catch (IndexOutOfBoundsException ignored) { // Ignore if index is out of bounds
            } catch (Exception e) { // Print exception message if one occurs
                System.out.println(e.getMessage());
            }
        }
        return targets;
    }

    private ArrayList<Stone> availableTargetsMultiple(ArrayList<Stone> chosen) {
        Stone first = chosen.get(0);
        Stone second = chosen.get(1);
        int drow = first.row - second.row;
        int dcol = first.col - second.col;
        ArrayList<Stone> stones = new ArrayList<>();
        int size = chosen.size();
        // Gets the difference between the 2 stones

        targetLine(drow, dcol, first, 0, stones, size); // From first and on
        targetLine(-drow, -dcol, chosen.get(size-1), 0, stones, size); // From last and on

        Iterator<Stone> it;
        boolean flag, added; // Flag checks if stones can be moved to the side

        for (int[] var : dirArr) { // List of all directions
            it = chosen.iterator();
            flag = true;
            added = false;
            ArrayList<Stone> maybe = new ArrayList<>();
            if (!((var[0] == drow && var[1] == dcol) || (var[0] == -drow && var[1] == -dcol))) { // Don't check the already checked
                if (((first.col < first.col + var[1]) && (first.row + var[0] <= first.row)) || (drow == 0 && first.col <= first.col + var[1])) { // Change only if the current one is more to the left than the next one
                    Stone.reverseList(chosen);
                }
                while (it.hasNext() && flag) {
                    Stone temp = it.next();
                    try {
                        if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() != 0) { // If future doesn't equal to 0, can't be moved
                            flag = false;
                        } else {
                            if (!added) {
                                maybe.add(hex[temp.row + var[0]][temp.col + var[1]]);
                                added = true;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) { // Ignore if index is out of bounds
                        flag = false;
                    } catch (Exception e) { // Print exception message if one occurs
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                flag = false;
            }
            if (flag) { // If all stones can be moved then add the maybe list
                stones.addAll(maybe);
            }
        }
        Stone.sort(stones);
        return stones;
    }

    // This method returns the available stones for a single stone move
    protected ArrayList<Stone> availableStonesForSingle(Stone temp) {
        ArrayList<Stone> arrayList = new ArrayList<>(); // Create an empty ArrayList to store the available stones
        for (int[] var : dirArr) { // Loop through the array containing the possible directions
            try {
                // Check if the stone in the given direction belongs to the player, if so add it to the ArrayList
                if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() == player) {
                    arrayList.add(hex[temp.row + var[0]][temp.col + var[1]]);
                }
            }
            // Catch IndexOutOfBoundsException since the given direction may lead to an index out of bounds error
            catch (IndexOutOfBoundsException ignored) {

            }
            // Catch any other exception and print its message
            catch (Exception e) {System.out.println(e.getMessage());}
        }
        return arrayList; // Return the ArrayList containing the available stones
    }

    // This method returns the available stones for a double stone move
    private ArrayList<Stone> availableStonesForDouble(ArrayList<Stone> temp) {
        ArrayList<Stone> stones = new ArrayList<>(); // Create an empty ArrayList to store the available stones
        Stone.sort(temp); // Sort the ArrayList of stones so that we can easily compare the first and second stones
        Stone first = temp.get(0); // Get the first stone
        Stone second = temp.get(1); // Get the second stone
        int drow = first.row - second.row; // Calculate the row difference between the two stones
        int dcol = first.col - second.col; // Calculate the column difference between the two stones
        // Check if the resulting move will lead to a valid edge
        if (first.row + drow < hex.length && first.row + drow >= 0 && first.col + dcol < hex.length && first.col + dcol >= 0) {
            // Check if the stone on the resulting edge belongs to the player, if so add it to the ArrayList
            if (hex[first.row + drow][first.col + dcol].getMainNum() == player) {
                stones.add(hex[first.row + drow][first.col + dcol]);
            }
        }
        // Check the other possible edge
        if (second.row - drow < hex.length && second.row - drow >= 0 && second.col - dcol < hex.length && second.col - dcol >= 0) {
            // Check if the stone on the other resulting edge belongs to the player, if so add it to the ArrayList
            if (hex[second.row - drow][second.col - dcol].getMainNum() == player) {
                stones.add(hex[second.row - drow][second.col - dcol]);}}
        return stones; // Return the ArrayList containing the available stones
    }

    // This method returns the amount of stones moved
    public int getAmountOfStonesMoved() {
        return this.amountOfStonesMoved;
    }

    // this handles lines and checks if they can move
    private void targetLine(int drow, int dcol, Stone mainStone, int count, ArrayList<Stone> targets, int size) {
        if (size <= count) return;
        try {
            int mainNum = hex[mainStone.row + drow][mainStone.col + dcol].getMainNum(); // main num of next stone
            if (mainNum == 0) {
                targets.add(hex[mainStone.row + drow][mainStone.col + dcol]); // adds the next stone
            }
            else if (mainNum == 4 && count >= 1) {
                targets.add(hex[mainStone.row][mainStone.col]); // if the next stone is edge and count is >= 1 then add current stone
            }
            else if (mainNum == this.player *-1) { // if next stone is enemy
                targetLine(drow, dcol, hex[mainStone.row + drow][mainStone.col + dcol], count + 1, targets, size); // keep going
                }}
        catch (ArrayIndexOutOfBoundsException e) {
            if (count >= 1) {
                targets.add(mainStone); // add future
                }}}
}
