package com.example.abalone.play.Logic;

import com.example.abalone.play.Control.Control;
import com.example.abalone.play.Control.Layouts;
import com.example.abalone.play.GameActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class Board {

    protected int player;
    public Stone[][] hex;
    protected final int[][] dirArr = {{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, 0}, {1, 1}};
    public int deadBlue = 0, deadRed = 0;
    public ArrayList<Stone> selected = new ArrayList<>();
    protected ArrayList<Stone> toBe = new ArrayList<>();
    public int selectedSize = 0;

    public ArrayList<Stone> move2 = null;
    public ArrayList<Stone> targets = null;

    private Control control;
    private AI ai;

    protected boolean AiTurn;

    // get player
    public int getPlayer() {
        return this.player;
    }

    public void setPlayer(int p) {
        this.player = p;
    }

    // creates the board
    public Board(boolean first, int num) {
        AiTurn = false;
        boolean increase = false;
        player = 1;
        int cols = 9, rows = 9;
        int x, temp = (int) Math.floor(cols / 2.0);
        Stone[][] tempHex = new Stone[rows][cols];
        for (int i = 0; i < rows; i++) {
            x = temp;
            for (int j = 0; j < cols; j++) {
                if (!increase && cols - j <= temp) {
                    tempHex[i][j] = new Stone(4, i, j);
                }
                else if (increase && x > 0) {
                    tempHex[i][j] = new Stone(4, i, j);
                    x--;
                }
                else {
                    tempHex[i][j] = new Stone(0, i, j);
                }
            }
            if (!increase && temp <= 0)
                increase = true;
            temp = increase ? temp + 1 : temp - 1;
        }
        hex = tempHex;
        if (first) {

            int[][] placeAcc;
            switch (num) {
                case 2:
                    placeAcc = Layouts.organizeGermanDaisy();
                    break;
                case 3:
                    placeAcc = Layouts.organizeSnake();
                    break;
                case 4:
                    placeAcc = Layouts.organizeCs();
                    break;
                case 5:
                    placeAcc = Layouts.organizeCrown();
                    break;
                case 6:
                    placeAcc = Layouts.organizeDavid();
                    break;
                case 7:
                    placeAcc = Layouts.organizeHook();
                    break;
                case 8:
                    placeAcc = Layouts.organizeArrows();
                    break;
                default:
                    placeAcc = Layouts.organizeNormal();
                    break;
            }
            organize(placeAcc);
        }
    }

    // organize board according to placeAcc
    public void organize(int[][] placeAcc) {
        int redAmount = 14, blueAmount = 14;
        for (int i = 0; i < placeAcc.length; i++) {
            for (int j = 0; j < placeAcc[i].length; j++) {
                int currentNum = placeAcc[i][j];
                hex[i][j].setBothNums(currentNum);
                if (currentNum == 1)
                    blueAmount--;
                else if (currentNum == -1)
                    redAmount--;
            }
        }
        this.deadBlue = blueAmount;
        this.deadRed = redAmount;
    }

    // turns stone array to int array
    public int[][] turnStoneToIntArr() {
        int[][] newHex = new int[9][9];
        for (int i = 0; i < hex.length; i++) {
            for (int j = 0; j < hex[i].length; j++) {
                newHex[i][j] = hex[i][j].getMainNum();
            }
        }
        return newHex;
    }

    // the "main" function
    public boolean makeMove(Stone Stone_ish) {

        boolean pushedTroops = false;
            // select stone
        try {
            if (Stone_ish.getMainNum() == player) {
                if (!choosePiece(Stone_ish, move2)) { // if false cleans selected and chooses the new one
                    cleanSelected();
                    changeSelected(Stone_ish);
                }
                targets = availableTargets();
                move2 = availableStones2();
            }
            else if ((Stone_ish.getMainNum() == player * -1 || Stone_ish.getMainNum() == 0) && !selected.isEmpty()) { // if chose wrong stone and is allowed to
                pushedTroops = pushTroops(targets, Stone_ish);  // push the stone
                if (pushedTroops) {
                    cleanSelected();
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) { // if chosen is out of bounds or anything unexpected happens within the loop
            }
        return pushedTroops;
    }

    public boolean CheckAndMoveAI(int goAhead) {
        if (goAhead == 1 && AI.hasInstance()) {
            ai = AI.getInstance(player * -1);
            AiTurn = true;
            player *= -1;
            if (control == null)
                control = Control.getInstance();
            AiThread aiThread = new AiThread(this, control.getGameUI());
            aiThread.start();
            return true;
        }
        else {
            player *= -1;
            return false;
        }
    }

    public class AiThread extends Thread {

        private Board tempBoard;
        private GameActivity mainActivity;

        public AiThread(Board _tempBoard, GameActivity _mainActivity) {
            this.tempBoard = _tempBoard;
            this.mainActivity = _mainActivity;
        }

        public void run() {
            AIBoard aiBoard = ai.bestMove(tempBoard);
            ArrayList<Stone>[] madeMove = aiBoard.getMadeMove();
            Stone.reverseList(madeMove[0]);
            this.mainActivity.runOnUiThread(() -> control.makeInvisible(madeMove[0], madeMove[1]));
            updateBoardWithAI(aiBoard);
            AiTurn = false;
        } // run()
    }

    // starts the pushing functions
    protected boolean pushTroops(ArrayList<Stone> targets, Stone moveTo) {
        if (targets.isEmpty()) { // if no targets, selection was bad
            cleanSelected();
            return false;
        }
        if (targets.contains(moveTo)) {
            moveStones(moveTo);
            return true;
        }
        return false;
    }

    // moves the stones
    protected void moveStones(Stone moveTo) {
        moveTo.setMainNum(0); // sets move to num to 0 after saving it

        boolean reverse = shouldReverse(moveTo);

        if (beforeSideMove(moveTo))
            return;

        Stone last = selected.get(selectedSize - 1);
        int drow = moveTo.row - last.row;
        int dcol = moveTo.col - last.col;

        shouldReverse2(reverse);

        beforeLineMove(drow, dcol);

    }

    protected void beforeLineMove(int drow, int dcol) {
        Stone moveTo = this.toBe.get(this.toBe.size() - 1);
        int ogNum = moveTo.getOgNum();
        this.merge();
        try {
            if (hex[moveTo.row + drow][moveTo.col + dcol].getMainNum() == 4) { // if next one off grid
                // remove current
                // when player moves to edge
                this.removeStone(ogNum == selected.get(0).getMainNum() * -1);
            }
            else { removeStone(false); }
        }
        catch (ArrayIndexOutOfBoundsException e) { // if next one off grid
            // when player moves to edge
            removeStone(ogNum == selected.get(0).getMainNum() * -1);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        moveTo.setOgNum(moveTo.getMainNum());
    }

    protected boolean shouldReverse(Stone moveTo) {
        boolean reverse = false;

        Stone.sort(selected);
        if (!moveTo.isBefore(selected.get(0))) {
            Stone.reverseList(selected);
            reverse = true; // if one is reversed, all are REVERSED!
        }
        this.toBe.add(moveTo);

        return reverse;
    }

    protected void shouldReverse2(boolean reverse) {
        Stone.sort(toBe); // make sure it's sorted
        if (reverse) { // IF ONE IS REVERSED THEY ALL ARE!
            Stone.reverseList(toBe);}
    }

    protected boolean beforeSideMove(Stone moveTo) {
        Stone last = selected.get(selectedSize - 1);
        int drow = moveTo.row - last.row;
        int dcol = moveTo.col - last.col;


        if (selectedSize > 1) {
            Stone secondLast = selected.get(selectedSize - 2);
            if (makeSureSelected(moveTo, last.row - secondLast.row, last.col - secondLast.col, last)) { // checks the line to see if moveTo is in the same line - if it is move on...
                return false;
            }
            else {

                if ((drow == 1 && dcol <= -1) || (drow == -1 && dcol >= 1)) { // if more than 1 selected and not same line and too far, need to reverse
                    Stone.reverseList(selected);
                    last = selected.get(selectedSize - 1);
                    drow = (moveTo.row - last.row);
                    dcol = (moveTo.col - last.col);
                }
                if (!AiTurn) {
                    if (callToControl(false)) {
                        this.sideMove(drow, dcol);
                        return true;
                    } else
                        return false;
                }
                else {
                    this.sideMove(drow, dcol);
                    return true;
                }
            }
        }
        else
            return false;
    }

    private boolean callToControl(boolean isLineMove) {
        if (Control.hasInstance()) {
            control = Control.getInstance();
            if (isLineMove)
                control.makeInvisible(this.selected);
            else
                control.makeInvisible(this.selected, this.toBe);
            return true;
        }
        else {
            System.out.println("Problem in Board Class");
            return false;
        }
    }

    // if moveTo is same line add all stones leading up to toBe
    private boolean makeSureSelected(Stone moveTo, int drow, int dcol, Stone start) {
        boolean didSomething = false;
        ArrayList<Stone> tempList = new ArrayList<>();
        while (!start.equals(moveTo)) {
            try {
                start = hex[start.row + drow][start.col + dcol];
                if (!toBe.contains(start)) {
                    tempList.add(start);
                } else {
                    didSomething = true;
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return false;
                }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (didSomething)
            toBe.addAll(tempList);
        return didSomething;
    }

    // side move stones
    protected void sideMove(int drow, int dcol) {
        for (Stone temp : selected) {

            int num = hex[temp.row + drow][temp.col + dcol].getMainNum();
            hex[temp.row + drow][temp.col + dcol].setMainNum(temp.getMainNum());
            temp.setMainNum(num);

        }
    }

    // actually push (not side push), might also push enemy
    private void actuallyPush() {
        int size = selectedSize;
        Stone first = selected.get(0);
        for (int i = size - 2; i >= 0; i--) {
            Stone next = selected.get(i + 1);
            Stone current = selected.get(i);
            next.setMainNum(current.getMainNum());
            next.setOgNum(next.getMainNum());
        }
        first.setMainNum(0);
        first.setOgNum(0);
    }

    // checks if stone should be killed and moves on
    private void removeStone(boolean killed) {
        if (killed) {
            if (selected.get(0).getMainNum() == 1)
                deadRed++;
            else
                deadBlue++;
            System.out.println("Dead red: " + deadRed + ", Dead blue: " + deadBlue);
        }
        if (!AiTurn) {
            if (callToControl(true))
                this.actuallyPush();
        }
        else
            this.actuallyPush();
    }

    // returns a list with all available targets to go to
    protected ArrayList<Stone> availableTargets() {
        if (selected.isEmpty())
            return null;
        if (selectedSize >= 2) // can be 2 or 3
            return availableTargets2();

        // if code here, can only be 1 selected
        ArrayList<Stone> targets = new ArrayList<>();
        Stone temp = selected.get(0);
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

    // handle case of more than 1 selected
    private ArrayList<Stone> availableTargets2() {
        Stone first = selected.get(0);
        Stone second = selected.get(1);
        int drow = first.row - second.row;
        int dcol = first.col - second.col;
        ArrayList<Stone> stones = new ArrayList<>();
        int size = selectedSize;
        // gets the difference between the 2 stones

        targetLine(drow, dcol, first, 0, stones, size); // from first and on
        targetLine(-drow, -dcol, selected.get(size-1), 0, stones, size); // from last and on

        Iterator<Stone> it;
        boolean flag, added; // flag checks if stones can be moved to the side
        for (int[] var : dirArr) { // list of all directions
            it = selected.iterator();
            flag = true;
            added = false;
            ArrayList<Stone> maybe = new ArrayList<>();
            if (!((var[0] == drow && var[1] == dcol) || (var[0] == -drow && var[1] == -dcol))) { // don't check the already checked
                if (((first.col < first.col + var[1]) && (first.row + var[0] <= first.row)) || (drow == 0 && first.col <= first.col + var[1])) { // change only if the current one is more to the left than the next one
                    Stone.reverseList(selected);
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
            else if (mainNum == player *-1) { // if next stone is enemy
                targetLine(drow, dcol, hex[mainStone.row + drow][mainStone.col + dcol], count + 1, targets, size); // keep going
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            if (count >= 1) {
                targets.add(mainStone); // add future
            }
        }
    }

    // checks for available to be chosen stones
    protected ArrayList<Stone> availableStones2 () {
        if (selectedSize <= 0) return null;
        ArrayList<Stone> arrayList = new ArrayList<>();
        if (selectedSize == 2)
            return choosePiece2Highlight(arrayList);
        else if (selectedSize >= 3) { return null; } // no more available

        // checks for available when 1 is selected
        Stone temp = selected.get(0);
        for (int[] var : dirArr) {
            try {
                if (hex[temp.row + var[0]][temp.col + var[1]].getMainNum() == player) {
                    arrayList.add(hex[temp.row + var[0]][temp.col + var[1]]);
                }
                else
                    continue;
                if (hex[temp.row + 2 * var[0]][temp.col + 2 * var[1]].getMainNum() == player) {
                    arrayList.add(hex[temp.row + 2 * var[0]][temp.col + 2 * var[1]]);
                }
            }
            catch (IndexOutOfBoundsException ignored) {
            }
            catch (Exception e) {System.out.println(e.getMessage());}
        }
        return arrayList;
    }

    // checks if player
    protected boolean choosePiece(Stone stone, ArrayList<Stone> highlights) {
        if (selected.isEmpty()) {
            changeSelected(stone);
            return true;
        }

        if (selected.contains(stone)) {
            if (selectedSize >= 3) {
                Stone.sort(selected);
                if (selected.get(1).equals(stone)) { // if the middle one is the one you remove...
                    Stone temp = selected.get(2); // also remove another one
                    changeSelected(stone);
                    changeSelected(temp);
                    return true;
                }
            }
            changeSelected(stone);
            return true;
        }

        else if (selectedSize >= 3) {return false;}

        if (!highlights.contains(stone))
            return false;

        if (selectedSize == 2) {
            changeSelected(stone);
            return true;
        }

        Stone temp = selected.get(0);  // there is only one stone in the array...
        for (int[] var: dirArr) { // all directions
            if (var[0] == stone.row - temp.row && var[1] == stone.col - temp.col) {
                changeSelected(stone);
                return true;
            }
            else if (2 * var[0] == stone.row - temp.row && 2 * var[1] == stone.col - temp.col) {
                changeSelected(stone);
                changeSelected(hex[temp.row + var[0]][temp.col + var[1]]);
                return true;
            } // checks for "legality" of the stone and adds them to the list if legal
        }
        return false;
    }

    // checks possible selections when 2 are already selected
    private ArrayList<Stone> choosePiece2Highlight(ArrayList<Stone> stones) {
        Stone.sort(selected);
        Stone first = selected.get(0);
        Stone second = selected.get(1);
        int drow = first.row - second.row;
        int dcol = first.col - second.col;
        // checks the edges
        try {
            if (hex[first.row + drow][first.col + dcol].getMainNum() == player) {
                stones.add(hex[first.row + drow][first.col + dcol]);
            }
        }
        catch (Exception e) {System.out.println("8");}
        try {
            if (hex[second.row - drow][second.col - dcol].getMainNum() == player) {
                stones.add(hex[second.row - drow][second.col - dcol]);
            }
        }
        catch (Exception e) {
            System.out.println("9");
        }
        return stones;
    }

    protected void cleanSelected() {
        for (Stone stone : selected) {
            stone.setSelected(false);
        }
        selected = new ArrayList<>();
        selectedSize = 0;
        toBe.clear();
    }

    protected void merge() {
        selected.addAll(toBe);
        selectedSize += toBe.size();
    }

    protected void changeSelected(Stone stone) {
        stone.isSelected = !stone.isSelected;
        if (stone.isSelected) {
            selected.add(stone);
            selectedSize++;
        }
        else {
            selected.remove(stone);
            selectedSize--;
        }
        Stone.sort(selected);
    }

    public ArrayList<Stone> getSelected() {
        return selected;
    }

    public ArrayList<Stone> getMove2() {
        return move2;
    }

    public ArrayList<Stone> getTargets() {
        return targets;
    }

    private void updateBoardWithAI(AIBoard aiBoard) {
        for (int i = 0; i < hex.length; i++) {
            for (int j = 0; j < hex[i].length; j++) {
                Stone main = this.hex[i][j], other = aiBoard.hex[i][j];
                main.setMainNum(other.getMainNum());
                main.setOgNum(other.getOgNum());
            }
        }
        this.deadRed = aiBoard.deadRed;
        this.deadBlue = aiBoard.deadBlue;
    }
}
