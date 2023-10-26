package com.godot.godotandroidholoplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CustomNotificationBuilder.showNotification(
                intent.getStringExtra("message"),
                intent.getStringExtra("title"),
                intent.getStringExtra("image"),
                intent.getIntExtra("notification_id", 0),
                context
        );
    }

}
