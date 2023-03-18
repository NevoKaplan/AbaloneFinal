package com.example.abalone.play;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.abalone.R;
import com.example.abalone.play.Logic.Data.ShowDatabase;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Data.UserTable;
import com.example.abalone.play.alarmManager.MyBroadcastReceiver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OptionsMenu extends PopupMenu {

    private boolean isLoggedIn;
    public Context context;
    Dialog dialog;
    private UserTable table;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Uri imageUri;
    private ImageView userImage;
    private Uri fileUri;
    private int totalTime, hour, minute;

    public OptionsMenu(Context context, View anchor) {
        super(context, anchor);
        table = new UserTable(context);
        getMenuInflater().inflate(R.menu.popup_menu, super.getMenu());
        this.isLoggedIn = false; // need to check first
        this.context = context;
        this.setOnMenuItemClickListener(this::onOptionsItemSelected);
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(context, ShowDatabase.class);
        switch(id) {
            case R.id.settings:
                break;
            case R.id.log:
                if (!isLoggedIn) {
                    intent.putExtra("forSigningIn", true);
                    context.startActivity(intent);
                }
                break;
            case R.id.register:
                createDialog();
                break;
            case R.id.leaderboards:
                intent.putExtra("forSigningIn", false);
                context.startActivity(intent);
                break;
            case R.id.notification:
                popTimePicker();
                break;
            default:
                break;
        }

        return true;
    }

    public void showUsers() {

    }

    private void popTimePicker() {
        totalTime = 0;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                totalTime += selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000;
                hour = selectedHour;
                minute = selectedMinute;

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + totalTime, pendingIntent);

                String toastText = "A notification will be sent in ";
                if (hour == 1)
                    toastText += hour + " hour and ";
                else if (hour > 0)
                    toastText += hour + " hours and ";
                if (minute == 1)
                    toastText += minute + " minute";
                else if (minute > 0)
                    toastText += minute + " minutes";
                else if (hour == 0)
                    toastText += "a few seconds";

                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time for Notification");
        timePickerDialog.show();

    }

    public void createDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        userImage = dialog.findViewById(R.id.userImage);
        dialog.findViewById(R.id.update).setOnClickListener(this::clickDialog);
        dialog.setTitle("Register");
        ((TextView)dialog.findViewById(R.id.textView)).setText("Register");
        ((TextView)dialog.findViewById(R.id.buttonText)).setText("Sign In");
        dialog.findViewById(R.id.delete_button).setVisibility(View.GONE);
        //((TextView)dialog.findViewById(R.id.givenID)).setText("Given User ID: " + user.getId());
        //((TextView)dialog.findViewById(R.id.FirstNameTextBox)).setText(user.getName());
        //((TextView)dialog.findViewById(R.id.SurnameTextBox)).setText(user.getSurname());
        //((TextView)dialog.findViewById(R.id.EmailTextBox)).setText(user.getEmail());
        //((ImageView)dialog.findViewById(R.id.image)).setImageBitmap(user.getImg());
        userImage.setOnClickListener(view ->
        {
            Intent intent = new Intent(context, forUserSelect.class);
            intent.putExtra("userImage", R.id.userImage);
            context.startActivity(intent);
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    public User clickDialog(View view) {
        if (view.getId() == R.id.update) {
            if (!((TextView) dialog.findViewById(R.id.FirstNameTextBox)).getText().toString().matches("") && !((TextView) dialog.findViewById(R.id.EmailTextBox)).getText().toString().matches("")) {
                String name = ((TextView) dialog.findViewById(R.id.FirstNameTextBox)).getText().toString();
                String surname = ((TextView) dialog.findViewById(R.id.SurnameTextBox)).getText().toString();
                String email = ((TextView) dialog.findViewById(R.id.EmailTextBox)).getText().toString();
                ImageView img = dialog.findViewById(R.id.userImage);
                Bitmap bitmap=((BitmapDrawable)img.getDrawable()).getBitmap();
                dialog.dismiss();
                User user = new User(name, surname, email, bitmap);
                System.out.println("Bitmap (OptionsMenu): " + bitmap.getHeight() + ", " + bitmap.getWidth());
                table.createUser(user);
                Toast.makeText(context, name + "'s details saved", Toast.LENGTH_SHORT).show();
                return user;
            } else {
                Toast.makeText(context, "Must fill NAME & E-MAIL fields", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }
}
