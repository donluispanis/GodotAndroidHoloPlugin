package com.godot.godotandroidholoplugin;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

public class NotificationScheduler {
    private static final long ONE_SECOND = 1000;

    public static final String NOTIFICATION_PREFERENCES = "notification_preferences";
    public static final String REPEATING_NOTIFICATION_PREFERENCES = "r_notification_preferences";

    public static void scheduleNotification(String message, String title, String image, int interval, int tag, Context context){
        if(interval <= 0) return;

        Log.d("godot", "scheduleLocalNotification: " + message);
        saveNotificationPreferences(message, title, image, interval, tag, context);
        PendingIntent sender = getNotificationPendingIntent(message, title, image, tag, context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, interval);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    public static void rescheduleNotification(Context context){
        SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_PREFERENCES, 0);
        String message = prefs.getString("message", "");
        String title = prefs.getString("title", "");
        String image = prefs.getString("image", "");
        int interval = prefs.getInt("interval", 0);
        int tag = prefs.getInt("tag", 0);

        if (message.isEmpty())
            return;

        scheduleNotification(message, title, image, interval, tag, context);
    }

    public static void cancelScheduledNotification(int tag, Context context){
        Log.d("godot", "cancelLocalNotification");

        // Cancel any pending intents
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = getNotificationPendingIntent("", "", "", tag, context);
        am.cancel(sender);

        // Cancel any showed notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(tag);

        saveNotificationPreferences("", "", "", 0, tag, context);
    }

    public static void scheduleRepeatingNotification(String message, String title, String image, int interval, int repeatInterval, int tag, Context context){
        if(interval <= 0) return;

        Log.d("godot", "scheduleLocalRepeatingNotification: " + message);
        saveRepeatingNotificationPreferences(message, title, image, interval, repeatInterval, tag, context);
        PendingIntent sender = getNotificationPendingIntent(message, title, image, tag, context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, interval);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval * ONE_SECOND, sender);
    }

    public static void rescheduleRepeatingNotification(Context context){
        SharedPreferences prefs = context.getSharedPreferences(REPEATING_NOTIFICATION_PREFERENCES, 0);
        String message = prefs.getString("message", "");
        String title = prefs.getString("title", "");
        String image = prefs.getString("image", "");
        int interval = prefs.getInt("interval", 0);
        int repeatInterval = prefs.getInt("repeatInterval", 0);
        int tag = prefs.getInt("tag", 0);

        if (message.isEmpty())
            return;

        scheduleRepeatingNotification(message, title, image, interval, repeatInterval, tag, context);
    }

    public static void cancelScheduledRepeatingNotification(int tag, Context context){
        Log.d("godot", "cancelLocalRepeatingNotification");

        // Cancel any pending intents
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = getNotificationPendingIntent("", "", "", tag, context);
        am.cancel(sender);

        // Cancel any showed notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(tag);

        saveRepeatingNotificationPreferences("", "", "", 0, 0, tag, context);
    }

    private static void saveNotificationPreferences(String message, String title, String image, int interval, int tag, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_PREFERENCES, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("message", message);
        editor.putString("title", title);
        editor.putString("image", image);
        editor.putInt("interval", interval);
        editor.putInt("tag", tag);
        editor.apply();
    }

    private static void saveRepeatingNotificationPreferences(String message, String title, String image, int interval, int repeatInterval, int tag, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(REPEATING_NOTIFICATION_PREFERENCES, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("message", message);
        editor.putString("title", title);
        editor.putString("image", image);
        editor.putInt("interval", interval);
        editor.putInt("repeatInterval", repeatInterval);
        editor.putInt("tag", tag);
        editor.apply();
    }

    private static PendingIntent getNotificationPendingIntent(String message, String title, String image, int tag, Context context) {
        Intent i = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        i.putExtra("notification_id", tag);
        i.putExtra("message", message);
        i.putExtra("title", title);
        i.putExtra("image", image);

        return PendingIntent.getBroadcast(context, tag, i, get_intent_flags());
    }

    public static int get_intent_flags()
    {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        if(android.os.Build.VERSION.SDK_INT >= 23)
        {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return flags;
    }
}
