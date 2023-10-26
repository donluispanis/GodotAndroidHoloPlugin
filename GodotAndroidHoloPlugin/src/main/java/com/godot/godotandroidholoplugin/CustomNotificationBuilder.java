package com.godot.godotandroidholoplugin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

public class CustomNotificationBuilder {
    public static final String NOTIFICATION_CHANNEL_ID = "10002" ;

    public static void showNotification(String message, String title, String image, int notificationId, Context context){
        Log.d("godot", "Show notification: " + message);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.setShowBadge(true);
            manager.createNotificationChannel(notificationChannel) ;
        }

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setShowWhen(false);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
        builder.setTicker(message);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setColorized(true);
        builder.setNumber(1);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        setSmallIcon(builder, context);
        setLargeIcon(image, builder, context);
        setColor(builder, context);
        setPendingIntent(builder, context);

        Notification notification = builder.build();

        manager.notify(notificationId, notification);
    }

    private static void setSmallIcon(NotificationCompat.Builder builder, Context context) {
        int defaultIconId = context.getResources().getIdentifier("icon", "mipmap", context.getPackageName());
        int customIconId = context.getResources().getIdentifier("notification_icon", "mipmap", context.getPackageName());

        if (customIconId <= 0)
            builder.setSmallIcon(defaultIconId);
        else
            builder.setSmallIcon(customIconId);
    }

    private static void setLargeIcon(String image, NotificationCompat.Builder builder, Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP)
            return;

        if (image == null || image.trim().isEmpty())
            return;

        int largeImageId = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
        Bitmap largeIcon = ((BitmapDrawable)(ResourcesCompat.getDrawable(context.getResources(), largeImageId, null))).getBitmap();
        builder.setLargeIcon(largeIcon);
    }

    private static void setColor(NotificationCompat.Builder builder, Context context) {
        int colorId = context.getResources().getIdentifier("notification_color", "color", context.getPackageName());

        if (colorId <= 0)
        {
            Log.e("godot", "Missing notification color");
        }
        else
        {
            if(android.os.Build.VERSION.SDK_INT >= 23)
            {
                builder.setColor(context.getResources().getColor(colorId, null));
            }
            else{
                builder.setColor(context.getResources().getColor(colorId));
            }
        }
    }

    private static void setPendingIntent(NotificationCompat.Builder builder, Context context) {
        Class appClass = null;
        try {
            appClass = Class.forName("com.godot.game.GodotApp");
        } catch (ClassNotFoundException e) {
            Log.e("godot", "Godot App not found");
            return;
        }

        Intent intent = new Intent(context, appClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, NotificationScheduler.get_intent_flags());

        builder.setContentIntent(pendingIntent);
    }
}
