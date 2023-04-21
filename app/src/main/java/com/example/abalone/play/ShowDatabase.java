package com.example.abalone.play;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abalone.R;
import com.example.abalone.play.Control.Control;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Data.UserAdapter;
import com.example.abalone.play.Logic.Data.UserTable;

import java.util.ArrayList;

public class ShowDatabase extends AppCompatActivity {

    private User user;
    ArrayList<User> list;
    ListView lv;
    UserTable userTable;
    UserAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showall_activity);

        // Set back button click listener
        findViewById(R.id.back).setOnClickListener(this::backClicked);

        // Get ListView reference
        lv = findViewById(R.id.lv);

        // Instantiate the UserTable and ArrayList objects
        userTable = new UserTable(this);
        list = new ArrayList<>();

        // Populate the ListView with database entries
        onBegin();
    }

    // Method to handle back button clicks
    private void backClicked(View view) {
        onBackPressed();
    }

    // Method to handle list item clicks
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        // Get the selected user's id and data from the database
        long userId = Long.parseLong((((TextView) v.findViewById(R.id.sId)).getText().toString()));
        user = userTable.getStudentByID(userId);

        // Log and show an alert dialog
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        createSureDialog(user.getName());
    }

    // Method to create an alert dialog to ask user to select player
    public void createSureDialog(String name) {
        Context context = this;
        new AlertDialog.Builder(this)
                .setTitle("Select Player")
                .setMessage("Which player would you like " + name +  " to be?")
                .setPositiveButton("Player 1", (dialog, which) -> {
                    Control.setSelectedUser1(user);
                    Toast.makeText(context, name + " selected for player 1", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Player 2", (dialog, which) -> {
                    Control.setSelectedUser2(user);
                    Toast.makeText(context, name + " selected for player 2", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to populate the ListView with database entries
    public void onBegin() {
        list = userTable.getAllStudents();
        Log.i("data", "list size is " + list.size());

        // Create an adapter and set it to the ListView
        studentAdapter = new UserAdapter(this, 0, list, userTable);
        lv.setAdapter(studentAdapter);

        // Set the onItemClick listener
        lv.setOnItemClickListener(this::onItemClick);
    }
}