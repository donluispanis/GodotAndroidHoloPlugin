package com.godot.godotandroidholoplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartOnReboot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // NOTE: by default, we only store in memory the last notification scheduled, which effectively means that we won't receive
        // more than one notification of each type after rebooting
        NotificationScheduler.rescheduleNotification(context);
        NotificationScheduler.rescheduleRepeatingNotification(context);
    }
}