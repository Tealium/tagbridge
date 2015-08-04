package com.tealium.googledfp;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

import org.json.JSONException;
import org.json.JSONObject;

final class BannerAdIdentifier extends AdIdentifier {

    enum Anchor {
        TOP(Gravity.CENTER_HORIZONTAL | Gravity.TOP),
        BOTTOM(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        final int gravity;

        Anchor(int gravity) {
            this.gravity = gravity;
        }

        FrameLayout.LayoutParams createFrameLayoutLayoutParams() {
            final FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            fllp.gravity = this.gravity;

            return fllp;
        }
    }

    private final Anchor anchor;

    public BannerAdIdentifier(Anchor anchor, String adUnitId, String id) {
        super(adUnitId, id);

        if ((this.anchor = anchor) == null) {
            throw new IllegalArgumentException();
        }
    }

    public Anchor getAnchor() {
        return anchor;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            final JSONObject obj = new JSONObject()
                    .put(GoogleDFPRemoteCommand.KEY_BANNER_ANCHOR, anchor.toString())
                    .put(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID, this.getAdUnitId());

            if (this.getAdId() != null) {
                obj.put(GoogleDFPRemoteCommand.KEY_AD_ID, this.getAdId());
            }

            return obj;
        } catch (JSONException e) {
            throw new RuntimeException(e); // Should never happen.
        }
    }

    static BannerAdIdentifier parseBannerAdIdentifier(JSONObject payload) throws JSONException {
        final String adUnitId = payload.getString(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID);
        final String id = payload.optString(GoogleDFPRemoteCommand.KEY_AD_ID, null);
        final Anchor anchor = Anchor.valueOf(
                payload.getString(GoogleDFPRemoteCommand.KEY_BANNER_ANCHOR));

        return new BannerAdIdentifier(anchor, adUnitId, id);
    }
}
