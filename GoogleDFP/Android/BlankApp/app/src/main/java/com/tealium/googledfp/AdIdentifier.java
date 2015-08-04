package com.tealium.googledfp;

import org.json.JSONObject;

abstract class AdIdentifier {

    private final String adUnitId;
    private final String adId;

    public AdIdentifier(String adUnitId, String adId) {
        if ((this.adUnitId = adUnitId) == null) {
            throw new IllegalArgumentException();
        }
        this.adId = adId;
    }

    public String getAdUnitId() {
        return adUnitId;
    }

    public String getAdId() {
        return adId;
    }

    public abstract JSONObject toJSONObject();
}
