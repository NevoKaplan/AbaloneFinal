package com.example.abalone.play;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abalone.R;
import com.example.abalone.play.Control.Layouts;
import com.example.abalone.play.Recycler.CustomAdapter;
import com.example.abalone.play.Recycler.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.Random;

public class ChooseActivity extends AppCompatActivity {

    // Declare variables
    private int bluePiece, redPiece;
    private final ArrayList<int[][]> layouts = Layouts.allLayouts(); // Array list of layouts
    private final int boardsAmount = layouts.size(); // Number of layouts
    private final ImageView[][][] boards = new ImageView[boardsAmount][9][9]; // 3D array of ImageViews for the boards
    private final int[][][] idArray = new int[boardsAmount][9][9]; // 3D array of IDs for the boards
    private final ImageView[] realBoards = new ImageView[boardsAmount]; // Array of ImageViews for the real boards

    private final int positionOffset = -106; // Offset for positions

    private CustomAdapter adapter; // Adapter for RecyclerView

    static Random rnd = new Random(); // Random object

    private boolean aiActivated; // Flag to indicate if AI is activated

    // Array of possible drawable resources
    private final int[] possible = {R.drawable.marble_blue, R.drawable.marble_red,
            R.drawable.marble_gray, R.drawable.marble_white,
            R.drawable.checkers_brown, R.drawable.checkers_blue,
            R.drawable.checkers_white, R.drawable.checkers_gray,
            R.drawable.cyan_space, R.drawable.red_space};

