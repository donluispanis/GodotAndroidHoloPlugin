package com.godot.godotandroidholoplugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Set;

public class GodotAndroidHoloPlugin extends GodotPlugin {

    private static final String REVIEW_SIGNAL = "on_review_flow_ended";
    private static final String LOAD_ERROR_SIGNAL = "on_review_failed_to_load";

    public GodotAndroidHoloPlugin(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotAndroidHoloPlugin";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();
        signals.add(new SignalInfo(REVIEW_SIGNAL));
        signals.add(new SignalInfo(LOAD_ERROR_SIGNAL));
        return signals;
    }

    @UsedByGodot
    public void startInAppReviewFlow() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ReviewManager manager = ReviewManagerFactory.create(getActivity().getApplicationContext());

            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);

                    flow.addOnCompleteListener(onCompleteTask -> {
                        emitSignal(REVIEW_SIGNAL);
                    });
                } else {
                    emitSignal(LOAD_ERROR_SIGNAL);
                }
            });
        }
        else {
            emitSignal(LOAD_ERROR_SIGNAL);
        }
    }

    @UsedByGodot
    public void navigateToPlayStore(String playStoreUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(playStoreUrl));
        intent.setPackage("com.android.vending");
        getActivity().startActivity(intent);
    }
}