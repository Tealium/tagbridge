package com.tealium.tagbridge.artisan;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.artisan.activity.ArtisanActivity;
import com.artisan.application.ArtisanRegisteredApplication;
import com.artisan.incodeapi.ArtisanLocationValue;
import com.artisan.incodeapi.ArtisanProfileManager;
import com.artisan.manager.ArtisanConfigOption;
import com.artisan.manager.ArtisanManager;
import com.artisan.manager.ArtisanManagerCallback;
import com.artisan.services.ArtisanBoundActivity;
import com.artisan.services.ArtisanService;
import com.tealium.library.Key;
import com.tealium.library.RemoteCommand;
import com.tealium.library.Tealium;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Module for incorporating Artisan Android SDK into TagBridge
 * 4.1
 *
 * @version 1.0
 * @since 2015-04-10
 */
public final class TealiumArtisanMobileTagBridge extends RemoteCommand implements ArtisanRegisteredApplication, ArtisanBoundActivity {

    private static final String TAG = "Tealium-Artisan";

    // Exposed to in-package visibility for testing convenience.
    static interface ArtisanMethod {
        void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args);
    }

    @Override
    public ArtisanService getArtisanService() {
        return ArtisanActivity._getArtisanService();
    }

    @Override
    public void registerPowerhooks() {

    }

    @Override
    public void registerInCodeExperiments() {

    }

    @Override
    public void registerUserProfileVariables() {

    }

    // I don't understand how this could work.
    private static class TealiumArtisanApp extends Application {
        private static Context context;

        public void onCreate() {
            super.onCreate();
            TealiumArtisanApp.context = getApplicationContext();
        }

        public static Context getAppContext() {
            return TealiumArtisanApp.context;
        }
    }

    private static final String MSG_ARGS_MISSING = "\"arguments\" must not be null.";
    private static final String KEY_METHOD = "method";
    private static final String KEY_ARGS = "arguments";
    // TODO private static final boolean DEBUG = true;
    // TODO private static final String TAG = "Tealium-artisan_xx.xx";

    private final Map<String, ArtisanMethod> methods;
    private boolean initialized;
    // Shouldn't be statically held, this uses up unnecessary memory, and can lead to bugs.
    private static Response responseObject;

    /**
     * @param context used to initialize the Adobe config.
     * @throws IllegalArgumentException when context is null.
     */
    public TealiumArtisanMobileTagBridge(Context context) {
        super("artisan", "Module for incorporating Artisan SDK");

        if (context == null) {
            throw new IllegalArgumentException("context must not be null.");
        }

        // What's the Context being used for?

        this.initialized = false;

        this.methods = new HashMap<String, ArtisanMethod>(9);

        /** MODIFY
         * Add method mapping here
         * this.methods.put("js_method_name", javaMethod());
         */
        this.methods.put("start", start());
        this.methods.put("has_first_playlist_downloaded", hasFirstPlaylistDownloaded());
        this.methods.put("on_first_playlist_downloaded", onFirstPlaylistDownloaded());
        this.methods.put("on_create", onCreate());
        this.methods.put("on_start", onStart());

        this.methods.put("on_stop", onStop());
        this.methods.put("on_destroy", onDestroy());
        this.methods.put("disable_auto_event_collection", disableAutoEventCollection());
        this.methods.put("profile_manager", profileManager());

    }

    @Override
    protected void onInvoke(Response response) throws Throwable {
        setResponse(response);
        if (!initialized) {
            // Initialization seems useless.
            //Config.setContext(context);
            initialized = true;
        }

        final JSONObject payload = response.getRequestPayload();
        final String methodName = payload.optString(KEY_METHOD, null);
        final JSONObject args = payload.optJSONObject(KEY_ARGS);

        if (methodName == null) {
            response.setStatus(Response.STATUS_BAD_REQUEST);
            response.setBody("no method was specified.");
            response.send();
            return;
        }

        try {
            this.invokeMethod(methodName, args);
            response.send();// OK by default.
        } catch (IllegalArgumentException e) {
            response.setStatus(Response.STATUS_BAD_REQUEST);
            response.setBody(String.format(
                    Locale.ROOT,
                    "Failed to perform method \"%s\", reason: %s",
                    methodName,
                    e.getMessage()));
            response.send();
        }
    }

    // Moved to test.
    private void invokeMethod(String methodName, JSONObject args) throws Throwable {

        final ArtisanMethod method = this.methods.get(methodName);
        if (method == null) {
            throw new IllegalArgumentException(String.format(
                    Locale.ROOT,
                    "method with name \"%s\" was not found.",
                    methodName));
        }

        method.invoke(this, args);
    }

    /**
     * Converts a JSONObject to a Map.
     *
     * @param jsonObject if null, returns null; otherwise, iterates through the
     *                   JSONObject performing put operations into the new map.
     * @return null or populated map.
     */
    private static HashMap<String, Object> jsonObjectToMap(JSONObject jsonObject) {

        // This method is unused

        if (jsonObject == null) {
            return null;
        }

        HashMap<String, Object> map = new HashMap<String, Object>(jsonObject.length());
        Iterator<String> keys = jsonObject.keys();
        String key;

        while (keys.hasNext()) {
            key = keys.next();
            map.put(key, jsonObject.opt(key));
        }

        return map;
    }

    /**
     * Extracts a string from the given JSONObject. Throws an
     * IllegalArgumentException if it's missing.
     *
     * @param args source containing the value for the key.
     * @param key  key to fetch the value from the args.
     * @throws IllegalArgumentException if key is missing.
     */
    private static String extractString(JSONObject args, String key) {
        final String value = args.optString(key, null);
        if (value == null) {
            //Log.w("Tealium:TagBridge:Artisan", String.format(Locale.ROOT, "\"%s\" is missing from \"arguments\". If overload is available, this may not be an issue", key));
            // Tag too long, should be a constant, and colons not ok
            Log.w(TAG, String.format(Locale.ROOT, "\"%s\" is missing from \"arguments\". If overload is available, this may not be an issue", key));
            return null;
        }
        return value;
    }

    // Method is superflous (and unused) see http://developer.android.com/reference/org/json/JSONObject.html#getDouble%28java.lang.String%29
    private static double extractDouble(JSONObject args, String key) {


        final String value = args.optString(key, null);

        if (value == null) {
            throw new IllegalArgumentException(String.format(
                    Locale.ROOT,
                    "\"%s\" missing from \"arguments\".",
                    key));
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    Locale.ROOT,
                    "\"%s\" is not able to be converted into a double.",
                    value));
        }
    }

    private static void setResponse(Response response) {
        responseObject = response;
    }

    private static Response getResponse() {
        return responseObject;
    }

    private static ArtisanMethod start() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                String appID = extractString(args, Key.APP_ID /* Should use constants when available "app_id"*/);
                String configOptionsString = extractString(args, "start_config_options");
                if (configOptionsString != null) {
                    try {
                        // Param is unused
                        JSONObject configOptionsJson = new JSONObject(configOptionsString);
                        ArtisanConfigOption artisanConfigOption = new ArtisanConfigOption();
                        //TODO: map all keys to config options
                        ArtisanManager.startArtisan(artisanApplication, appID, artisanConfigOption);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ArtisanManager.startArtisan(artisanApplication, appID);
                    }
                } else {
                    ArtisanManager.startArtisan(artisanApplication, appID);
                }
            }
        };
    }

    private static ArtisanMethod onCreate() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                ArtisanActivity.artisanOnCreate((Activity) TealiumArtisanApp.getAppContext());
            }
        };
    }

    private static ArtisanMethod onStart() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                ArtisanActivity.artisanOnStart((Activity) TealiumArtisanApp.getAppContext());
            }
        };
    }

    private static ArtisanMethod onStop() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                ArtisanActivity.artisanOnStop((Activity) TealiumArtisanApp.getAppContext());
            }
        };
    }

    private static ArtisanMethod onDestroy() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                ArtisanActivity.artisanOnDestroy();
            }
        };
    }

    private static ArtisanMethod disableAutoEventCollection() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                ArtisanManager.disableAutoEventCollection();
            }
        };
    }

    private static ArtisanMethod profileManager() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                String profileOptions = extractString(args, "profile");
                JSONObject profileOptionsJson;
                if (profileOptions != null) {
                    try {
                        profileOptionsJson = new JSONObject(profileOptions);
                        if (profileOptionsJson.length() <= 0) {
                            return;
                        }
                        Iterator<?> keys = profileOptionsJson.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            Map<String, String> arguments = new HashMap<>();
                            if (profileOptionsJson.get(key) instanceof String) {
                                arguments.put("arg", profileOptionsJson.optString(key));
                            } else if (profileOptionsJson.get(key) instanceof JSONObject) {
                                Iterator<?> subKeys = profileOptionsJson.optJSONObject(key).keys();
                                JSONObject value = profileOptionsJson.optJSONObject(key);
                                while (subKeys.hasNext()) {
                                    String subKey = (String) subKeys.next();
                                    if (value.get(subKey) instanceof String) {
                                        arguments.put(subKey, value.optString(subKey));
                                    }
                                }
                            } else {
                                continue;
                            }
                            setProfileMethod(key, arguments);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void setProfileMethod(String profileMethod, Map<String, String> arguments) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date;
                String string;
                String number;
                String stringValue;
                Number numberValue;
                String dateTime;
                String location;

                switch (profileMethod) {
                    case "clearProfile":
                        ArtisanProfileManager.clearProfile();
                        break;

                    case "clearVariable":
                        ArtisanProfileManager.clearVariableValue(arguments.get("arg"));
                        break;

                    case "setGender":
                        String genderString = arguments.get("arg");
                        switch (genderString) {
                            case "male":
                                ArtisanProfileManager.setGender(ArtisanProfileManager.Gender.Male);
                                break;
                            case "female":
                                ArtisanProfileManager.setGender(ArtisanProfileManager.Gender.Female);
                                break;
                            default:
                                ArtisanProfileManager.setGender(ArtisanProfileManager.Gender.NA);
                                break;
                        }
                        break;

                    case "setUserAge":
                        Integer age = Integer.parseInt(arguments.get("arg"));
                        if (age > 0) {
                            ArtisanProfileManager.setUserAge(age);
                        }
                        break;

                    case "setUserPrefix":
                        ArtisanProfileManager.setUserPrefix(arguments.get("arg"));
                        break;

                    case "setUserFirstName":
                        ArtisanProfileManager.setUserFirstName(arguments.get("arg"));
                        break;

                    case "setUserMiddleName":
                        ArtisanProfileManager.setUserMiddleName(arguments.get("arg"));
                        break;

                    case "setUserLastName":
                        ArtisanProfileManager.setUserLastName(arguments.get("arg"));
                        break;

                    case "setUserSuffix":
                        ArtisanProfileManager.setUserSuffix(arguments.get("arg"));
                        break;

                    case "setUserReferralSource":
                        ArtisanProfileManager.setUserReferralSource(arguments.get("arg"));
                        break;

                    case "setFirstSeen":
                        try {
                            date = format.parse(arguments.get("arg"));
                            ArtisanProfileManager.setUserFirstSeen(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "setSignUpDate":
                        try {
                            date = format.parse(arguments.get("arg"));
                            ArtisanProfileManager.setUserSignUpDate(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "setUserStreetAddress":
                        ArtisanProfileManager.setUserStreetAddress(arguments.get("arg"));
                        break;

                    case "setUserStreetAddress2":
                        ArtisanProfileManager.setUserStreetAddress2(arguments.get("arg"));
                        break;

                    case "setUserCompanyName":
                        ArtisanProfileManager.setUserCompany(arguments.get("arg"));
                        break;

                    case "setUserCity":
                        ArtisanProfileManager.setUserCity(arguments.get("arg"));
                        break;

                    case "setUserStateProvince":
                        ArtisanProfileManager.setUserStateProvince(arguments.get("arg"));
                        break;

                    case "setUserPostalCode":
                        ArtisanProfileManager.setUserPostalCode(arguments.get("arg"));
                        break;

                    case "setUserCountry":
                        ArtisanProfileManager.setUserCountry(arguments.get("arg"));
                        break;

                    case "setUserAvatarUrl":
                        ArtisanProfileManager.setUserAvatarURL(arguments.get("arg"));
                        break;

                    case "setUserFacebook":
                        ArtisanProfileManager.setUserFacebook(arguments.get("arg"));
                        break;

                    case "setUserTwitterName":
                        ArtisanProfileManager.setUserTwitterName(arguments.get("arg"));
                        break;

                    case "setUserUrl":
                        ArtisanProfileManager.setUserUrl(arguments.get("arg"));
                        break;

                    case "setUserPhoneNumber":
                        ArtisanProfileManager.setUserPhoneNumber(arguments.get("arg"));
                        break;

                    case "setUserEmail":
                        ArtisanProfileManager.setUserEmail(arguments.get("arg"));
                        break;

                    case "setOptedOutEmail":
                        ArtisanProfileManager.setOptedOutOfEmail(Boolean.parseBoolean(arguments.get("arg")));
                        break;

                    case "setOptedOutPush":
                        ArtisanProfileManager.setOptedOutOfPush(Boolean.parseBoolean(arguments.get("arg")));
                        break;

                    case "setOptedOutText":
                        ArtisanProfileManager.setOptedOutOfText(Boolean.parseBoolean(arguments.get("arg")));
                        break;

                    case "setSharedUserId":
                        ArtisanProfileManager.setSharedUserId(arguments.get("arg"));
                        break;

                    case "registerNumber":
                        number = arguments.get("number");
                        if (arguments.get("value") != null) {
                            numberValue = Long.parseLong(arguments.get("value"));
                            ArtisanProfileManager.registerNumber(number, numberValue);
                        } else {
                            ArtisanProfileManager.registerNumber(number);
                        }
                        break;

                    case "registerString":
                        string = arguments.get("string");
                        stringValue = arguments.get("value");
                        if (stringValue != null) {
                            ArtisanProfileManager.registerString(string, stringValue);
                        } else {
                            ArtisanProfileManager.registerString(string);
                        }
                        break;

                    case "setNumberValue":
                        number = arguments.get("variableName");
                        numberValue = Long.parseLong(arguments.get("numberValue"));
                        ArtisanProfileManager.setNumberValue(number, numberValue);
                        break;

                    case "setStringValue":
                        string = arguments.get("variableName");
                        stringValue = arguments.get("stringValue");
                        ArtisanProfileManager.setStringValue(string, stringValue);
                        break;

                    case "registerDateTime":
                        dateTime = arguments.get("dateTime");
                        if (arguments.get("value") != null) {
                            try {
                                date = format.parse(arguments.get("value"));
                                ArtisanProfileManager.registerDateTime(dateTime, date);
                            } catch (ParseException e) {
                                // Not useful in android
                                e.printStackTrace();
                                ArtisanProfileManager.registerDateTime(dateTime);
                            }

                        } else {
                            ArtisanProfileManager.registerDateTime(dateTime);
                        }
                        break;

                    case "registerLocation":
                        location = arguments.get("location");
                        if (arguments.get("lat") != null && arguments.get("long") != null) {
                            ArtisanLocationValue locationValue = new ArtisanLocationValue();
                            locationValue.setLatitude(Double.parseDouble(arguments.get("lat")));
                            locationValue.setLongitude(Double.parseDouble(arguments.get("long")));

                            ArtisanProfileManager.registerLocation(location, locationValue);
                        } else {
                            ArtisanProfileManager.registerDateTime(location);
                        }
                        break;

                    case "setDateTimeValue":
                        dateTime = arguments.get("variableName");
                        if (arguments.get("dateTimeValue") != null) {
                            try {
                                date = format.parse(arguments.get("dateTimeValue"));
                                ArtisanProfileManager.setDateTimeValue(dateTime, date);
                            } catch (ParseException e) {
                                // Not useful in android
                                e.printStackTrace();
                            }
                        }
                        break;

                    case "setLocationValue":
                        location = arguments.get("location");
                        if (arguments.get("lat") != null && arguments.get("long") != null) {
                            ArtisanLocationValue locationValue = new ArtisanLocationValue();
                            locationValue.setLatitude(Double.parseDouble(arguments.get("lat")));
                            locationValue.setLongitude(Double.parseDouble(arguments.get("long")));

                            ArtisanProfileManager.setLocationValue(location, locationValue);
                        }
                        break;

                    default:

                }
            }

        };
    }

    private static ArtisanMethod hasFirstPlaylistDownloaded() {
        return new ArtisanMethod() {
            @Override
            public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }

                Boolean hasDownloaded = false;
                hasDownloaded = ArtisanManager.isFirstPlaylistDownloaded();
                Response response = getResponse();
                response.setBody(hasDownloaded.toString());
            }
        };
    }

    private static ArtisanMethod onFirstPlaylistDownloaded() {
        return new ArtisanMethod() {
            @Override
            public void invoke(final TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
                if (args == null) {
                    throw new IllegalArgumentException(MSG_ARGS_MISSING);
                }
                final String callback = extractString(args, "callback");

                try {
                    args.getLong("timeout");
                }catch (JSONException e) {
                    throw new IllegalArgumentException(e);
                }

                final long timeout = args.getLong("timeout");

                ArtisanManager.onFirstPlaylistDownloaded((Activity) TealiumArtisanApp.getAppContext(), new ArtisanManagerCallback() {
                    @Override
                    public void execute() {
                        Map<String, String> data = new HashMap<String, String>(1);
                        data.put("artisan_callback", callback);

                        Tealium.track(TealiumArtisanApp.getAppContext(), data, Tealium.EVENT);
                    }
                }, timeout);
            }
        };
    }

    /**
     * !!! Add bridged methods here !!!
     private static ArtisanMethod javaMethod() {
     return new ArtisanMethod() {
    @Override public void invoke(TealiumArtisanMobileTagBridge artisanApplication, JSONObject args) {
    if (args == null) {
    throw new IllegalArgumentException(MSG_ARGS_MISSING);
    }

    //Call SDK method here
    }
    };
     }
     **/
}