    // onCreate method
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_layout);

        bluePiece = R.drawable.marble_blue; // Set blue piece drawable
        redPiece = R.drawable.marble_red; // Set red piece drawable

        ArrayList<Drawable> lst = arrToList(); // Convert array to list of Drawables
        adapter = new CustomAdapter(lst); // Create CustomAdapter object

        onBegin(); // Call onBegin method
        onChange(); // Call onChange method
        arrowAnimations(); // Call arrowAnimations method

        System.out.println("I AM HERE CURRENTLY"); // Print message to console
    }

    // Method to handle switch state changes
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        aiActivated = isChecked; // Set aiActivated flag
    }

    // Method to handle button clicks
    public void onClick4(View view) {
        Intent intent = new Intent(this, GameActivity.class); // Create intent for GameActivity
        Bundle bundle = new Bundle(); // Create bundle object
        bundle.putInt("bluePiece", bluePiece); // Add bluePiece to bundle
        bundle.putInt("redPiece", redPiece); // Add redPiece to bundle
        int layoutNum;
        switch (view.getId()) { // Switch statement to determine which board was clicked
            case R.id.board1:
                layoutNum = 1;
                break;
            case R.id.board2:
                layoutNum = 2;
                break;
            case R.id.board3:
                layoutNum = 3;
                break;
            case R.id.board4:
                layoutNum = 4;
                break;
            case R.id.board5:
                layoutNum = 5;
                break;
            case R.id.board6:
                layoutNum = 6;
                break;
            case R.id.board7:
                layoutNum = 7;
                break;
            case R.id.board8:
                layoutNum = 8;
                break;
            default:
                layoutNum = rnd.nextInt(boardsAmount) + 1;
                break;
        }
        bundle.putInt("layoutNum", layoutNum); // Add layoutNum to bundle
        bundle.putBoolean("activateAI", aiActivated); // Add aiActivated flag to bundle
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }


    private void onBegin() {
        // Get the RecyclerViews and start threads to set them up
        RecyclerView recyclerViewTop = findViewById(R.id.recyclerViewTop);
        Thread setUpTopRecycler = new Thread(new firstThread(recyclerViewTop, true));
        RecyclerView recyclerViewBottom = findViewById(R.id.recyclerViewBottom);
        Thread setUpBottomRecycler = new Thread(new firstThread(recyclerViewBottom, false));
        setUpTopRecycler.start();
        setUpBottomRecycler.start();

        // Set up the menu
        menuSetUp();

        // Get the image views for the boards and create the boards
        realBoards[0] = findViewById(R.id.board1);
        realBoards[1] = findViewById(R.id.board2);
        realBoards[2] = findViewById(R.id.board3);
        realBoards[3] = findViewById(R.id.board4);
        realBoards[4] = findViewById(R.id.board5);
        realBoards[5] = findViewById(R.id.board6);
        realBoards[6] = findViewById(R.id.board7);
        realBoards[7] = findViewById(R.id.board8);
        int size = (int)((realBoards[0].getLayoutParams().width * 0.97)/9);
        int i = 0;
        for (int[][] placeAcc : layouts) {
            createBoard(size, realBoards[i], placeAcc, i);
            // Set an onClickListener for each board
            realBoards[i].setOnClickListener(this::onClick4);
            i++;
        }
        aiActivated = false;

        // Set up the click listeners for the buttons and the switch
        findViewById(R.id.shuffle).setOnClickListener(this::onClick4);
        findViewById(R.id.topRightArrow).setOnClickListener(this::onArrowClick);
        findViewById(R.id.topLeftArrow).setOnClickListener(this::onArrowClick);
        findViewById(R.id.bottomRightArrow).setOnClickListener(this::onArrowClick);
        findViewById(R.id.bottomLeftArrow).setOnClickListener(this::onArrowClick);
        SwitchCompat switch1 = findViewById(R.id.aiSwitch);
        switch1.setOnCheckedChangeListener(this::onCheckedChanged);
    }

    // Runnable class for setting up a RecyclerView
    private class firstThread implements Runnable {
        RecyclerView recycler;
        boolean top;

        public firstThread(RecyclerView recycler, boolean top) {
            this.recycler = recycler;
            this.top = top;
        }

        @Override
        public void run() {
            // Set the adapter for the RecyclerView
            recycler.setAdapter(adapter);
            SpacesItemDecoration dividerItemDecoration = new SpacesItemDecoration(16);

            // Set the LinearLayoutManager and add a scroll listener to the RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.recycler.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    // Select the middle item and update the view
                    recyclerView.post(() -> selectMiddleItem(recyclerView, top));
                    onChange();
                }
            });

            // Set up a LinearSnapHelper and attach it to the RecyclerView
            LinearSnapHelper snapHelper = new LinearSnapHelper() {
                @Override
                public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                    View centerView = findSnapView(layoutManager);
                    if (centerView == null)
                        return RecyclerView.NO_POSITION;
                    int position = layoutManager.getPosition(centerView);
                    int targetPosition = -1;
                    if (layoutManager.canScrollHorizontally()) {
                        if (velocityX < 0) {
                            targetPosition = position - 1;
                        } else {
                            targetPosition = position + 1;
                        }
                    }

                    if (layoutManager.canScrollVertically()) {
                        if (velocityY < 0) {
                            targetPosition = position - 1;
                        } else {
                            targetPosition = position + 1;
                        }
                    }

                    final int firstItem = 0;
                    final int lastItem = layoutManager.getItemCount() - 1;
                    targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                    return targetPosition;
                }
            };
            snapHelper.attachToRecyclerView(recycler);

            if (top)
                layoutManager.scrollToPositionWithOffset(Integer.MAX_VALUE / 2 + 6, positionOffset);
            else
                layoutManager.scrollToPositionWithOffset(Integer.MAX_VALUE / 2 + 7, positionOffset);
            recycler.setLayoutManager(layoutManager);

            recycler.addItemDecoration(dividerItemDecoration);
        }
    }

    public ArrayList<Drawable> arrToList() {
        // Create an empty ArrayList of Drawable objects
        ArrayList<Drawable> list = new ArrayList<>();
        // For each integer in the possible array, get the corresponding Drawable and add it to the list
        for (int stoneImg : possible)
            list.add(ContextCompat.getDrawable(this, stoneImg));
        // Return the completed list
        return list;
    }

    private void onChange() {
        // Initialize a counter variable i to 0
        int i = 0;
        // Iterate over each hexagon layout and corresponding image view array
        for (int[][] hex : layouts) {
            // Call the update function for the current layout and image view array
            update(hex, boards[i]);
            // Increment i to move on to the next layout and image view array
            i++;
        }
    }

    private void update(int[][] hex, ImageView[][] imageViews) {
        // Set the number of rows and columns to iterate through to 9
        int rows = 9, cols = 9;
        // Iterate over each hexagon in the layout
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Get the number representing the current hexagon from the layout
                int num = hex[i][j];
                // If the hexagon is not empty and its corresponding image view exists
                if (num != 4 && imageViews[i][j] != null) {
                    // If the hexagon represents a blue piece
                    if (num == 1) {
                        // Set the image resource of the image view to the blue piece
                        imageViews[i][j].setImageResource(bluePiece);
                    }
                    // If the hexagon represents a red piece
                    else if (num == -1) {
                        // Set the image resource of the image view to the red piece
                        imageViews[i][j].setImageResource(redPiece);
                    }
                }
            }
        }
    }

    private void createBoard(int size, ImageView frame, int[][] tempHex, int index) {
        // Get the constraint layout for the activity
        ConstraintLayout layout = findViewById(R.id.cons);
        // Request a layout update
        layout.requestLayout();
        // Create a new ConstraintSet to modify the layout constraints
        ConstraintSet cs = new ConstraintSet();
        // Iterate over each row of the hexagon layout
        for (int iRow = 0; iRow < tempHex.length; iRow++) {
            // Iterate over each hexagon in the current row
            for (int iCol = 0; iCol < tempHex[iRow].length; iCol++) {
                // If the hexagon is not empty
                if (tempHex[iRow][iCol] != 4) {
                    // Create a new image view for the hexagon
                    ImageView imageView = new ImageView(this);
                    // Set the layout parameters for the image view
                    ConstraintLayout.LayoutParams lp =
                            new ConstraintLayout.LayoutParams(size, size);
                    // Generate a unique ID for the image view
                    int id = View.generateViewId();
                    // If the current hexagon is in the top row
                    if (iRow == 0) {
                        // Set the top margin of the image view to 1/60th of the height of the frame image view
                        lp.topMargin = frame.getLayoutParams().height / 60;
                    }
                    // Add the ID of the image view to the ID array for the current board and hexagon
                    idArray[index][iRow][iCol] = id;
                    imageView.setId(id);
                    layout.addView(imageView, lp);
                    boards[index][iRow][iCol] = imageView;
                }
                else {
                    boards[index][iRow][iCol] = null;
                }
            }
        }
        cs.clone(layout);
        cs.setDimensionRatio(frame.getId(), tempHex.length + ":" + tempHex[0].length);

        int count = 0;
        for (int iRow = 0; iRow < tempHex.length; iRow++) {
            for (int iCol = 0; iCol < tempHex[iRow].length; iCol++) {
                if (tempHex[iRow][iCol] != 4) {
                    count++;
                    int id = idArray[index][iRow][iCol];
                    cs.setDimensionRatio(id, "1:1");
                    if (iRow == 0) {
                        cs.connect(id, ConstraintSet.TOP, frame.getId(), ConstraintSet.TOP);
                    } else {
                        cs.connect(id, ConstraintSet.TOP, idArray[index][iRow - 1][3], ConstraintSet.BOTTOM);
                    }

                }
            }
            int[] idArrLine = new int[count];
            count = 0;
            for (int i = 0; i < idArray[index][iRow].length; i++) {
                if (idArray[index][iRow][i] != 0) {
                    idArrLine[count] = idArray[index][iRow][i];
                    count++;}
            }
            count = 0;
            cs.createHorizontalChain(frame.getId(), ConstraintSet.LEFT, frame.getId(), ConstraintSet.RIGHT, idArrLine, null, ConstraintSet.CHAIN_PACKED);
        }
        frame.requestLayout();
        cs.applyTo(layout);

    }

    // This method selects the middle item in a RecyclerView and scrolls to it
