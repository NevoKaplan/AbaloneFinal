// Import necessary libraries
package com.example.abalone.play;

// Import necessary classes and packages
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.example.abalone.R;

// Define SplashScreen class that extends AppCompatActivity
public class SplashScreen extends AppCompatActivity {

    // Declare private instance variables
    View first, second, third, fourth, fifth, sixth;
    ImageView circle;
    Animation topAnimation, bottomAnimation, zoomAnimation;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Initialize variables and animations
        init();
        // Create a new handler
        handler = new Handler();
        // Post a delayed Runnable to start the ContinueActivity after 2 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, ContinueActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

        // Set an onClickListener on the SplashScreen layout to cancel the delay and start the activity
        findViewById(R.id.splash_screen_layout).setOnClickListener(v -> {
            // Remove the delayed code from the Handler's queue
            handler.removeCallbacksAndMessages(null);
            // Start the activity
            Intent intent = new Intent(SplashScreen.this, ContinueActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Define the init() method to initialize the animations and views
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

        // Set topAnimation to all views in arr
        View[] arr = {first, second, third, fourth, fifth, sixth};
        for (View v : arr) v.setAnimation(topAnimation);

        circle = findViewById(R.id.circle);
        circle.setAnimation(zoomAnimation);
    }
}