package com.example.abalone.play.alarmManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.abalone.R;
import com.example.abalone.play.SplashScreen;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        System.out.println("Message Should Be sent");
        sendNotification();
    }

    private void sendNotification() {
        // Create an intent to launch the app's opening activity
        Intent launchIntent = new Intent(context, SplashScreen.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add FLAG_IMMUTABLE flag to the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE);

        Bitmap iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.abalone_logo_round);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default_channel")
                .setSmallIcon(R.drawable.abalone_logo_round)
                .setLargeIcon(iconBitmap)
                .setContentTitle("Abalone")
                .setContentText("Don't forget to win some matches!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
