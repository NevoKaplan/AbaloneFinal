package com.example.abalone.play.Logic;

import java.util.ArrayList;
import java.util.Iterator;

public class AIBoard extends Board {

    final static int MAX_DEPTH = 3;
    int depth;
    private int val;
    private int preDeadRed, preDeadBlue;
    private ArrayList<Stone>[] madeMove = new ArrayList[2];
    public static int run = 0;


    public boolean sideMoveable;

    private ArrayList<Stone[][]> alreadyBoards;

    private int amountOfStonesMoved;

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


    public AIBoard(Board board) {
        super(false, -1);
        updateBoard(board.hex);
        this.player = board.player;
        this.depth = MAX_DEPTH;
        for (int i = 0; i < madeMove.length; i++)
            madeMove[i] = new ArrayList<>();
        alreadyBoards = new ArrayList<>();
        preDeadBlue = board.deadBlue;
        preDeadRed = board.deadRed;
        deadBlue = board.deadBlue;
        deadRed = board.deadRed;
        sideMoveable = false;
    }

    public AIBoard(AIBoard board) {
        super(false, -1);
        run++;
        updateBoard(board.hex);
        this.player = board.player;
        this.depth = board.depth-1;
        this.val = 0;
        for (int i = 0; i < madeMove.length; i++)
            madeMove[i] = new ArrayList<>();
        preDeadBlue = board.preDeadBlue;
        preDeadRed = board.preDeadRed;
        deadBlue = board.deadBlue;
        deadRed = board.deadRed;
        alreadyBoards = new ArrayList<>();
        sideMoveable = false;
    }

    public void resetAlreadyBoards() {
        this.alreadyBoards = new ArrayList<>();
    }

    private void updateBoard(Stone[][] stones) {
        for (int i = 0; i < this.hex.length; i++) {
            for (int j = 0; j < this.hex[i].length; j++) {
                this.hex[i][j].setMainNum(stones[i][j].getMainNum());
                this.hex[i][j].setOgNum(stones[i][j].getOgNum());
            }
        }
    }

    public int getDepth() {
        return this.depth;
    }
    public int getVal() {
        return this.val;
    }
    public void setVal(int val) {
        this.val = val;
    }
    public ArrayList<Stone>[] getMadeMove() {
        return this.madeMove;
    }
    public void setBestSelected(ArrayList<Stone> selected, ArrayList<Stone> toBe) {
        for (Stone stone : selected) {
            this.madeMove[0].add(stone);
        }
        for (Stone stone : toBe) {
            this.madeMove[1].add(stone);
        }
    }

    public void setBestSelected() {
        for (Stone stone : this.selected) {
            this.madeMove[0].add(stone);
        }
        for (Stone stone : this.toBe) {
            this.madeMove[1].add(stone);
        }
    }

    public ArrayList<AIBoard>[] getNextBoards(int currentPlayer) {
        this.player = currentPlayer;
        return this.IterateNextBoards();
    }


    private boolean checkHelper(Stone[][] other) {
        for (Stone[][] og : alreadyBoards) {
            if (hexEquals(og, other))
                return false;
        }
        return true;
    }
    private boolean checkNotIfExists(Stone[][] board) {
        if (checkHelper(board)) {
            alreadyBoards.add(board);
            return true;
        }
        return false;
    }

    private boolean hexEquals(Stone[][] og, Stone[][] other) {
        for (int i = 0; i < og.length; i++) {
            for (int j = 0; j < og[0].length; j++) {
                if (og[i][j].getMainNum() != other[i][j].getMainNum())
                    return false;
            }
        }
        return true;
    }

