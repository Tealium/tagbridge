package com.tealium.googledfp.identifier;

import android.view.Gravity;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.tealium.googledfp.BannerAdUtils;
import com.tealium.googledfp.GoogleDFPRemoteCommand;

import org.json.JSONException;
import org.json.JSONObject;

public final class BannerAdIdentifier extends AdIdentifier {

    public enum Anchor {
        TOP(Gravity.CENTER_HORIZONTAL | Gravity.TOP),
        BOTTOM(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        final int gravity;

        Anchor(int gravity) {
            this.gravity = gravity;
        }

        public FrameLayout.LayoutParams createFrameLayoutLayoutParams() {
            final FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            fllp.gravity = this.gravity;

            return fllp;
        }
    }

    private final Anchor anchor;

    /**
     * @param anchor   is null, defaults to {@link BannerAdIdentifier.Anchor#BOTTOM}
     * @param adUnitId non-null
     * @param id       id or null
     */
    public BannerAdIdentifier(Anchor anchor, String adUnitId, String id) {
        super(adUnitId, id);
        this.anchor = anchor == null ? Anchor.BOTTOM : anchor;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return super.toJSONObject()
                    .put(GoogleDFPRemoteCommand.KEY_BANNER_ANCHOR, anchor.toString())
                    .put("type", "BANNER");
        } catch (JSONException e) {
            throw new RuntimeException(e); // Should never happen.
        }
    }

    public AdListener getAdListener(final PublisherAdView adView) {

        final AdListener superAdListener = super.getAdListener();
        final BannerAdIdentifier adIdentifier = this;

        return new AdListener() {
            @Override
            public void onAdClosed() {
                superAdListener.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                superAdListener.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                superAdListener.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                superAdListener.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                superAdListener.onAdLoaded();
                BannerAdUtils.resizeContentView(adIdentifier, adView);
            }
        };
    }

    public static BannerAdIdentifier parseBannerAdIdentifier(JSONObject payload) throws JSONException {
        final String adUnitId = payload.getString(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID);
        final String id = payload.optString(GoogleDFPRemoteCommand.KEY_AD_ID, null);
        final Anchor anchor = Anchor.valueOf(
                payload.getString(GoogleDFPRemoteCommand.KEY_BANNER_ANCHOR));

        return new BannerAdIdentifier(anchor, adUnitId, id);
    }

}
