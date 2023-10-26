package com.godot.godotandroidholoplugin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class InAppReviewUtilities {

    static void startInAppReviewFlow(GodotAndroidHoloPlugin plugin, Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ReviewManager manager = ReviewManagerFactory.create(activity.getApplicationContext());

            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);

                    flow.addOnCompleteListener(onCompleteTask -> {
                        plugin.emitGodotSignal(GodotAndroidHoloPlugin.REVIEW_SIGNAL);
                    });
                } else {
                    plugin.emitGodotSignal(GodotAndroidHoloPlugin.LOAD_ERROR_SIGNAL);
                }
            });
        }
        else {
            plugin.emitGodotSignal(GodotAndroidHoloPlugin.LOAD_ERROR_SIGNAL);
        }
    }

    static void navigateToPlayStore(Activity activity, String playStoreUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(playStoreUrl));
        intent.setPackage("com.android.vending");
        activity.startActivity(intent);
    }
}
