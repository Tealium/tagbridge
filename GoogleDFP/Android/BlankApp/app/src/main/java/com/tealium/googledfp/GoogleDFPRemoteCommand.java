package com.tealium.googledfp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.tealium.library.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class GoogleDFPRemoteCommand extends RemoteCommand {

    private static final String COMMAND_CREATE_AD = "create_ad";
    private static final String COMMAND_GET_AD_UNIT_IDS = "get_ad_unit_ids";
    private static final String COMMAND_REMOVE_AD = "remove_ad";

    private static final int STATUS_NO_VIEW = 418;
    private static final int STATUS_INCOMPATIBLE = 419;
    private static final int STATUS_AD_NOT_FOUND = 420;

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

        final String command = response.getRequestPayload().optString("command", null);

        if (COMMAND_CREATE_AD.equals(command)) {
            this.createAd(response);
        } else if (COMMAND_GET_AD_UNIT_IDS.equals(command)) {
            this.getAdUnitIds(response);
        } else if (COMMAND_REMOVE_AD.equals(command)) {
            this.removeAd(response);
        } else {
            response.setStatus(Response.STATUS_BAD_REQUEST);
            response.setBody(command + " is an unknown command.");
        }

        response.send();
    }

    private void createAd(Response response) {

        final FrameLayout contentView = this.getCurrentContentView(response);
        if (contentView == null) {
            return;
        }

        final AdConfiguration.Anchor anchor = parseAnchor(response.getRequestPayload());

        PublisherAdView adView = new PublisherAdView(contentView.getContext());
        adView.setAdSizes(parseAdSizes(response.getRequestPayload()));
        adView.setAdUnitId(parseAdUnitId(response.getRequestPayload()));
        adView.setLayoutParams(createAdViewLayoutParams(anchor));

        AdConfiguration adConfiguration = new AdConfiguration(adView, anchor);
        adView.setTag(adConfiguration);
        adView.getViewTreeObserver().addOnGlobalLayoutListener(adConfiguration);

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        adView.loadAd(adRequest);

        contentView.addView(adView);
    }

    private void getAdUnitIds(Response response) {
        final FrameLayout contentView = this.getCurrentContentView(response);
        if (contentView == null) {
            return;
        }

        final JSONArray adUnitIds = new JSONArray();

        for (int i = 0; i < contentView.getChildCount(); i++) {
            View child = contentView.getChildAt(i);
            if (child instanceof PublisherAdView) {
                adUnitIds.put(((PublisherAdView) child).getAdUnitId());
            }
        }

        response.setBody(adUnitIds.toString());
    }

    private void removeAd(Response response) {
        final FrameLayout contentView = this.getCurrentContentView(response);
        if (contentView == null) {
            return;
        }

        final String adUnitId = parseAdUnitId(response.getRequestPayload());
        PublisherAdView adView = null;

        for (int i = 0; i < contentView.getChildCount(); i++) {
            View child = contentView.getChildAt(i);
            if (child instanceof PublisherAdView) {
                if (adUnitId.equals(((PublisherAdView) child).getAdUnitId())) {
                    adView = (PublisherAdView) child;
                    break;
                }
            }
        }

        if (adView == null) {
            response.setStatus(STATUS_AD_NOT_FOUND);
            response.setBody("No ad found with unit id " + adUnitId);
            return;
        }

        contentView.removeView(adView);
    }

    private FrameLayout getCurrentContentView(Response response) {
        final Activity activity = this.getCurrentActivity();

        if (activity == null) {
            response.setStatus(STATUS_NO_VIEW);
            response.setBody("There is no visible activity.");
            return null;
        }

        final View contentView = activity.findViewById(android.R.id.content);
        if (!(contentView instanceof FrameLayout)) {
            response.setStatus(STATUS_INCOMPATIBLE);
            response.setBody("This view is incompatible for ad display");
            return null;
        }

        return (FrameLayout) contentView;
    }

    private Activity getCurrentActivity() {
        return this.currentActivity == null ? null : this.currentActivity.get();
    }

    private int dipToPx(int dipValue) {
        DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private static ViewTreeObserver.OnGlobalLayoutListener createGlobalLayoutListener(
            final PublisherAdView adView) {

        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (adView.getHeight() <= 0) {
                    return;
                }


            }
        };
    }

    private static String parseAdUnitId(JSONObject payload) {
        final String adUnitId = payload.optString("ad_unit_id", null);
        if (adUnitId == null) {
            throw new IllegalArgumentException("ad_unit_id is missing!");
        }
        return adUnitId;
    }

    private static AdSize[] parseAdSizes(JSONObject payload) {

        final JSONArray adSizesArray = payload.optJSONArray("ad_sizes");
        if (adSizesArray == null || adSizesArray.length() == 0) {
            throw new IllegalArgumentException("ad_sizes is missing values");
        }

        final AdSize[] adSizes = new AdSize[adSizesArray.length()];
        String adSizeValue;

        for (int i = 0; i < adSizesArray.length(); i++) {
            // TODO: custom ad size
            if ("BANNER".equals(adSizeValue = adSizesArray.optString(i))) {
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

    private static AdConfiguration.Anchor parseAnchor(JSONObject payload) {
        final String anchor = payload.optString("anchor", null);
        if (anchor == null) {
            throw new IllegalArgumentException("key \"anchor\" is missing");
        }

        if ("top".equals(anchor)) {
            return AdConfiguration.Anchor.TOP;
        } else if ("bottom".equals(anchor)) {
            return AdConfiguration.Anchor.BOTTOM;
        }

        throw new IllegalArgumentException("Value \"" + anchor + "\" is not valid for key \"anchor\".");
    }

    private static FrameLayout.LayoutParams createAdViewLayoutParams(AdConfiguration.Anchor anchor) {

        int gravity;

        switch (anchor) {
            case TOP:
                gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case BOTTOM:
                gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                break;
            default:
                throw new IllegalArgumentException(anchor + " is not a supported anchor.");
        }

        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        fllp.gravity = gravity;

        return fllp;
    }

}
