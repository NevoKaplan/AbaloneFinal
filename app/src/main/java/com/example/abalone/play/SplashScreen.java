package com.example.abalone.play;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.abalone.R;

public class SplashScreen extends AppCompatActivity {

    View first, second, third, fourth, fifth, sixth;
    ImageView circle;
    Animation topAnimation, bottomAnimation, zoomAnimation;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, ContinueActivity.class);
                startActivity(intent);
                finish();
            }

        }, 2000);
    }

    private void init() {
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom);

        first = findViewById(R.id.first_line);
        second = findViewById(R.id.second_line);
        third = findViewById(R.id.third_line);
        fourth = findViewById(R.id.fourth_line);
        fifth = findViewById(R.id.fifth_line);
        sixth = findViewById(R.id.sixth_line);

        View[] arr = {first, second, third, fourth, fifth, sixth};
        for (View v : arr) v.setAnimation(topAnimation);

        circle = findViewById(R.id.circle);
        circle.setAnimation(zoomAnimation);
    }
}