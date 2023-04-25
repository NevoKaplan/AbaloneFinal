// Import necessary libraries
package com.example.abalone.play;

import android.app.AlertDialog; // Allows the creation of alert dialogs
import android.app.NotificationChannel; // NotificationChannel objects represent a distinct type of notification that can be triggered
import android.app.NotificationManager; // Allows the creation of notifications
import android.content.Context; // Provides access to the application's resources and classes
import android.content.DialogInterface; // Defines the callback for the button presses in alert dialogs
import android.content.Intent; // Represents an operation to be performed
import android.content.SharedPreferences; // Allows storage of key-value pairs
import android.os.Bundle; // Represents a collection of data
import android.view.View; // A basic building block for user interface components
import android.widget.ImageView; // Displays images
import androidx.appcompat.app.AppCompatActivity; // Provides a base class for activities that use the AppCompat library
import com.example.abalone.R; // The resource class that contains references to the app's resources

public class ContinueActivity extends AppCompatActivity {


    // Called when the activity is starting
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continue_layout);  // Sets the layout for this activity
        createNotificationChannel();  // Calls method to create notification channel
        // Loads the SharedPreferences object with the name "my_prefs"
        SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        // String to be used if no saved game board exists
        String noState = getString(R.string.no_board_state);
        // Prints the saved board state, or the "no board state" message if none exists
        System.out.println("This: " + sharedPref.getString(getString(R.string.saved_board_state), noState));
        // If there is no saved game board state, start a new game
        if (sharedPref.getString(getString(R.string.saved_board_state), noState).equals(noState)) {
            newGame();
        }
        menuSetUp();  // Calls method to set up menu
        (findViewById(R.id.continueOld)).setOnClickListener(this::continueGame);  // Sets up listener for "continue game" button
        (findViewById(R.id.newGame)).setOnClickListener(this::beforeNewGame);  // Sets up listener for "new game" button
    }

    // Creates an alert dialog that asks if the user is sure they want to start a new game
    private void beforeNewGame(View view) {
        new AlertDialog.Builder(this)
                .setTitle("New Game")
                .setMessage("Are you sure you want to start a new game?\nYour last save will be deleted.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newGame();  // Calls newGame method if user selects "YES"
                    }
                })
                .setNegativeButton(android.R.string.no, null)  // Does nothing if user selects "NO"
                .setIcon(android.R.drawable.ic_dialog_alert)  // Sets the icon for the alert dialog
                .show();  // Displays the alert dialog
    }

    // Starts a new game
    private void newGame() {
        Intent intent = new Intent(this, ChooseActivity.class);  // Creates an intent to start the ChooseActivity
        startActivity(intent);  // Starts the activity
        finish();  // Closes the current activity
    }

    // Continues the saved game
    private void continueGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);  // Creates an intent to start the GameActivity
        intent.putExtra(getString(R.string.use_saved_board), true);  // Adds extra data to the intent indicating that the saved board should be used
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
        CharSequence name = "Default Channel";
        String description = "Get a notification at will";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("default_channel", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}