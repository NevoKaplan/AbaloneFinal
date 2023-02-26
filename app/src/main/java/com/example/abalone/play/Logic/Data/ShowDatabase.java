package com.example.abalone.play.Logic.Data;

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

import java.io.IOException;
import java.util.ArrayList;

public class ShowDatabase extends AppCompatActivity {

    private Dialog dialog;
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //ImageView imageView = findViewById(R.id.image_view);
                   // imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public User clickDialog(View view) {
        if (view.getId() == R.id.update) {
            /*if (!((TextView) dialog.findViewById(R.id.FirstNameTextBox)).getText().toString().matches("") && !((TextView) dialog.findViewById(R.id.SurnameTextBox)).getText().toString().matches("") && !((TextView) dialog.findViewById(R.id.EmailTextBox)).getText().toString().matches("") && !((TextView) dialog.findViewById(R.id.avgTextBox)).getText().toString().matches("")) {
                         user.setName(((TextView) dialog.findViewById(R.id.FirstNameTextBox)).getText().toString());
                        user.setSurname(((TextView) dialog.findViewById(R.id.SurnameTextBox)).getText().toString());
                        user.setEmail(((TextView) dialog.findViewById(R.id.EmailTextBox)).getText().toString());
                        dialog.dismiss();
                        userTable.updateByRow(user);
                        userTable.close();
                        return user;
            } else {
                Toast.makeText(this, "Must fill all fields", Toast.LENGTH_SHORT).show();
            }*/
        }
        return null;
    }

    public void createDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.findViewById(R.id.update).setOnClickListener(this::clickDialog);
        dialog.findViewById(R.id.delete_button).setOnClickListener(this::clickDialog);
        dialog.setTitle("Update Student");
        ((TextView)dialog.findViewById(R.id.textView)).setText("Update Student");
        ((TextView)dialog.findViewById(R.id.buttonText)).setText("Update");
        ((TextView)dialog.findViewById(R.id.givenID)).setText("Given User ID: " + user.getId());
        ((TextView)dialog.findViewById(R.id.FirstNameTextBox)).setText(user.getName());
        ((TextView)dialog.findViewById(R.id.SurnameTextBox)).setText(user.getSurname());
        ((TextView)dialog.findViewById(R.id.EmailTextBox)).setText(user.getEmail());
        ((ImageView)dialog.findViewById(R.id.userImage)).setImageBitmap(user.getImg());
        dialog.setCancelable(true);
        dialog.show();
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        long userId = Long.parseLong((((TextView) v.findViewById(R.id.sId)).getText().toString()));
        user = userTable.getStudentByID(userId);

        Intent intent = getIntent();
        boolean isSignIn = intent.getBooleanExtra("forSigningIn", false);
        Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        if (isSignIn) {
            Control.setSelectedUser(user);
            Toast.makeText(this, user.getSurname() + " selected", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            updateCaller();
            onBegin();
        }
    }

    public void updateCaller() {
        if (user != null) {
            createDialog();
        } else {
            Toast.makeText(this, "ID number not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBegin() {
        list = userTable.getAllStudents();
        Log.i("data", "list size is " + list.size());

        studentAdapter = new UserAdapter(this, 0, list, userTable);
        lv.setAdapter(studentAdapter);
        lv.setOnItemClickListener(this::onItemClick);
    }

    public static void restartActivity(Context context) {

    }
}