    private ArrayList<AIBoard>[] IterateNextBoards() {
        ArrayList<AIBoard>[] nextBoards = new ArrayList[3];
        for (int i = 0; i < nextBoards.length; i++)
            nextBoards[i] = new ArrayList<>();
        int count = 0;                               // count for how many troops already checked - to check less
        if (player == 1)
            count += this.deadBlue;
        else
            count += this.deadRed;

        for (int i = 0; i < this.hex.length && count <= 14; i++) {
            for (int j = 0; j < this.hex[i].length; j++) {
                if (this.hex[i][j].getMainNum() == player) {
                    count++;
                    nextBoards[0].addAll(findMoveSingle(hex[i][j]));
                    ArrayList<Stone> availableDouble = availableStonesForSingle(hex[i][j]);
                    ArrayList<Stone> tempSelected;

                    for (Stone availlable : availableDouble) {
                        tempSelected = new ArrayList<>();
                        tempSelected.add(hex[i][j]);
                        tempSelected.add(availlable);
                        Stone.sort(tempSelected);

                        nextBoards[1].addAll(findMoveMultiple(tempSelected, 2)); // double - problem here
                        ArrayList<Stone> availableTriple = availableStonesForDouble(tempSelected);

                        for (Stone available2 : availableTriple) {
                            tempSelected = new ArrayList<>();
                            tempSelected.add(hex[i][j]);
                            tempSelected.add(availlable);
                            tempSelected.add(available2);

                            Stone.sort(tempSelected);
                            nextBoards[2].addAll(findMoveMultiple(tempSelected, 3)); // triple
                        }
                    }
                }
            }
        }
        return nextBoards;
    }

    private ArrayList<AIBoard> findMoveSingle(Stone start) {
        ArrayList<AIBoard> boards = new ArrayList<>();
        ArrayList<Stone> targets = availableTargetsForSingle(start);
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
        return boards;
    }

    private ArrayList<AIBoard> findMoveMultiple(ArrayList<Stone> starts, int amount) {

        ArrayList<AIBoard> boards = new ArrayList<>();
        ArrayList<Stone> targets = availableTargetsMultiple(starts);

        for (Stone target : targets) {
            AIBoard aiBoard = new AIBoard(this);
            aiBoard.amountOfStonesMoved = amount;
            for (Stone start : starts)
                aiBoard.changeSelected(aiBoard.hex[start.row][start.col]);

            for (Stone s : aiBoard.selected)
                aiBoard.madeMove[0].add(new Stone(s));

            aiBoard.doMoveMultiple(aiBoard.hex[target.row][target.col]);
            aiBoard.cleanSelected();
            if (checkNotIfExists(aiBoard.hex))
                boards.add(aiBoard);
        }
        return boards;
    }

    private void doMoveSingle(Stone moveTo) {
        moveTo.setMainNum(0);

        this.toBe.add(moveTo);
        for (Stone s : this.toBe)
            this.madeMove[1].add(new Stone(s));

        Stone last = selected.get(0);
        int drow = last.row - moveTo.row;
        int dcol = last.col - moveTo.col;

        beforeLineMove(drow, dcol);
    }

    private void doMoveMultiple(Stone moveTo) {
        boolean reverse = this.shouldReverse(moveTo);

        this.madeMove[1].add(new Stone(moveTo));
        if (beforeSideMove(moveTo)) {
            this.sideMoveable = true;
            return;
        }

        shouldReverse2(reverse);

        Stone last = selected.get(selectedSize - 1);
        int drow = moveTo.row - last.row;
        int dcol = moveTo.col - last.col;

        this.madeMove[1] = new ArrayList<>();
        for (Stone s : this.toBe)
            this.madeMove[1].add(new Stone(s));

        this.beforeLineMove(drow, dcol);
    }

    public int getWinner() {
        if (this.deadBlue >= 6)
            return 1;
        else if (this.deadRed >= 6)
            return -1;
        else
            return 0;
    }

    public int evaluate(int amount) {
        //System.out.println("This board player: " + player);
        int sum = 1;
        for (int i = 0; i < hex.length; i++) {
            for (int j = 0; j < hex[i].length; j++) {
                if (hex[i][j].getMainNum() != 4 && hex[i][j].getMainNum() == player) {
                    sum += fromMiddle[i][j];
                }
            }
        }

        switch (amount) {
            case 2:
                sum += 10;
                break;
            case 1:
                sum += 5;
                break;
            default:
                break;
        }

        boolean flag = false;
        for (int i = 0; i < madeMove[0].size(); i++) {
            if (madeMove[0].get(i).row == 3 && madeMove[0].get(i).col == 5)
                flag = true;
        }
        /*if (flag) {
            for (int i = 0; i < 2; i++) {
                if (i == 0)
                    System.out.println("Selected: ");
                else
                    System.out.println("To be: ");
                for (Stone stone : madeMove[i]) {
                    System.out.println(stone);
                }
            }
        }
        System.out.println("deadBlue: " + deadBlue + ", pre: " + preDeadBlue);*/
        boolean flag3 = false;
        if (deadBlue > preDeadBlue) {
            System.out.println("IT DOES WORK1");
            if (player == -1)
                sum *= 10;
            else
                sum /= 5;
            if (sum > 300)
                flag3 = true;
        }
        if (deadRed > preDeadRed) {
            System.out.println("IT DOES WORK2");
            if (player == 1)
                sum *= 10;
            else
                sum /= 5;
        }
        return sum * player;
    }

