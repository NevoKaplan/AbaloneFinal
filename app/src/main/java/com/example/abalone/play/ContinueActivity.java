package com.example.abalone.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.abalone.R;
import com.example.abalone.play.Control.Control;

public class ContinueActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continue_layout);
        createNotificationChannel();

        SharedPreferences sharedPref = getSharedPreferences( "my_prefs", Context.MODE_PRIVATE);
        String noState = getString(R.string.no_board_state);
        System.out.println("This: " + sharedPref.getString(getString(R.string.saved_board_state), noState));
        if (sharedPref.getString(getString(R.string.saved_board_state), noState).equals(noState))
            newGame();

        menuSetUp();
        (findViewById(R.id.continueOld)).setOnClickListener(this::continueGame);
        (findViewById(R.id.newGame)).setOnClickListener(this::beforeNewGame);
    }

    private void beforeNewGame(View view) {
        new AlertDialog.Builder(this)
                .setTitle("New Game")
                .setMessage("Are you sure you want to start a new game?\nYour last save will be deleted.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newGame();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void newGame() {
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);
        finish();
    }

    private void continueGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(getString(R.string.use_saved_board), true);
        startActivity(intent);
        finish();
    }

    public void menuSetUp() {
        ImageView imageView = findViewById(R.id.menu);
        imageView.setOnClickListener(view -> {
            OptionsMenu optionsMenu = new OptionsMenu(ContinueActivity.this, imageView, getSupportFragmentManager());
            optionsMenu.show();
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Get a notification at will";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}