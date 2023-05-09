// This class represents an options menu that extends the PopupMenu class.
// It contains methods to handle the user's selection in the menu, such as displaying a log, registering for an account, setting a notification, or navigating to a website.
// The class also has a private method to display a time picker dialog when the user selects the "notification" option, which allows them to set a specific time for a notification to be sent.
// The goToUrl() method is used to launch a web browser to navigate to a specified URL.

package com.example.abalone.play;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;

import com.example.abalone.R;
import com.example.abalone.play.alarmManager.MyBroadcastReceiver;

public class OptionsMenu extends PopupMenu {
    public Context context;
    private int totalTime, hour, minute;
    private FragmentManager fragmentManager;

    // Constructor to set up the options menu
    public OptionsMenu(Context context, View anchor, FragmentManager fragmentManager1) {
        super(context, anchor);
        getMenuInflater().inflate(R.menu.popup_menu, super.getMenu());
        this.context = context;
        this.setOnMenuItemClickListener(this::onOptionsItemSelected);
        this.fragmentManager = fragmentManager1;
    }

    // Method to handle the user's selection in the options menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(context, ShowDatabase.class);

        switch(id) {
            // If the user selects "log", start the ShowDatabase activity
            case R.id.log:
                context.startActivity(intent);
                break;
            // If the user selects "register", show the RegisterFragment dialog
            case R.id.register:
                RegisterFragment fragment = new RegisterFragment(context);
                fragment.show(fragmentManager, "Register");
                break;
            // If the user selects "notification", display a time picker dialog to set a notification
            case R.id.notification:
                popTimePicker();
                break;
            // If the user selects "bibi", navigate to the specified website
            case R.id.bibi:
                goToSite();
                break;
            // Do nothing for other options
            default:
                break;
        }
        return true;
    }

    // Method to display a time picker dialog when the user selects "notification"
    private void popTimePicker() {
        totalTime = 0;

        // Listener for when the user sets the time in the dialog
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            totalTime += selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000;
            hour = selectedHour;
            minute = selectedMinute;

            // Set up the alarm manager to send a notification at the specified time
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, MyBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + totalTime, pendingIntent);

            // Show a toast to confirm the time that the notification will be sent
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
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time for Notification");
        timePickerDialog.show();

    }

    private void goToSite () {
        goToUrl ("https://nevokaplan4.wixsite.com/bibis-adventure");
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        context.startActivity(launchBrowser);
    }
}