// recycler: The RecyclerView to select the middle item in
// top: Whether the RecyclerView is the top one (true) or the bottom one (false)
    private void selectMiddleItem(RecyclerView recycler, boolean top) {
// Get the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
        // Find the first and last completely visible items in the layout
        int findMiddleLeft = layoutManager.findFirstCompletelyVisibleItemPosition();
        int findMiddleRight = layoutManager.findLastCompletelyVisibleItemPosition();

// If there is only one completely visible item, select it and return
        if (findMiddleLeft == findMiddleRight) {
            prechange(top, findMiddleRight);
            return;
        }

// Get the indices of the first and last visible items
        int firstVisibleIndex = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleIndex = layoutManager.findLastVisibleItemPosition();

// Iterate through all the visible items in the layout
        for (int i = firstVisibleIndex; i < lastVisibleIndex; i++) {
            // Get the ViewHolder for the current item
            RecyclerView.ViewHolder vh = recycler.findViewHolderForLayoutPosition(i);

            // Get the screen location of the item
            int[] location = new int[2];
            vh.itemView.getLocationOnScreen(location);
            int x = location[0];
            int halfWidth = (int) (vh.itemView.getWidth() * .5);
            int rightSide = x + halfWidth;
            int leftSide = x - halfWidth;

            // Check if the item is in the middle of the screen
            int screenWidth = getScreenWidth();
            boolean isInMiddle = screenWidth * .5 >= leftSide && screenWidth * .5 < rightSide;

            // If the item is in the middle of the screen, select it and return
            if (isInMiddle) {
                prechange(top, i);
                return;
            }
        }
    }

