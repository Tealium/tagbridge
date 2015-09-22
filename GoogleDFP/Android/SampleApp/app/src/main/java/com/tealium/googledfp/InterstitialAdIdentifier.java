package com.tealium.googledfp;

import com.google.android.gms.ads.AdListener;

import org.json.JSONException;
import org.json.JSONObject;

final class InterstitialAdIdentifier {

    private final String adUnitId;
    private final String adId;
    private final AdListener adListener;
    private CloseListener closeListener;
    private AdStatus status;

    public InterstitialAdIdentifier(String adUnitId, String adId) {
        if ((this.adUnitId = adUnitId) == null) {
            throw new IllegalArgumentException();
        }
        this.adId = adId;
        this.adListener = createAdListener();
        this.status = AdStatus.CREATED;
    }

    public final String getAdUnitId() {
        return adUnitId;
    }

    public final String getAdId() {
        return adId;
    }

    public JSONObject toJSONObject() {
        try {
            final JSONObject obj = new JSONObject()
                    .put(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID, this.adUnitId)
                    .put("status", this.status)
                    .put("type", "INTERSTITIAL");

            if (this.adId != null) {
                obj.put(GoogleDFPRemoteCommand.KEY_AD_ID, this.adId);
            }

            return obj;
        } catch (JSONException e) {
            throw new RuntimeException(e);// Should never happen.
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterstitialAdIdentifier that = (InterstitialAdIdentifier) o;

        if (!adUnitId.equals(that.adUnitId)) return false;
        if (adId != null ? !adId.equals(that.adId) : that.adId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = adUnitId.hashCode();
        result = 31 * result + (adId != null ? adId.hashCode() : 0);
        return result;
    }

    private AdListener createAdListener() {
        return new AdListener() {

            @Override
            public void onAdClosed() {
                status = AdStatus.CLOSED;
                adClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                status = AdStatus.FAILED_TO_LOAD;
            }

            @Override
            public void onAdLeftApplication() {
                status = AdStatus.LEFT_APPLICATION;
            }

            @Override
            public void onAdOpened() {
                status = AdStatus.OPENED;
            }

            @Override
            public void onAdLoaded() {
                status = AdStatus.LOADED;
            }
        };
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    private void adClosed() {
        if (closeListener != null) {
            closeListener.onInterstitialAdClose(this);
        }
    }

    public AdListener getAdListener() {
        return this.adListener;
    }

    public static InterstitialAdIdentifier parseInterstitialAdIdentifier(JSONObject payload) throws JSONException {
        final String adUnitId = payload.getString(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID);
        final String id = payload.optString(GoogleDFPRemoteCommand.KEY_AD_ID, null);

        return new InterstitialAdIdentifier(adUnitId, id);
    }

    public interface CloseListener {
        void onInterstitialAdClose(InterstitialAdIdentifier identifier);
    }
}
