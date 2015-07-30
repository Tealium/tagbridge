package com.tealium.googledfp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.tealium.library.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class GoogleDFPRemoteCommand extends RemoteCommand {

    private static final int RESULT_NO_VIEW = 418;

    private final Context context;
    private WeakReference<Activity> currentActivity;

    public GoogleDFPRemoteCommand(Application application) {
        super("google_dfp", "Google DFP");

        // TODO: consider unregistration
        this.context = application.getApplicationContext();
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

        PublisherAdView adView = new PublisherAdView(activity);
        adView.setAdSizes(parseAdSizes(response.getRequestPayload()));
        adView.setAdUnitId(parseAdUnitId(response.getRequestPayload()));
        adView.setLayoutParams(parseLayoutParams(response.getRequestPayload()));

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        adView.loadAd(adRequest);

        ((FrameLayout) contentView).addView(adView);

        response.send();
    }

    private FrameLayout.LayoutParams parseLayoutParams(JSONObject payload) throws JSONException {
        final int left = payload.optInt("left", Integer.MIN_VALUE);
        final int top = payload.optInt("top", Integer.MIN_VALUE);
        final int right = payload.optInt("right", Integer.MIN_VALUE);
        final int bottom = payload.optInt("bottom", Integer.MIN_VALUE);

        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        int gravity = 0;

        if (left >= 0) {
            fllp.leftMargin = this.dipToPx(left);
            if (right >= 0) {
                gravity |= Gravity.CENTER_HORIZONTAL;
            } else {
                gravity |= Gravity.START;
            }
        }

        if (top >= 0) {
            fllp.topMargin = this.dipToPx(top);
            if (bottom >= 0) {
                gravity |= Gravity.CENTER_VERTICAL;
            } else {
                gravity |= Gravity.TOP;
            }
        }

        if (right >= 0) {
            fllp.rightMargin = this.dipToPx(right);
            if (left < 0) {
                // Wasn't centered, must be right aligned.
                gravity |= Gravity.END;
            }
        }

        if (bottom >= 0) {
            fllp.bottomMargin = this.dipToPx(bottom);
            if (top < 0) {
                // Wasn't centered, must be bottom aligned.
                gravity |= Gravity.BOTTOM;
            }
        }

        fllp.gravity = gravity;

        return fllp;
    }

    private Activity getCurrentActivity() {
        return this.currentActivity == null ? null : this.currentActivity.get();
    }

    private int dipToPx(int dipValue) {
        DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private static String parseAdUnitId(JSONObject payload) {
        final String adUnitId = payload.optString("ad_unit_id", null);
        if (adUnitId == null) {
            throw new IllegalArgumentException("ad_unit_id is missing!");
        }
        return adUnitId;
    }

    private static AdSize[] parseAdSizes(JSONObject payload) throws JSONException {

        final JSONArray adSizesArray = payload.optJSONArray("ad_sizes");
        if (adSizesArray == null || adSizesArray.length() == 0) {
            throw new IllegalArgumentException("ad_sizes is missing values");
        }

        final AdSize[] adSizes = new AdSize[adSizesArray.length()];
        String adSizeValue;

        for (int i = 0; i < adSizesArray.length(); i++) {
            // TODO: custom ad size
            if ("BANNER".equals(adSizeValue = adSizesArray.getString(i))) {
                adSizes[i] = AdSize.BANNER;
            } else if ("LARGE_BANNER".equals(adSizeValue)) {
                adSizes[i] = AdSize.LARGE_BANNER;
            } else if ("MEDIUM_RECTANGLE".equals(adSizeValue)) {
                adSizes[i] = AdSize.MEDIUM_RECTANGLE;
            } else if ("FULL_BANNER".equals(adSizeValue)) {
                adSizes[i] = AdSize.FULL_BANNER;
            } else if ("LEADERBOARD".equals(adSizeValue)) {
                adSizes[i] = AdSize.LEADERBOARD;
            } else if ("SMART_BANNER".equals(adSizeValue)) {
                adSizes[i] = AdSize.SMART_BANNER;
            } else {
                throw new IllegalArgumentException(adSizeValue + " is not a valid ad_size");
            }
        }

        return adSizes;
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
