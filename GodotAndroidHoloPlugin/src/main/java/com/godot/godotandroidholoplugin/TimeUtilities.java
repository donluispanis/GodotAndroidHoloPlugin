package com.godot.godotandroidholoplugin;

import android.app.Activity;
import android.provider.Settings;

public class TimeUtilities {
    static boolean isNetworkTimeEnabled(Activity activity) {
        try {
            return Settings.Global.getInt(activity.getContentResolver(), Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
