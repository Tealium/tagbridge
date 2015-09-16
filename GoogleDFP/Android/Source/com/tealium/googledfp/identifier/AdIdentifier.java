package com.tealium.googledfp.identifier;

import com.google.android.gms.ads.AdListener;
import com.tealium.googledfp.GoogleDFPRemoteCommand;

import org.json.JSONException;
import org.json.JSONObject;

abstract class AdIdentifier {

    private final String adUnitId;
    private final String adId;
    private final AdListener adListener;
    private AdStatus status;

    public AdIdentifier(String adUnitId, String adId) {
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

    protected AdListener getAdListener() {
        return adListener;
    }

    public JSONObject toJSONObject() {
        try {
            final JSONObject obj = new JSONObject()
                    .put(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID, this.adUnitId)
                    .put("status", this.status);

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

        AdIdentifier that = (AdIdentifier) o;

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

    protected void adClosed() {

    }
}
