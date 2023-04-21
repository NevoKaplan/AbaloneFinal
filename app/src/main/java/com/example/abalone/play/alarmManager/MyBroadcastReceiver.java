/**
 * This class is a BroadcastReceiver that listens for alarm events and sends a notification to remind the user to play the Abalone game.
 */
package com.example.abalone.play.alarmManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.abalone.R;
import com.example.abalone.play.SplashScreen;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // Print a message to the console indicating that the notification should be sent
        System.out.println("Message Should Be sent");
        // Send the notification
        sendNotification();
    }

    /**
     * This method creates and sends the notification to remind the user to play Abalone.
     */
    private void sendNotification() {
        // Create an intent to launch the app's opening activity
        Intent launchIntent = new Intent(context, SplashScreen.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Add FLAG_IMMUTABLE flag to the PendingIntent to prevent modification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE);

        // Load the Abalone game icon as a Bitmap
        Bitmap iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.abalone_logo_round);

        // Build the notification using the NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default_channel")
                .setSmallIcon(R.drawable.abalone_logo_round) // Set the small icon for the notification
                .setLargeIcon(iconBitmap) // Set the large icon for the notification
                .setContentTitle("Abalone") // Set the title for the notification
                .setContentText("Don't forget to win some matches!") // Set the content text for the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the priority for the notification
                .setContentIntent(pendingIntent) // Set the PendingIntent to be triggered when the notification is clicked
                .setAutoCancel(true); // Automatically dismiss the notification when the user clicks on it

        // Get the NotificationManagerCompat object and use it to notify the user
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build()); // The first argument is the notification id, which should be unique for each notification
    }
}
