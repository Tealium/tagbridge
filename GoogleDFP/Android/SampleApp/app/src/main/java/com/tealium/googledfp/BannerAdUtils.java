package com.tealium.googledfp;

import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.tealium.googledfp.identifier.BannerAdIdentifier;

public final class BannerAdUtils {
    private BannerAdUtils() {
    }


    public static void resizeContentView(BannerAdIdentifier adIdentifier, PublisherAdView adView) {

        final int height = adView.getHeight();
        View contentView;

        if (height <= 0 || (contentView = getContentView(adView)) == null) {
            // Need a height and a child to modify.
            return;
        }

        FrameLayout.LayoutParams contentViewLP = ((FrameLayout.LayoutParams) contentView.getLayoutParams());

        boolean needsNewLayout = false;

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

    public static View getContentView(PublisherAdView adView) {
        FrameLayout contentViewParent = (FrameLayout) adView.getParent();
        if (contentViewParent == null) {
            return null;
        }

        for (int i = 0; i < contentViewParent.getChildCount(); i++) {
            View child = contentViewParent.getChildAt(i);
            if (child.getTag() instanceof BannerAdIdentifier) {
                // it's an ad
                continue;
            }

            return child;
        }

        return null;
    }
}
