package com.tealium.googledfp;

import com.google.android.gms.ads.AdListener;

import org.json.JSONException;
import org.json.JSONObject;

abstract class AdIdentifier {

    private enum Status {
        CREATED, CLOSED, FAILED_TO_LOAD, LEFT_APPLICATION, OPENED, LOADED;
    }

    private final String adUnitId;
    private final String adId;
    private final AdListener adListener;
    private Status status;

    public AdIdentifier(String adUnitId, String adId) {
        if ((this.adUnitId = adUnitId) == null) {
            throw new IllegalArgumentException();
        }
        this.adId = adId;
        this.adListener = createAdListener();
        this.status = Status.CREATED;
    }

    public final String getAdUnitId() {
        return adUnitId;
    }

    public final String getAdId() {
        return adId;
    }

    public final AdListener getAdListener() {
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

    private AdListener createAdListener() {
        return new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                status = Status.CLOSED;
                adClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                status = Status.FAILED_TO_LOAD;
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                status = Status.LEFT_APPLICATION;
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                status = Status.OPENED;
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                status = Status.LOADED;
            }
        };
    }

    protected void adClosed() {

    }
}
