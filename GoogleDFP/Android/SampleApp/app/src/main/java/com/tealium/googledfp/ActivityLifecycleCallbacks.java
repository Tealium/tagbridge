package com.tealium.googledfp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

final class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private static ActivityLifecycleCallbacks instance;

    interface VisiblityListener {
        void onActivityResume(Activity activity);

        void onActivityPause(Activity activity);
    }

    private final Set<VisiblityListener> listeners;

    private ActivityLifecycleCallbacks() {
        this.listeners = Collections.newSetFromMap(new WeakHashMap<VisiblityListener, Boolean>());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        for (VisiblityListener listener : this.listeners) {
            listener.onActivityResume(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        for (VisiblityListener listener : this.listeners) {
            listener.onActivityPause(activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * Register for activity lifecycle callbacks. Should be called on the main thread.
     *
     * @param application the application reference.
     * @param listener    will be held in a weak reference.
     */
    public static synchronized void register(Application application, VisiblityListener listener) {
        if (listener == null || application == null) {
            throw new IllegalArgumentException();
        }

        if (instance == null) {
            application.registerActivityLifecycleCallbacks(instance = new ActivityLifecycleCallbacks());
        }

        instance.listeners.add(listener);
    }
}
