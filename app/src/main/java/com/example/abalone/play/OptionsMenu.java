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

    public OptionsMenu(Context context, View anchor, FragmentManager fragmentManager1) {
        super(context, anchor);
        getMenuInflater().inflate(R.menu.popup_menu, super.getMenu());
        this.context = context;
        this.setOnMenuItemClickListener(this::onOptionsItemSelected);
        this.fragmentManager = fragmentManager1;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(context, ShowDatabase.class);
        switch(id) {
            case R.id.log:
                    context.startActivity(intent);
                break;
            case R.id.register:
                RegisterFragment fragment = new RegisterFragment(context);
                fragment.show(fragmentManager, "Register");
                break;
            case R.id.notification:
                popTimePicker();
                break;
            case R.id.bibi:
                goToSite();
                break;
            default:
                break;
        }

        return true;
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

    private void goToSite () {
        goToUrl ("https://nevokaplan4.wixsite.com/bibis-adventure");
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        context.startActivity(launchBrowser);
    }
}
