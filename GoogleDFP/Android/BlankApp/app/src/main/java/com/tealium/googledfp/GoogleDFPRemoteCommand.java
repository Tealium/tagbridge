package com.tealium.googledfp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.tealium.library.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

public class GoogleDFPRemoteCommand extends RemoteCommand {

    static final String KEY_AD_UNIT_ID = "ad_unit_id";
    static final String KEY_BANNER_ANCHOR = "banner_anchor";
    static final String KEY_AD_ID = "ad_id";
    static final String KEY_BANNER_AD_SIZES = "banner_ad_sizes";

    private static final String COMMAND_CREATE_BANNER_AD = "create_banner_ad";
    private static final String COMMAND_GET_ADS = "get_ads";
    private static final String COMMAND_REMOVE_AD = "remove_ad";


    private static final int STATUS_NO_VIEW = 418;
    private static final int STATUS_INCOMPATIBLE = 419;
    private static final int STATUS_AD_NOT_FOUND = 420;
    private static final int STATUS_ALREADY_REMOVED = 421;

    private final Context context;
    private WeakReference<Activity> currentActivity;
    private final Map<View, AdIdentifier> ads;

    public GoogleDFPRemoteCommand(Application application) {
        super("google_dfp", "Google DFP");

        // TODO: consider unregistration
        this.context = application.getApplicationContext();
        this.ads = new WeakHashMap<>();
        application.registerActivityLifecycleCallbacks(createActivityLifecycleCallbacks());
    }

    @Override
    protected void onInvoke(Response response) throws Throwable {

        final String command = response.getRequestPayload().optString("command", null);

        if (COMMAND_CREATE_BANNER_AD.equals(command)) {
            this.createBannerAd(response);
        } else if (COMMAND_GET_ADS.equals(command)) {
            this.getAds(response);
        } else if (COMMAND_REMOVE_AD.equals(command)) {
            this.removeAd(response);
        } else {
            response.setStatus(Response.STATUS_BAD_REQUEST);
            response.setBody(command + " is an unknown command.");
        }

        response.send();
    }

    private void createBannerAd(Response response) throws JSONException {

        final FrameLayout contentView = this.getCurrentContentView(response);
        if (contentView == null) {
            return;
        }

        final PublisherAdView adView = new PublisherAdView(contentView.getContext());
        final BannerAdIdentifier adIdentifier = BannerAdIdentifier.parseBannerAdIdentifier(
                response.getRequestPayload());
        final PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();

        adView.setAdSizes(parseBannerAdSizes(response.getRequestPayload()));
        adView.setAdUnitId(adIdentifier.getAdUnitId());
        adView.setLayoutParams(adIdentifier.getAnchor().createFrameLayoutLayoutParams());
        adView.setTag(adIdentifier);
        adView.getViewTreeObserver().addOnGlobalLayoutListener(createBannerGlobalLayoutListener(
                adView, adIdentifier));
        adView.loadAd(adRequest);

        contentView.addView(adView);
        this.ads.put(adView, adIdentifier);
    }

    private void getAds(Response response) {

        final JSONArray ads = new JSONArray();

        for (AdIdentifier adIdentifier : this.ads.values()) {
            ads.put(adIdentifier.toJSONObject());
        }

        response.setBody(ads.toString());
    }

    // TODO: fix affected margins
    private void removeAd(Response response) throws JSONException {

        final String adId = response.getRequestPayload().optString(KEY_AD_ID, null);
        final String adUnitId = response.getRequestPayload().optString(KEY_AD_UNIT_ID, null);
        final View viewToRemove = this.getAd(adId, adUnitId);

        if (viewToRemove == null) {
            response.setStatus(STATUS_AD_NOT_FOUND);
            response.setBody(String.format(
                    Locale.ROOT,
                    "Ad { ad_id=%s, ad_unit_id=%s } not found.",
                    adId,
                    adUnitId));
            return;
        }

        this.ads.remove(viewToRemove);

        final FrameLayout contentViewFrame = (FrameLayout) viewToRemove.getParent();
        if (contentViewFrame == null) {
            response.setStatus(STATUS_ALREADY_REMOVED);
            response.setBody(String.format(
                    Locale.ROOT,
                    "Ad { ad_id=%s, ad_unit_id=%s } already orphaned.",
                    adId,
                    adUnitId));
            return;
        }

        contentViewFrame.removeView(viewToRemove);
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

    private View getAd(String adId, String adUnitId) {

        // find ad
        for (Map.Entry<View, AdIdentifier> entry : this.ads.entrySet()) {
            final boolean adIdMatches = adId != null && adId.equals(entry.getValue().getAdId());
            final boolean adUnitIdMatches = adUnitId != null && adUnitId.equals(entry.getValue().getAdUnitId());

            if (adIdMatches || adUnitIdMatches) {
                return entry.getKey();
            }
        }

        return null;
    }

    private static AdSize[] parseBannerAdSizes(JSONObject payload) {

        final JSONArray adSizesArray = payload.optJSONArray(KEY_BANNER_AD_SIZES);
        if (adSizesArray == null || adSizesArray.length() == 0) {
            throw new IllegalArgumentException("banner_ad_sizes is missing values");
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

    private static ViewTreeObserver.OnGlobalLayoutListener createBannerGlobalLayoutListener(
            final View adView, final BannerAdIdentifier adIdentifier) {

        return new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                final int height = adView.getHeight();
                View contentView;

                if (height <= 0 || (contentView = this.getContentView()) == null) {
                    // Need a height and a child to modify.
                    return;
                }

                FrameLayout.LayoutParams contentViewLP = ((FrameLayout.LayoutParams) contentView.getLayoutParams());

                boolean needsNewLayout = false;

                // TODO: support existing margin
                switch (adIdentifier.getAnchor()) {
                    case TOP:
                        if (contentViewLP.topMargin != height) {
                            contentViewLP.topMargin = height;
                            needsNewLayout = true;
                        }
                        break;
                    case BOTTOM:
                        if (contentViewLP.bottomMargin != height) {
                            contentViewLP.bottomMargin = height;
                            needsNewLayout = true;
                        }
                        break;
                }

                if (needsNewLayout) {
                    // Resetting will inform the parent that new margins need to be drawn.
                    contentView.setLayoutParams(contentViewLP);
                }
            }

            private View getContentView() {

                FrameLayout contentViewParent = (FrameLayout) adView.getParent();
                if (contentViewParent == null) {
                    return null;
                }

                for (int i = 0; i < contentViewParent.getChildCount(); i++) {
                    View child = contentViewParent.getChildAt(i);
                    if (child.getTag() instanceof AdIdentifier) {
                        // it's an ad
                        continue;
                    }

                    return child;
                }

                return null;
            }
        };
    }
}