// Get the width of the screen in pixels
        public static int getScreenWidth() {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

// This method is called when an arrow button is clicked
// view: The button that was clicked
        public void onArrowClick(View view) {
            switch (view.getId()) {
                case R.id.topRightArrow:
                    moveTheSelection(findViewById(R.id.recyclerViewTop), 1, true);
                    break;
                case R.id.topLeftArrow:
                    moveTheSelection(findViewById(R.id.recyclerViewTop), -1, true);
                    break;
                case R.id.bottomRightArrow:
                    moveTheSelection(findViewById(R.id.recyclerViewBottom), 1, false);
                    break;
                case R.id.bottomLeftArrow:
                    moveTheSelection(findViewById(R.id.recyclerViewBottom), -1, false);
                    break;
                default:
                    break;
            }
        }

// Move the selection in the given RecyclerView by the given amount
// recycler: The RecyclerView to move the selection in
// right: The amount to move the selection by (positive for right, negative for left)
// top: Whether the RecyclerView is the top one (true) or the bottom one (false)
        public void moveTheSelection(RecyclerView recycler, int right, boolean top) {
// Get the layout manager for the RecyclerView
            LinearLayoutManager layoutManager = (LinearLayoutManager) (recycler.getLayoutManager());
            // Get the index of the first completely visible item and the amount to move the selection by
            int pos = layoutManager.findFirstCompletelyVisibleItemPosition(), dx = right;

// If the selection will land on a piece of the opposite color, double
            if ((top && possible[(pos + dx) % possible.length] == redPiece) || (!top && possible[(pos + dx) % possible.length] == bluePiece))
                dx *= 2;

            int posOffset = 160;

            System.out.println("pos: " + pos + ", posWithIndex: " + (pos + dx));
            prechange(top, pos + dx);
            layoutManager.scrollToPositionWithOffset(pos + dx, posOffset);
    }

    public void prechange(boolean top, int index) {
        if (top) {
            if (possible[index % possible.length] != redPiece)
                bluePiece = possible[index % possible.length];
        }
        else {
            if (possible[index % possible.length] != bluePiece)
                redPiece = possible[index % possible.length];
        }
    }

    // arrow animation
    private void arrowAnimations() {
        Animation right = AnimationUtils.loadAnimation(this, R.anim.move_horizontal_right);
        Animation left = AnimationUtils.loadAnimation(this, R.anim.move_horizontal_left);

        findViewById(R.id.topRightArrow).setAnimation(right);
        findViewById(R.id.bottomRightArrow).setAnimation(right);
        findViewById(R.id.topLeftArrow).setAnimation(left);
        findViewById(R.id.bottomLeftArrow).setAnimation(left);
    }

    public void menuSetUp() {
        ImageView imageView = findViewById(R.id.menu);
        imageView.setOnClickListener(view -> {
            OptionsMenu optionsMenu = new OptionsMenu(ChooseActivity.this, imageView, getSupportFragmentManager());
            optionsMenu.show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations() && !isFinishing()) {
            finish();
        }
    }
}