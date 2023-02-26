package com.example.abalone.play;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import com.example.abalone.R;
import com.example.abalone.play.Control.Control;
import com.example.abalone.play.Logic.AI;
import com.example.abalone.play.Logic.Board;
import com.example.abalone.play.Logic.Stone;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    ConstraintLayout layout;
    private final int[][] idArray = new int[9][9];
    private Board board;
    private Control control;

    private Drawable bluePiece, redPiece, empty_space;

    private ArrayList<ImageView>[] removedPieces = new ArrayList[2];

    private int removedSize;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        Intent received = getIntent();
        Bundle bundle = received.getBundleExtra("bundle") ; //getArguments();
        control = Control.getInstance(bundle.getInt("layoutNum", 1));
        bluePiece = ContextCompat.getDrawable(this, bundle.getInt("bluePiece", R.drawable.marble_blue));
        redPiece = ContextCompat.getDrawable(this, bundle.getInt("redPiece", R.drawable.marble_red));
        empty_space = ContextCompat.getDrawable(this, R.drawable.empty_space);
        board = control.getBoard();
        //onConfigurationChanged(this.getResources().getConfiguration());

        int screenWidth = getScreenWidth(), size = screenWidth - (int)(screenWidth * 0.02);
        ImageView background = findViewById(R.id.gridFrame);
        background.getLayoutParams().height = size;
        background.getLayoutParams().width = size;
        background.requestLayout();
        createBoard((int)((background.getLayoutParams().width * 0.97)/9), board);
        buttons();
        removedSize = getRemovedPieceSize();
        for (int i = 0; i < removedPieces.length; i++)
            removedPieces[i] = new ArrayList<>();
    }


    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }*/


    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        control.onCheckedChanged(isChecked);
    }


    private void createBoard(int size, Board board) {
        Stone[][] tempHex = board.hex;
        layout = findViewById(R.id.layout);
        ConstraintSet cs = new ConstraintSet();

        for (int iRow = 0; iRow < tempHex.length; iRow++) {
            for (int iCol = 0; iCol < tempHex[iRow].length; iCol++) {
                if (tempHex[iRow][iCol].getMainNum() != 4) {
                    ImageView imageView = new ImageView(this);

                    ConstraintLayout.LayoutParams lp =
                            new ConstraintLayout.LayoutParams(size, size);
                    int id = View.generateViewId();
                    if (iRow == 0) {
                        ImageView background = findViewById(R.id.gridFrame);
                        lp.topMargin = background.getLayoutParams().height / 62;
                    }
                    idArray[iRow][iCol] = id;
                    imageView.setTag(tempHex.length * iRow + iCol);
                    imageView.setId(id);
                    imageView.setOnClickListener(this::gettingStone);
                    if (tempHex[iRow][iCol].getMainNum() == 1) {
                        imageView.setBackground(bluePiece);
                    }
                    else if (tempHex[iRow][iCol].getMainNum() == -1) {
                        imageView.setBackground(redPiece);
                    }
                    else {
                        imageView.setBackground(empty_space);
                    }
                    layout.addView(imageView, lp);
                }
            }
        }
        cs.clone(layout);
        cs.setDimensionRatio(R.id.gridFrame, tempHex.length + ":" + tempHex[0].length);

        int count = 0;
        for (int iRow = 0; iRow < tempHex.length; iRow++) {
            for (int iCol = 0; iCol < tempHex[iRow].length; iCol++) {
                if (tempHex[iRow][iCol].getMainNum() != 4) {
                    count++;
                    int id = idArray[iRow][iCol];
                    cs.setDimensionRatio(id, "1:1");
                    if (iRow == 0) {
                        cs.connect(id, ConstraintSet.TOP, R.id.gridFrame, ConstraintSet.TOP);
                    } else {
                        cs.connect(id, ConstraintSet.TOP, idArray[iRow - 1][3], ConstraintSet.BOTTOM);
                    }

                }
            }
            int[] idArrLine = new int[count];
            count = 0;
            for (int i = 0; i < idArray[iRow].length; i++) {
                if (idArray[iRow][i] != 0) {
                    idArrLine[count] = idArray[iRow][i];
                    count++;}
            }
            count = 0;
            cs.createHorizontalChain(R.id.gridFrame, ConstraintSet.LEFT, R.id.gridFrame, ConstraintSet.RIGHT, idArrLine, null, ConstraintSet.CHAIN_PACKED);
        }
        cs.applyTo(layout);
    }

    // this should be in control...
    /*public void gettingStone(View view) {
        outerloop:
        for (int i = 0; i < idArray.length; i++) {
            for (int j = 0; j < idArray.length; j++) {
                if (view.getId() == idArray[i][j]) {
                    if (!(board.selectedSize == 0 && board.hex[i][j].getMainNum() != board.player)) {
                        if (control.setCurrentStone(board.hex[i][j]))
                            updateBoard();
                        else
                            preUpdateBoard();
                    }
                    break outerloop;
                }
            }
        }
    }*/

    public void gettingStone(View view) {
        int row = (int)view.getTag()/board.hex.length;
        int col = (int)view.getTag()%board.hex.length;
        int preDeadRed = board.deadRed, preDeadBlue = board.deadBlue;
        if (!(board.selectedSize == 0 && board.hex[row][col].getMainNum() != board.getPlayer())) {
            if (control.setCurrentStone(board.hex[row][col])) {
                int newDeadRed = board.deadRed, newDeadBlue = board.deadBlue;

                updateBoard();
                changePlayerImage(board.getPlayer() * -1);
                System.out.println("Here And Now");
                //addRemovedPiece((int)Math.round(Math.random()));
                checkToAdd(preDeadRed, preDeadBlue, newDeadRed, newDeadBlue);
                preDeadRed = board.deadRed; preDeadBlue = board.deadBlue;
                if (control.hasAiInstance()) {
                    changePlayerImage(board.getPlayer() * -1);
                }
                if (control.AIMoveMaybe()) { // check if there's AI and if so then make move
                    newDeadRed = board.deadRed; newDeadBlue = board.deadBlue;
                    updateBoard();
                    changePlayerImage(board.getPlayer());
                    checkToAdd(preDeadRed, preDeadBlue, newDeadRed, newDeadBlue);
                }
            }
            else
                preUpdateBoard();
        }
    }

    private void checkToAdd(int preRed, int preBlue, int red, int blue) {
        if (blue > preBlue)
            addRemovedPiece(1);
        else if (red > preRed)
            addRemovedPiece(0);
    }

    private boolean updateBoard() {
        for (int i = 0; i < board.hex.length; i++) {
            for (int j = 0; j < board.hex.length; j++) {
                int num = board.hex[i][j].getMainNum();
                if (num != 4) {
                    ImageView imageView = (ImageView) layout.getViewById(idArray[i][j]);
                    // if (imageView != null) {
                    if (num == 1) {
                        imageView.setBackground(bluePiece);
                    } else if (num == -1) {
                        imageView.setBackground(redPiece);
                    } else {
                        imageView.setBackground(empty_space);

                    }
                    //}
                }
            }
        }
        return true;
    }

    private void preUpdateBoard() {
        updateBoard();

        Drawable currentPiece;

        boolean bool_move2 = board.move2 != null, bool_targets = board.targets != null, bool_selected = board.selected != null;
        if (board.getPlayer() == 1) {
            currentPiece = bluePiece;
        }
        else {
            currentPiece = redPiece;
        }
        if (bool_move2) {
            LayerDrawable layerDrawable = (LayerDrawable) (ContextCompat.getDrawable(this, R.drawable.can_selected_layer));
            assert layerDrawable != null;
            layerDrawable.setDrawableByLayerId(R.id.backStoneCan, currentPiece);
            for (Stone stone : board.move2) {
                View view = layout.getViewById(idArray[stone.row][stone.col]);
                view.setBackground(layerDrawable);
            }
        }
        if (bool_targets) {
            LayerDrawable layerDrawable = (LayerDrawable) (ContextCompat.getDrawable(this, R.drawable.layer_to_move));
            assert layerDrawable != null;
            layerDrawable.setDrawableByLayerId(R.id.backStoneMove, currentPiece);
            for (Stone stone : board.targets) {
                View view = layout.getViewById(idArray[stone.row][stone.col]);
                view.setBackground(layerDrawable);
            }
        }
        if (bool_selected) {
            LayerDrawable layerDrawable = (LayerDrawable) (ContextCompat.getDrawable(this, R.drawable.selected_layer));
            assert layerDrawable != null;
            layerDrawable.setDrawableByLayerId(R.id.backStone, currentPiece);
            for (Stone stone : board.selected) {
                View view = layout.getViewById(idArray[stone.row][stone.col]);
                view.setBackground(layerDrawable);
            }
        }
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig)
    {

        super.onConfigurationChanged(newConfig);

        int orientation = newConfig.orientation;
        int screenHeight = getScreenHeight();
        int screenWidth = getScreenWidth();
        setContentView(R.layout.activity_main);
        ImageView background = findViewById(R.id.gridFrame);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            background.getLayoutParams().height = screenWidth - (int)(screenWidth * 0.02);
            background.getLayoutParams().width = background.getLayoutParams().height;
        }

        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            background.getLayoutParams().height = screenHeight - (int)(screenHeight * 0.1);
            background.getLayoutParams().width = background.getLayoutParams().height;
        }

        background.requestLayout();
        createBoard((int)((background.getLayoutParams().width * 0.97)/9), board);
        buttons();
    }*/

    private void buttons() {
        SwitchCompat switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(this::onCheckedChanged);
        switch1.setChecked(AI.hasInstance());
        changePlayerImage(board.getPlayer());
        menuSetUp();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    // right now useless but incase needed...
    private void resetBoard() {
        for (int i = 0; i < idArray.length; i++) {
            for (int j = 0; j < idArray[i].length; j++) {
                if (idArray[i][j] != 0) {
                    View namebar = layout.findViewById(idArray[i][j]);
                    ((ViewGroup) namebar.getParent()).removeView(namebar);
                }
            }
        }
    }

    /*public void onClick(View view) {
        int n = -1;
        switch(view.getId()) {
            case R.id.bNormal:
                n = 1;
                break;
            case R.id.bGerman:
                n = 2;
                break;
            case R.id.bSnakes:
                n = 3;
                break;
            case R.id.bCrown:
                n = 4;
                break;
            default:
                break;
        }
        if (n != -1) {
            board = control.setUpBoard(n);
            updateBoard();
        }
    }*/

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Control.destroy();
        finish();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void addRemovedPiece(int player) {
        if (player == -1) {
            player = 0;
        }
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.transparent);
        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(this.removedSize, this.removedSize);
        layoutParams.leftToLeft = R.id.layout;
        layoutParams.rightToRight = R.id.layout;
        if (player == 0) {
            layoutParams.bottomToTop = R.id.gridFrame;
            layoutParams.setMargins(0, 0, 0, 16);
            imageView.setBackground(redPiece);
        }
        else {
            layoutParams.topToBottom = R.id.gridFrame;
            layoutParams.setMargins(0, 16, 0, 0);
            imageView.setBackground(bluePiece);
        }
        imageView.setLayoutParams(layoutParams);
        imageView.setId(View.generateViewId());
        removedPieces[player].add(imageView);
        int size = removedPieces[player].size();
        float firstBias = (1.0f/(size+1));
        for (int i = 0; i < size; i++) {
            ImageView temp = removedPieces[player].get(i);
            ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams)temp.getLayoutParams();
            layoutParams2.horizontalBias = (firstBias*(i+1));
            temp.setLayoutParams(layoutParams2);
        }
        layout.addView(imageView);
        layout.requestLayout();
    }

    private void changePlayerImage(int player) {
        ImageView imageView = findViewById(R.id.currentPlayer);
        if (player == 1)
            imageView.setBackground(bluePiece);
        else
            imageView.setBackground(redPiece);
    }

    private int getRemovedPieceSize() {
        int screenWidth = getScreenWidth();
        return (int)((screenWidth*0.65/6));
    }

    public void menuSetUp() {
        ImageView imageView = findViewById(R.id.menu);
        imageView.setOnClickListener(view -> {
            OptionsMenu optionsMenu = new OptionsMenu(GameActivity.this, imageView);
            optionsMenu.show();
        });
    }

}