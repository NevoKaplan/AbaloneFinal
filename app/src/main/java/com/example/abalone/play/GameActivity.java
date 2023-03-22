package com.example.abalone.play;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import com.example.abalone.R;
import com.example.abalone.play.Control.Control;
import com.example.abalone.play.Logic.Board;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Stone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class GameActivity extends AppCompatActivity {

    ConstraintLayout layout;
    private final int[][] idArray = new int[9][9];
    private Board board;
    private Control control;

    private Drawable bluePiece, redPiece, empty_space;

    private ArrayList<ImageView>[] removedPieces = new ArrayList[2];

    private int removedSize;

    private ImageView movingImages[] = new ImageView[5];

    private MyHandler hndlr;

    private int preRemovedBlue, preRemovedRed;

    private int AiTurn; // 0 No Ai, 1 Ai's turn, -1 Player's turn

    private static final int YOffset = 100;

    private SharedPreferences sharedPref;

    private User mainUser;

    private Bitmap playerImgBitmap, guestImgBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        Intent received = getIntent();
        Bundle bundle = received.getBundleExtra("bundle") ; //getArguments();
        if (bundle == null)
            bundle = new Bundle();
        control = Control.createInstance(bundle.getInt("layoutNum", 1), this);
        int bluePieceInt = bundle.getInt("bluePiece", R.drawable.marble_blue);
        bluePiece = ContextCompat.getDrawable(this, bluePieceInt);
        int redPieceInt = bundle.getInt("redPiece", R.drawable.marble_red);
        redPiece = ContextCompat.getDrawable(this, redPieceInt);
        boolean shouldActivateAI = bundle.getBoolean("activateAI", false);
        if (shouldActivateAI)
            AiTurn = -1;
        else
            AiTurn = 0;
        empty_space = ContextCompat.getDrawable(this, R.drawable.empty_space);

        hndlr = new MyHandler();
        //onConfigurationChanged(this.getResources().getConfiguration());

        sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        board = control.getBoard();

        removedSize = getRemovedPieceSize();
        for (int i = 0; i < removedPieces.length; i++)
            removedPieces[i] = new ArrayList<>();
        layout = findViewById(R.id.layout);

        // if should use previously saved board state
        if (received.getBooleanExtra(getString(R.string.use_saved_board), false)) {
            // do use
            setUpBoardFromSavedState();
        }
        else {
            // don't use
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("bluePiece", bluePieceInt);
            editor.putInt("redPiece", redPieceInt);
            editor.apply();
        }

        preRemovedBlue = board.deadBlue;
        preRemovedRed = board.deadRed;

        int screenWidth = getScreenWidth(), size = screenWidth - (int)(screenWidth * 0.02);
        ImageView background = findViewById(R.id.gridFrame);
        background.getLayoutParams().height = size;
        background.getLayoutParams().width = size;
        background.requestLayout();

        movingImages[0] = findViewById(R.id.movingImage);
        movingImages[1] = findViewById(R.id.movingImage2);
        movingImages[2] = findViewById(R.id.movingImage3);
        movingImages[3] = findViewById(R.id.movingImage4);
        movingImages[4] = findViewById(R.id.movingImage5);

        createBoard((int)((background.getLayoutParams().width * 0.97)/9), board);
        menuSetUp();

        onResume();
        if (AiTurn == 0)
            guestImgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
        else
            guestImgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot_thinking);

        changePlayerImage(board.getPlayer());
        if (AiTurn == 1) {
            board.setPlayer(1);
            if (control.AIMoveMaybe(AiTurn)) { // check if there's AI and if so then make move
                updateBoard();
            }
            checkWin();
        }
    }

    private void setUpBoardFromSavedState() {
        String savedString = sharedPref.getString(getString(R.string.saved_board_state), getString(R.string.no_board_state));
        StringTokenizer st = new StringTokenizer(savedString, ",");
        int[][] savedList = new int[9][9];
        for (int i = 0; i < savedList.length; i++) {
            for (int j = 0; j < savedList[i].length; j++) {
                savedList[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        control.buildCustomBoard(savedList, sharedPref.getInt("currentPlayer", 1));
        AiTurn = sharedPref.getInt("AiTurn", 0);
        bluePiece = ContextCompat.getDrawable(this, sharedPref.getInt("bluePiece", R.drawable.marble_blue));
        redPiece = ContextCompat.getDrawable(this, sharedPref.getInt("redPiece", R.drawable.marble_red));
        int deadRed = board.deadRed, deadBlue = board.deadBlue;
        for (int i = 0; i < deadRed; i++) {
            addRemovedPiece(-1);
        }
        for (int i = 0; i < deadBlue; i++) {
            addRemovedPiece(1);
        }
    }

    private void saveCurrentBoardState() {
        int[][] list = control.getBoardAsInt();
        StringBuilder str = new StringBuilder();
        for (int[] ints : list) {
            for (int anInt : ints) {
                str.append(anInt).append(",");
            }
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_board_state), str.toString());
        editor.putInt("AiTurn", AiTurn);
        editor.putInt("currentPlayer", board.getPlayer());
        editor.apply();
    }


    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }*/

    private void createBoard(int size, Board board) {
        Stone[][] tempHex = board.hex;
        ConstraintSet cs = new ConstraintSet();
        ConstraintLayout.LayoutParams lp2 =
                new ConstraintLayout.LayoutParams(size, size);
        for (ImageView movingImage : movingImages)
            movingImage.setLayoutParams(lp2);

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

    public void gettingStone(View view) {
        int row = (int)view.getTag()/board.hex.length;
        int col = (int)view.getTag()%board.hex.length;
        if (!(board.selectedSize == 0 && board.hex[row][col].getMainNum() != board.getPlayer()) && AiTurn != 1) {
            if (control.setCurrentStone(board.hex[row][col])) {

            }
            else {
                preUpdateBoard();
            }
        }
    }

    private void afterPieceMovement() {
        updateBoard();
        changePlayerImage(board.getPlayer() * -1);
        AiTurn *= -1;
        if (AiTurn == 1)
            changePlayerImage(board.getPlayer() * -1);

        if (control.AIMoveMaybe(AiTurn)) { // check if there's AI and if so then make move
            updateBoard();
        }
        saveCurrentBoardState();
        checkWin();
    }

    private void updateBoard() {
        int removedBluePieces = 14, removedRedPieces = 14;
        for (int i = 0; i < board.hex.length; i++) {
            for (int j = 0; j < board.hex.length; j++) {
                int num = board.hex[i][j].getMainNum();
                if (num != 4) {
                    ImageView imageView = (ImageView) layout.getViewById(idArray[i][j]);
                    // if (imageView != null) {
                    if (num == 1) {
                        imageView.setBackground(bluePiece);
                        removedBluePieces--;
                    } else if (num == -1) {
                        imageView.setBackground(redPiece);
                        removedRedPieces--;
                    } else {
                        imageView.setBackground(empty_space);
                    }
                    //}
                }
            }
        }

        if (removedBluePieces > preRemovedBlue)
            addRemovedPiece(1);
        else if (removedRedPieces > preRemovedRed)
            addRemovedPiece(-1);
        preRemovedRed = removedRedPieces;
        preRemovedBlue = removedBluePieces;

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

    public void makeTroopsInvisible(ArrayList<Stone> selected, ArrayList<Stone> toBe) {

        ArrayList<Stone> newSelected = new ArrayList<>();
        ArrayList<Stone> newToBe = new ArrayList<>();
        for (Stone s : selected)
            newSelected.add(new Stone(s));
        if (toBe != null) {
            for (Stone s : toBe)
                newToBe.add(new Stone(s));
        }
        else
            newToBe.add(newSelected.remove(newSelected.size()-1));

        ArrayList<ImageView> fromImages = new ArrayList<>();
        for (Stone stone : newSelected) {
            int[] cur = stone.getPosition();
            ImageView image = findViewById(idArray[cur[0]][cur[1]]);
            fromImages.add(image);
        }

        ArrayList<int[]> to = new ArrayList<>();
        for (Stone stone : newToBe) {
            to.add(stone.getPosition());
        }
        Collections.reverse(fromImages);
        movingPiece(fromImages, to);
    }

    private void checkWin() {
        if (board.deadBlue >= 6)
            showWin(-1);
        else if (board.deadRed >= 6)
            showWin(1);
    }

    private void showWin(int player) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_board_state), getString(R.string.no_board_state));
        editor.apply();

        String winText;

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_win_dialog);
        ImageView imageView = dialog.findViewById(R.id.playerImage);
        if (player == 1) {
            winText = mainUser.getName() + " Won!!";
            imageView.setImageBitmap(mainUser.getImg());
            if (mainUser.getId() != -1) {
                control.updateUserAndAddPoint();
            }
        }
        else {
            winText = "Guest Won!!";
            imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_image));
        }
        dialog.setTitle(winText);
        dialog.findViewById(R.id.gloryButton).setOnClickListener(this::endActivity);
        ((TextView)dialog.findViewById(R.id.winText)).setText(winText);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void endActivity(View view) {
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
        finish();
    }

    private void changePlayerImage(int player) {
        ImageView imageView = findViewById(R.id.currentPlayer);
        if (player == 1)
            imageView.setImageBitmap(playerImgBitmap);
        else
            imageView.setImageBitmap(guestImgBitmap);
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

    public void movingPiece(ArrayList<ImageView> from, ArrayList<int[]> to){
        ArrayList<int[]> posXY1 = new ArrayList<>(); // pixels on screen
        ArrayList<int[]> posXY2 = new ArrayList<>();
        ImageView toImage = findViewById(idArray[to.get(0)[0]][to.get(0)[1]]);
        for (ImageView image : from) {
            int[] posFrom = new int[2];
            int[] posTo = new int[2];
            int x = 0;
            int y = 0;
            image.getLocationOnScreen(posFrom);
            toImage.getLocationOnScreen(posTo);
            posXY1.add(posFrom);
            if (posXY1.size() > 1) {
                x = posFrom[0] - posXY1.get(0)[0];
                y = posFrom[1] - posXY1.get(0)[1];
            }
            posTo[0] += x;
            posTo[1] += y;
            posXY2.add(posTo);
        }

        //Log.d("TAG", "gettingPiece: " + posXY1[0] + "," + posXY1[1] + "to: " + posXY2[0] + "," + posXY2[1]);
        int size = from.size();
        for (int index = 0; index < size; index++) {
            movingImages[index].setX(posXY1.get(index)[0]);
            movingImages[index].setY(posXY1.get(index)[1] - YOffset);
            ImageView curFrom = from.get(index);
            movingImages[index].setBackground(curFrom.getBackground());
            curFrom.setBackground(empty_space);
            movingImages[index].setVisibility(View.VISIBLE);
            // Log.d("TAG", "movingPiece: " + movingImage.getX() + "," + movingImage.getY());
            MovePieceThread movePieceThread = new MovePieceThread(posXY1.get(index)[0], posXY1.get(index)[1] - YOffset, posXY2.get(index)[0], posXY2.get(index)[1] - YOffset, index, size - 1);
            movePieceThread.start();

        }
    }

    private class MyHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            Bundle data = msg.getData();
            int count = data.getInt("count");
            float[] arr = data.getFloatArray("arr");
            int frames = data.getInt("frames");
            int index = data.getInt("index");
            int maxIndex = data.getInt("maxIndex");
            movingImages[index].setVisibility(View.VISIBLE);
            movingImages[index].setX(arr[0]);
            movingImages[index].setY(arr[1]);
            Log.d("TAG", "handleMessage: " + count);

            // update the Timer TextView
            // tvTimer.setText("" + count);

            if (count == frames) {
                movingImages[index].setVisibility(View.INVISIBLE);
                if (maxIndex == index)
                    afterPieceMovement();
            }
        } // handleMessage(...)

    }

    public class MovePieceThread extends Thread
    {
        private int movingPieceX, movingPieceY;
        private int targetX, targetY;
        private int interval = 32; // "sleep" interval in milisec
        private int frames = 10;
        private int currentIndex;
        private int maxIndex;

        public MovePieceThread(int _movingPieceX, int _movingPieceY, int _targetX, int _targetY, int _currentIndex, int _maxIndex)
        {
            this.movingPieceX = _movingPieceX;
            this.movingPieceY = _movingPieceY;
            this.targetX = _targetX;
            this.targetY = _targetY;
            this.currentIndex = _currentIndex;
            this.maxIndex = _maxIndex;
        }

        public void run()
        {
            int whileCounter = 0;
            float dX, dY;

            dX = (this.targetX - this.movingPieceX)/this.frames;
            dY = (this.targetY - this.movingPieceY)/this.frames;
            while (whileCounter < this.frames)
            {
                try
                {
                    this.movingPieceX += dX;
                    this.movingPieceY += dY;
                    Thread.sleep(interval);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }  // catch
                Log.d(": ", "counter=" + whileCounter);
                whileCounter++;
                sendCounter2Activity(this.movingPieceX, this.movingPieceY, whileCounter);
            } // while
        } // run()

        private void sendCounter2Activity(int X, int Y, int count)
        {
            Message msg = hndlr.obtainMessage();
            float[] arr = {X, Y};
            Bundle data = msg.getData();
            Log.d("AG", "KKK, X: " + X + " Y: " + Y);
            data.putInt("count", count);
            data.putInt("frames", this.frames);
            data.putFloatArray("arr", arr);
            data.putInt("index", this.currentIndex);
            data.putInt("maxIndex", this.maxIndex);
            hndlr.sendMessage(msg);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        mainUser = Control.getSelectedUser();
        if (mainUser == null) {
            mainUser = new User("Player1", "", "someone@abalone.com", BitmapFactory.decodeResource(this.getResources(), R.drawable.hamar), 0, -1);
        }
        playerImgBitmap = mainUser.getImg();
    }
}