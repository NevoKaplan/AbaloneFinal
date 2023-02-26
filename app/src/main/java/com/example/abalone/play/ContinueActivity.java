package com.example.abalone.play;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.abalone.R;
import com.example.abalone.play.Control.Control;

public class ContinueActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continue_layout);
        if (!Control.hasInstance())
            newGame(null);

        menuSetUp();
        (findViewById(R.id.continueOld)).setOnClickListener(this::continueGame);
        (findViewById(R.id.newGame)).setOnClickListener(this::newGame);
    }

    private void newGame(View view) {
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivity(intent);

    }

    private void continueGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void menuSetUp() {
        ImageView imageView = findViewById(R.id.menu);
        imageView.setOnClickListener(view -> {
            OptionsMenu optionsMenu = new OptionsMenu(ContinueActivity.this, imageView);
            optionsMenu.show();
        });
    }

}