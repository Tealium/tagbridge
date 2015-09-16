package com.tealium.googledfp;

import android.app.Activity;
import android.app.Application;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.tealium.googledfp.identifier.BannerAdIdentifier;
import com.tealium.googledfp.identifier.InterstitialAdIdentifier;
import com.tealium.library.RemoteCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The TagBridge module for Google Play Service's Ads.
 * <p/>
 * The following dependencies will need to be added to the build.gradle file:
 * <p/>
 * <pre>
 *     compile 'com.google.android.gms:play-services-ads:7.5.0'
 *     compile files('libs/tealium.4.1.3c.jar')
 * </pre>
 * <p/>
 * Once added, add a new instance of this module to {@link com.tealium.library.Tealium.Config#addRemoteCommand(RemoteCommand)}.
 * <p/>
 * Minimum supported API: 14
 */
public final class GoogleDFPRemoteCommand extends RemoteCommand implements
        ActivityLifecycleCallbacks.VisiblityListener, InterstitialAdIdentifier.CloseListener {

    public static final String KEY_AD_UNIT_ID = "ad_unit_id";
    public static final String KEY_AD_ID = "ad_id";
    public static final String KEY_BANNER_ANCHOR = "banner_anchor";
    static final String KEY_BANNER_AD_SIZES = "banner_ad_sizes";
    static final String KEY_CUSTOM_TARGETING = "custom_targeting";
    static final String KEY_KEYWORDS = "keywords";
    static final String KEY_CATEGORY_EXCLUSIONS = "category_exclusions";
    static final String KEY_REQUEST_AGENT = "request_agent";
    static final String KEY_LOCATION = "location";
    static final String KEY_GENDER = "gender";
    static final String KEY_BIRTHDAY = "birthday";
    static final String KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT = "tag_for_child_directed_treatment";
    static final String KEY_MANUAL_IMPRESSIONS = "manual_impressions";
    static final String KEY_PUBLISHER_PROVIDED_ID = "publisher_provided_id";
    static final String KEY_TEST_DEVICES = "test_devices";

    private static final String COMMAND_CREATE_BANNER_AD = "create_banner_ad";
    private static final String COMMAND_CREATE_INTERSTITIAL_AD = "create_interstitial_ad";
    private static final String COMMAND_SHOW_INTERSTITIAL_AD = "show_interstitial_ad";
    private static final String COMMAND_GET_ADS = "get_ads";
    private static final String COMMAND_REMOVE_AD = "remove_ad";

    private static final int STATUS_NO_VIEW = 418;
    private static final int STATUS_INCOMPATIBLE = 419;
    private static final int STATUS_UNABLE_TO_REMOVE = 420;
    private static final int STATUS_AD_NOT_FOUND = 421;
    private static final int STATUS_AD_NOT_READY = 422;

    private final Map<BannerAdIdentifier, PublisherAdView> bannerAds;
    private final Map<InterstitialAdIdentifier, PublisherInterstitialAd> interstitialAds;
    private WeakReference<Activity> currentActivity;

    public GoogleDFPRemoteCommand(Application application) {
        super("google_dfp", "Google DFP");

        this.bannerAds = new HashMap<>();
        this.interstitialAds = new HashMap<>();

        ActivityLifecycleCallbacks.register(application, this);
    }

    @Override
    protected void onInvoke(Response response) throws Throwable {

        final String command = response.getRequestPayload().optString("command", null);

        if (COMMAND_CREATE_BANNER_AD.equals(command)) {
            this.createBannerAd(response);
        } else if (COMMAND_CREATE_INTERSTITIAL_AD.equals(command)) {
            this.createInterstitialAd(response);
        } else if (COMMAND_SHOW_INTERSTITIAL_AD.equals(command)) {
            this.showInterstitialAd(response);
        } else if (COMMAND_GET_ADS.equals(command)) {
            this.getAds(response);
        } else if (COMMAND_REMOVE_AD.equals(command)) {
            this.removeAd(response);
        } else {
            response.setStatus(Response.STATUS_BAD_REQUEST);
            response.setBody(command + " is an unknown command.");
        }

        response.send();
    }

    @Override
    public void onActivityResume(Activity activity) {
        this.currentActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPause(Activity activity) {

        this.currentActivity = null;
    }

    @Override
    public void onInterstitialAdClose(InterstitialAdIdentifier identifier) {
        identifier.setCloseListener(null);
        this.interstitialAds.remove(identifier);
    }

    private void createBannerAd(Response response) throws JSONException {

        final FrameLayout contentViewFrame = this.getCurrentContentView(response);
        if (contentViewFrame == null) {
            return;
        }

        final BannerAdIdentifier adIdentifier = BannerAdIdentifier.parseBannerAdIdentifier(
                response.getRequestPayload());

        PublisherAdView adView = this.bannerAds.get(adIdentifier);
        if (adView != null) {
            if (contentViewFrame == adView.getParent()) {
                return;
            }

            this.orphanBannerAd(adView);
            // Resets
            adView.setAdSizes(adView.getAdSizes());
            contentViewFrame.addView(adView);
            BannerAdUtils.resizeContentView(adIdentifier, adView);
            return;
        }

        adView = new PublisherAdView(contentViewFrame.getContext().getApplicationContext());
        final PublisherAdRequest adRequest = parsePublisherAdRequest(response.getRequestPayload());

        adView.setAdSizes(parseBannerAdSizes(response.getRequestPayload()));
        adView.setAdUnitId(adIdentifier.getAdUnitId());
        adView.setLayoutParams(adIdentifier.getAnchor().createFrameLayoutLayoutParams());
        adView.setAdListener(adIdentifier.getAdListener(adView));
        adView.loadAd(adRequest);
        this.bannerAds.put(adIdentifier, adView);

        contentViewFrame.addView(adView);
    }

    private void createInterstitialAd(Response response) throws JSONException {

        final Activity activity = this.getCurrentActivity();
        if (activity == null) {
            response.setStatus(STATUS_NO_VIEW);
            response.setBody("There is no visible activity.");
            return;
        }

        final InterstitialAdIdentifier adIdentifier = InterstitialAdIdentifier.parseInterstitialAdIdentifier(
                response.getRequestPayload());

        if (this.interstitialAds.containsKey(adIdentifier)) {
            throw new IllegalArgumentException("An ad with ad_id=" + adIdentifier.getAdId() + " already exists.");
        }

        adIdentifier.setCloseListener(this);
        final PublisherInterstitialAd ad = new PublisherInterstitialAd(activity.getApplicationContext());
        final PublisherAdRequest adRequest = parsePublisherAdRequest(response.getRequestPayload());

        ad.setAdUnitId(adIdentifier.getAdUnitId());
        ad.setAdListener(adIdentifier.getAdListener());
        ad.loadAd(adRequest);

        this.interstitialAds.put(adIdentifier, ad);
    }

    private void showInterstitialAd(Response response) throws JSONException {
        final InterstitialAdIdentifier adIdentifier = InterstitialAdIdentifier
                .parseInterstitialAdIdentifier(response.getRequestPayload());

        final PublisherInterstitialAd interstitialAd = this.interstitialAds.get(adIdentifier);

        if (interstitialAd == null) {
            response.setStatus(STATUS_AD_NOT_FOUND);
            response.setBody(String.format(
                    Locale.ROOT,
                    "Ad { ad_id=%s, ad_unit_id=%s } not found.",
                    adIdentifier.getAdId(),
                    adIdentifier.getAdUnitId()));
            return;
        }

        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            return;
        }

        response.setStatus(STATUS_AD_NOT_READY);
        response.setBody(String.format(
                Locale.ROOT,
                "Ad { ad_id=%s, ad_unit_id=%s } not ready to show.",
                adIdentifier.getAdId(),
                adIdentifier.getAdUnitId()));
    }

    private void getAds(Response response) throws JSONException {

        final JSONArray ads = new JSONArray();
        final Activity visibleActivity = this.getCurrentActivity();

        for (Map.Entry<BannerAdIdentifier, PublisherAdView> entry : this.bannerAds.entrySet()) {
            final BannerAdIdentifier adIdentifier = entry.getKey();
            final PublisherAdView adView = entry.getValue();
            final View parentView = (View) adView.getParent();

            final boolean hasParent = parentView != null;
            final boolean belongsToCurrentActivity = parentView.getContext().equals(visibleActivity);

            ads.put(adIdentifier.toJSONObject()
                    .put("is_visible", hasParent && belongsToCurrentActivity));
        }

        for (InterstitialAdIdentifier adIdentifier : this.interstitialAds.keySet()) {
            ads.put(adIdentifier.toJSONObject());
        }

        response.setBody(ads.toString());
    }

    private void removeAd(Response response) throws JSONException {

        final String adId = response.getRequestPayload().optString(KEY_AD_ID, null);
        final String adUnitId = response.getRequestPayload().optString(KEY_AD_UNIT_ID, null);

        if (removeInterstitialAds(adId, adUnitId)) {
            return;
        }

        if (removeBannerAds(adId, adUnitId)) {
            return;
        }

        response.setStatus(STATUS_UNABLE_TO_REMOVE);
        response.setBody(String.format(
                Locale.ROOT,
                "Unable to remove ad { ad_id=%s, ad_unit_id=%s }.",
                adId,
                adUnitId));
    }

    private boolean removeInterstitialAds(String adId, String adUnitId) {

        boolean removed = false;

        for (InterstitialAdIdentifier adIdentifier : this.interstitialAds.keySet()) {
            if (TextUtils.equals(adId, adIdentifier.getAdId()) ||
                    TextUtils.equals(adUnitId, adIdentifier.getAdUnitId())) {
                removed = this.interstitialAds.remove(adIdentifier) != null || removed;
            }
        }

        return removed;
    }

    private boolean removeBannerAds(String adId, String adUnitId) {

        boolean removed = false;

        for (BannerAdIdentifier adIdentifier : this.bannerAds.keySet()) {
            if (TextUtils.equals(adId, adIdentifier.getAdId()) ||
                    TextUtils.equals(adUnitId, adIdentifier.getAdUnitId())) {

                final PublisherAdView adView = this.bannerAds.remove(adIdentifier);
                removed = adView != null || removed;
                this.orphanBannerAd(adView);
            }
        }

        return removed;
    }

    private boolean orphanBannerAd(PublisherAdView adView) {

        if (adView == null) {
            return false;
        }

        final View contentView = BannerAdUtils.getContentView(adView);
        if (contentView != null) {

            boolean needsNewLayout = false;

            // Reset margin.
            final FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) contentView.getLayoutParams();
            layoutParams.bottomMargin = 0;
            layoutParams.topMargin = 0;

            if (needsNewLayout) {
                contentView.setLayoutParams(layoutParams);
            }
        }


        final FrameLayout contentViewFrame = (FrameLayout) adView.getParent();
        if (contentViewFrame == null) {
            return false;
        }

        contentViewFrame.removeView(adView);
        return true;
    }

    private FrameLayout getCurrentContentView(Response response) {
        final Activity activity = this.getCurrentActivity();

        if (activity == null) {
            response.setStatus(STATUS_NO_VIEW);
            response.setBody("There is no visible activity.");
            return null;
        }

        final View contentView = activity.findViewById(android.R.id.content);
        if (!(contentView instanceof FrameLayout)) {
            response.setStatus(STATUS_INCOMPATIBLE);
            response.setBody("This view is incompatible for ad display");
            return null;
        }

        return (FrameLayout) contentView;
    }

    private Activity getCurrentActivity() {
        return this.currentActivity == null ? null : this.currentActivity.get();
    }

    private static AdSize[] parseBannerAdSizes(JSONObject payload) {

        final JSONArray adSizesArray = payload.optJSONArray(KEY_BANNER_AD_SIZES);
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

    private static PublisherAdRequest parsePublisherAdRequest(JSONObject payload) throws JSONException {
        final PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();

        extractCustomTargeting(builder, payload);
        extractKeywords(builder, payload);
        extractCategoryExclusions(builder, payload);
        extractLocation(builder, payload);
        extractGender(builder, payload);
        extractTestDevices(builder, payload);

        final String requestAgent = payload.optString(KEY_REQUEST_AGENT, null);
        if (requestAgent != null) {
            builder.setRequestAgent(requestAgent);
        }

        final long birthday = payload.optLong(KEY_BIRTHDAY, Long.MIN_VALUE);
        if (birthday != Long.MIN_VALUE) {
            builder.setBirthday(new Date(birthday));
        }

        final String publisherProvidedId = payload.optString(KEY_PUBLISHER_PROVIDED_ID, null);
        if (publisherProvidedId != null) {
            builder.setPublisherProvidedId(publisherProvidedId);
        }

        if (payload.has(KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT)) {
            builder.tagForChildDirectedTreatment(payload.getBoolean(KEY_TAG_FOR_CHILD_DIRECTED_TREATMENT));
        }

        if (payload.has(KEY_MANUAL_IMPRESSIONS)) {
            builder.setManualImpressionsEnabled(payload.getBoolean(KEY_MANUAL_IMPRESSIONS));
        }

        return builder.build();
    }

    private static void extractCustomTargeting(final PublisherAdRequest.Builder builder, JSONObject payload) throws JSONException {
        final JSONObject customTargeting = payload.optJSONObject(KEY_CUSTOM_TARGETING);
        if (customTargeting == null) {
            return;
        }
        Iterator<String> keys = customTargeting.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = customTargeting.get(key);
            if (value instanceof JSONArray) {
                final JSONArray array = (JSONArray) value;
                List<String> values = new ArrayList<>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    values.add(array.getString(i));
                }
                builder.addCustomTargeting(key, values);
            } else {
                builder.addCustomTargeting(key, value.toString());
            }
        }
    }

    private static void extractKeywords(final PublisherAdRequest.Builder builder, JSONObject payload) throws JSONException {
        final JSONArray keywords = payload.optJSONArray(KEY_KEYWORDS);
        if (keywords == null) {
            return;
        }
        for (int i = 0; i < keywords.length(); i++) {
            builder.addKeyword(keywords.getString(i));
        }
    }

    private static void extractCategoryExclusions(final PublisherAdRequest.Builder builder, JSONObject payload) throws JSONException {
        final JSONArray exclusions = payload.optJSONArray(KEY_CATEGORY_EXCLUSIONS);
        if (exclusions == null) {
            return;
        }
        for (int i = 0; i < exclusions.length(); i++) {
            builder.addCategoryExclusion(exclusions.getString(i));
        }
    }

    private static void extractLocation(final PublisherAdRequest.Builder builder, JSONObject payload) throws JSONException {
        final JSONObject locationInfo = payload.optJSONObject(KEY_LOCATION);
        if (locationInfo == null) {
            return;
        }

        final Location location = new Location("");
        location.setAccuracy(100);
        location.setLatitude(locationInfo.getDouble("latitude"));
        location.setLongitude(locationInfo.getDouble("longitude"));
        builder.setLocation(location);
    }

    private static void extractGender(final PublisherAdRequest.Builder builder, JSONObject payload) {
        final String gender = payload.optString(KEY_GENDER, null);
        if ("MALE".equals(gender)) {
            builder.setGender(com.google.android.gms.ads.AdRequest.GENDER_MALE);
        } else if ("FEMALE".equals(gender)) {
            builder.setGender(com.google.android.gms.ads.AdRequest.GENDER_FEMALE);
        } else if ("UNKNOWN".equals(gender)) {
            builder.setGender(com.google.android.gms.ads.AdRequest.GENDER_UNKNOWN);
        }
    }

    private static void extractTestDevices(final PublisherAdRequest.Builder builder, JSONObject payload) throws JSONException {
        final JSONArray testDevices = payload.optJSONArray(KEY_TEST_DEVICES);
        if (testDevices == null) {
            return;
        }
        for (int i = 0; i < testDevices.length(); i++) {
            final String testDevice = testDevices.getString(i);
            if (testDevice.equals("DEVICE_ID_EMULATOR")) {
                builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            } else {
                builder.addTestDevice(testDevice);
            }
        }
    }
}
