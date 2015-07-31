package com.tealium.googledfp;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

final class AdConfiguration implements ViewTreeObserver.OnGlobalLayoutListener {

    enum Anchor {
        TOP, BOTTOM;
    }

    private final Anchor anchor;
    private final PublisherAdView adView;

    public AdConfiguration(PublisherAdView adView, Anchor anchor) {
        if ((this.anchor = anchor) == null ||
                (this.adView = adView) == null) {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public void onGlobalLayout() {

        final int height = this.adView.getHeight();
        View contentView;

        if (height <= 0 || (contentView = this.getContentView()) == null) {
            // Need a height and a child to modify.
            return;
        }

        FrameLayout.LayoutParams contentViewLP = ((FrameLayout.LayoutParams) contentView.getLayoutParams());

        boolean needsNewLayout = false;

        // TODO: support existing margin
        switch (this.anchor) {
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

        if(needsNewLayout) {
            // Resetting will inform the parent that new margins need to be drawn.
            contentView.setLayoutParams(contentViewLP);
        }
    }


    // Assuming single content view item
    private View getContentView() {

        FrameLayout contentView = (FrameLayout) this.adView.getParent();
        if (contentView == null) {
            return null;
        }

        for (int i = 0; i < contentView.getChildCount(); i++) {
            View child = contentView.getChildAt(i);
            if (child.getTag() instanceof AdConfiguration) {
                continue;
            }

            return child;
        }

        return null;
    }
}
