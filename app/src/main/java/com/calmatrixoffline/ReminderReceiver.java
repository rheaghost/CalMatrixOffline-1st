package com.calmatrixoffline;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "calmatrix_reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d("Reminder", "onReceive fired at " + new java.util.Date());

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Daily Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("CalMatrix Offline")
                .setContentText("Time to log your food and exercise.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        nm.notify(1001, builder.build());

        // Reschedule for the same time tomorrow
        SharedPreferences prefs = context.getSharedPreferences("CalSnapPrefs", Context.MODE_PRIVATE);
        int minutes = prefs.getInt("reminder_minutes", 0);

        if (minutes > 0) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, minutes);

            android.app.AlarmManager am = (android.app.AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);
            Intent next = new Intent(context, ReminderReceiver.class);
            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                    context, 0, next,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT
                            | android.app.PendingIntent.FLAG_IMMUTABLE);

            if (am != null) {
                am.setExactAndAllowWhileIdle(
                        android.app.AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(), pi);
            }
        }






    }
}