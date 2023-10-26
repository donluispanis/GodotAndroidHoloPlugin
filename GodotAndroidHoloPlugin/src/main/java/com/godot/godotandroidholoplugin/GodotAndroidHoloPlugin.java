package com.godot.godotandroidholoplugin;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.sql.Time;
import java.util.Set;

public class GodotAndroidHoloPlugin extends GodotPlugin {

    static final String REVIEW_SIGNAL = "on_review_flow_ended";
    static final String LOAD_ERROR_SIGNAL = "on_review_failed_to_load";

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

    void emitGodotSignal(String signalName, Object... signalArgs) {
        emitSignal(signalName, signalArgs);
    }

    @UsedByGodot
    public void startInAppReviewFlow() {
        InAppReviewUtilities.startInAppReviewFlow(this, getActivity());
    }

    @UsedByGodot
    public void navigateToPlayStore(String playStoreUrl) {
        InAppReviewUtilities.navigateToPlayStore(getActivity(), playStoreUrl);
    }

    @UsedByGodot
    public boolean isNetworkTimeEnabled() {
        return TimeUtilities.isNetworkTimeEnabled(getActivity());
    }
}