package com.tealium.googledfp;

import org.json.JSONException;
import org.json.JSONObject;

final class InterstitialAdIdentifier extends AdIdentifier {

    public InterstitialAdIdentifier(String adUnitId, String adId) {
        super(adUnitId, adId);
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return super.toJSONObject().put("type", "INTERSTITIAL");
        } catch (JSONException e) {
            throw new RuntimeException(e); // Should never happen.
        }
    }

    static InterstitialAdIdentifier parseInterstitialAdIdentifier(JSONObject payload) throws JSONException {
        final String adUnitId = payload.getString(GoogleDFPRemoteCommand.KEY_AD_UNIT_ID);
        final String id = payload.optString(GoogleDFPRemoteCommand.KEY_AD_ID, null);

        return new InterstitialAdIdentifier(adUnitId, id);
    }
}
