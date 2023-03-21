package com.example.abalone.play;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.abalone.R;
import com.example.abalone.play.Control.Control;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Data.UserAdapter;
import com.example.abalone.play.Logic.Data.UserTable;

import java.io.IOException;
import java.util.ArrayList;

public class ShowDatabase extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private User user;
    ArrayList<User> list;
    ListView lv;
    UserTable userTable;
    UserAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showall_activity);

        findViewById(R.id.back).setOnClickListener(this::backClicked);
        lv = findViewById(R.id.lv);
        userTable = new UserTable(this);
        list = new ArrayList<>();
        onBegin();
    }

    private void backClicked(View view) {
        onBackPressed();
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        long userId = Long.parseLong((((TextView) v.findViewById(R.id.sId)).getText().toString()));
        user = userTable.getStudentByID(userId);

        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        Control.setSelectedUser(user);
        Toast.makeText(this, user.getName() + " " + user.getSurname() + " selected", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    public void onBegin() {
        list = userTable.getAllStudents();
        Log.i("data", "list size is " + list.size());

        studentAdapter = new UserAdapter(this, 0, list, userTable);
        lv.setAdapter(studentAdapter);
        lv.setOnItemClickListener(this::onItemClick);
    }

}