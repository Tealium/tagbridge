package com.tealium.googledfp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.tealium.library.RemoteCommand;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class GoogleDFPRemoteCommand extends RemoteCommand {

    private static final int RESULT_NO_VIEW = 418;
    private WeakReference<Activity> currentActivity;

    public GoogleDFPRemoteCommand(Application application) {
        super("google_dfp", "Google DFP");

        // TODO: consider unregistration
        application.registerActivityLifecycleCallbacks(createActivityLifecycleCallbacks());
    }

    @Override
    protected void onInvoke(Response response) throws Throwable {
        final Activity activity = this.getCurrentActivity();
        if (activity == null) {
            response.setStatus(RESULT_NO_VIEW).send();
            return;
        }

        final View contentView = activity.findViewById(android.R.id.content);
        if (!(contentView instanceof FrameLayout)) {
            // TODO: log error
            return;
        }

        AdView adView = new AdView(activity);
        adView.setLayoutParams(parseLayoutParams(response.getRequestPayload()));
        ((FrameLayout) contentView).addView(adView);
        response.send();
    }

    private Activity getCurrentActivity() {
        return this.currentActivity == null ? null : this.currentActivity.get();
    }

    private static FrameLayout.LayoutParams parseLayoutParams(JSONObject payload) throws JSONException {
        final int left = payload.optInt("left", Integer.MIN_VALUE);
        final int top = payload.optInt("top", Integer.MIN_VALUE);
        final int right = payload.optInt("right", Integer.MIN_VALUE);
        final int bottom = payload.optInt("bottom", Integer.MIN_VALUE);
        final int width = payload.optInt("width", Integer.MIN_VALUE);
        final int height = payload.optInt("height", Integer.MIN_VALUE);

        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                width >= 0 ? width : FrameLayout.LayoutParams.WRAP_CONTENT,
                height >= 0 ? height : FrameLayout.LayoutParams.WRAP_CONTENT);

        int gravity = 0;

        if (left >= 0) {
            fllp.leftMargin = left;
            gravity |= Gravity.START;
        }

        if (top >= 0) {
            fllp.topMargin = top;
            gravity |= Gravity.TOP;
        }

        if (right >= 0) {
            fllp.rightMargin = right;
            gravity |= Gravity.END;
        }

        if (bottom >= 0) {
            fllp.bottomMargin = bottom;
            gravity |= Gravity.BOTTOM;
        }

        fllp.gravity = gravity;

        return fllp;
    }

    private Application.ActivityLifecycleCallbacks createActivityLifecycleCallbacks() {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                currentActivity = null;

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
        };
    }
}
