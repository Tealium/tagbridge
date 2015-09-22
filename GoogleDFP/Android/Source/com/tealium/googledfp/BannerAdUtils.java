package com.tealium.googledfp;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.tealium.sampleapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

final class BannerAdUtils {
    private BannerAdUtils() {
    }

    public static void resizeContentView(Anchor anchor, PublisherAdView adView) {

        final int height = adView.getHeight();
        View contentView;

        if (height <= 0 || (contentView = getContentView(adView)) == null) {
            // Need a height and a child to modify.
            return;
        }

        FrameLayout.LayoutParams contentViewLP = ((FrameLayout.LayoutParams) contentView.getLayoutParams());

        boolean needsNewLayout = false;

        switch (anchor) {
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


    public static View getContentView(PublisherAdView adView) {
        FrameLayout contentViewParent = (FrameLayout) adView.getParent();
        if (contentViewParent == null) {
            return null;
        }

        for (int i = 0; i < contentViewParent.getChildCount(); i++) {
            View child = contentViewParent.getChildAt(i);
            if (child instanceof PublisherAdView) {
                // it's an ad
                continue;
            }

            return child;
        }

        return null;
    }

    static AdListener createAdListener(final Anchor anchor, final PublisherAdView adView) {
        return new AdListener() {
            @Override
            public void onAdLoaded() {
                resizeContentView(anchor, adView);
                adView.setAdListener(null);
                if (BuildConfig.DEBUG) {
                    Log.d(GoogleDFPRemoteCommand.TAG, "Injected loaded Ad.");
                }
            }
        };
    }

    static AdSize[] parseBannerAdSizes(JSONObject payload) {

        final JSONArray adSizesArray = payload.optJSONArray(GoogleDFPRemoteCommand.KEY_BANNER_AD_SIZES);
        if (adSizesArray == null || adSizesArray.length() == 0) {
            throw new IllegalArgumentException("banner_ad_sizes is missing values");
        }

        final AdSize[] adSizes = new AdSize[adSizesArray.length()];
        String adSizeValue;

        for (int i = 0; i < adSizesArray.length(); i++) {
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

    static boolean orphanBannerAd(Activity activity, String adUnitId) {
        final FrameLayout contentViewFrame = getContentViewFrame(activity);
        if (contentViewFrame == null) {
            return false;
        }

        PublisherAdView adViewToRemove = null;
        View contentView = null;

        for (int i = 0; i < contentViewFrame.getChildCount(); i++) {
            final View child = contentViewFrame.getChildAt(i);

            if (child instanceof PublisherAdView) {
                if (((PublisherAdView) child).getAdUnitId().equals(adUnitId)) {
                    adViewToRemove = (PublisherAdView) child;
                }
            } else {
                contentView = child;
            }
        }

        if (adViewToRemove == null) {
            return false;
        }

        contentViewFrame.removeView(adViewToRemove);

        if (contentView != null) {
            final FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) contentView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            layoutParams.topMargin = 0;
            contentView.setLayoutParams(layoutParams);
        }

        return true;
    }

    static FrameLayout getContentViewFrame(Activity activity) {

        if (activity == null) {
            throw new IllegalArgumentException();
        }

        final View contentView = activity.findViewById(android.R.id.content);
        if (!(contentView instanceof FrameLayout)) {
            return null;
        }

        return (FrameLayout) contentView;
    }
}