    @Override
    protected void merge() {
        selected.addAll(toBe);
        selectedSize += toBe.size();
    }

    protected ArrayList<Stone> availableTargetsForSingle(Stone temp) {
        // if code here, can only be 1 selected
        ArrayList<Stone> targets = new ArrayList<>();
        for (int[] var : dirArr) { // all directions list
            try {
                if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() == 0) {
                    targets.add(hex[temp.row + var[0]][temp.col + var[1]]);
                }
            } catch (IndexOutOfBoundsException ignored) {

            }
            catch (Exception e) {
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
        // gets the difference between the 2 stones

        targetLine(drow, dcol, first, 0, stones, size); // from first and on
        targetLine(-drow, -dcol, chosen.get(size-1), 0, stones, size); // from last and on

        Iterator<Stone> it;
        boolean flag, added; // flag checks if stones can be moved to the side


        for (int[] var : dirArr) { // list of all directions
            it = chosen.iterator();
            flag = true;
            added = false;
            ArrayList<Stone> maybe = new ArrayList<>();
            if (!((var[0] == drow && var[1] == dcol) || (var[0] == -drow && var[1] == -dcol))) { // don't check the already checked
                if (((first.col < first.col + var[1]) && (first.row + var[0] <= first.row)) || (drow == 0 && first.col <= first.col + var[1])) { // change only if the current one is more to the left than the next one
                    Stone.reverseList(chosen);
                }
                while (it.hasNext() && flag) {
                    Stone temp = it.next();
                    try {
                        if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() != 0) { // if future doesn't equal to 0, can't be moved
                            flag = false;
                        } else {
                            if (!added) {
                                maybe.add(hex[temp.row + var[0]][temp.col + var[1]]);
                                added = true;
                            }
                        }
                    }
                    catch (IndexOutOfBoundsException e) {
                        flag = false;
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            else {
                flag = false;
            }
            if (flag) { // if all stones can be moved then add the maybe list
                stones.addAll(maybe);
            }
        }
        Stone.sort(stones);
        return stones;
    }

    protected ArrayList<Stone> availableStonesForSingle(Stone temp) {
        // checks for available when 1 is selected
        ArrayList<Stone> arrayList = new ArrayList<>();
        for (int[] var : dirArr) {
            try {
                if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() == player) {
                    arrayList.add(hex[temp.row + var[0]][temp.col + var[1]]);
                }
            }
            catch (IndexOutOfBoundsException ignored) {

            }
            catch (Exception e) {System.out.println(e.getMessage());}
        }
        return arrayList;
    }

    private ArrayList<Stone> availableStonesForDouble(ArrayList<Stone> temp) {
        ArrayList<Stone> stones = new ArrayList<>();
        Stone.sort(temp);
        Stone first = temp.get(0);
        Stone second = temp.get(1);
        int drow = first.row - second.row;
        int dcol = first.col - second.col;
        // checks the edges
        if (first.row + drow < hex.length && first.row + drow >= 0 && first.col + dcol < hex.length && first.col + dcol >= 0) {
            if (hex[first.row + drow][first.col + dcol].getMainNum() == player) {
                stones.add(hex[first.row + drow][first.col + dcol]);
            }
        }
        if (second.row - drow < hex.length && second.row - drow >= 0 && second.col - dcol < hex.length && second.col - dcol >= 0) {
            if (hex[second.row - drow][second.col - dcol].getMainNum() == player) {
                stones.add(hex[second.row - drow][second.col - dcol]);
            }
        }
        return stones;
    }

    public int getAmountOfStonesMoved() {
        return this.amountOfStonesMoved;
    }

    // this handles lines and checks if they can move
    private void targetLine(int drow, int dcol, Stone mainStone, int count, ArrayList<Stone> targets, int size) {
        if (size <= count)
            return;
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
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            if (count >= 1) {
                targets.add(mainStone); // add future

            }
        }
    }

}
