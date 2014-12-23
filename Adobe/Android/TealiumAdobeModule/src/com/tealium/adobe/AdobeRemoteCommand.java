package com.tealium.adobe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.adobe.mobile.Analytics;
import com.adobe.mobile.Config;
import com.adobe.mobile.Media;
import com.adobe.mobile.MobilePrivacyStatus;
import com.adobe.mobile.Analytics.BEACON_PROXIMITY;
import com.adobe.mobile.Analytics.TimedActionBlock;
import com.tealium.library.RemoteCommand;

/**
 * Module for incorporating Adobe Marketing Cloud 4.3 Android SDK into TagBridge
 * 4.1
 * 
 * @since 2014-12-11
 * @version 1.0
 * */
public final class AdobeRemoteCommand extends RemoteCommand {

	// Exposed to in-package visibility for testing convenience.
	static interface AdobeMethod {
		void invoke(JSONObject args);
	}

	private static final String MSG_ARGS_MISSING = "\"arguments\" must not be null.";
	private static final String KEY_METHOD = "method";
	private static final String KEY_ARGS = "arguments";

	private final Context context;
	private final Map<String, AdobeMethod> methods;
	private boolean initialized;

	/**
	 * @param context
	 *            used to initialize the Adobe config.
	 * @throws IllegalArgumentException
	 *             when context is null.
	 * 
	 * */
	public AdobeRemoteCommand(Context context) {
		super("adobe", "Module for incorporating Android Adobe Marketing Cloud SDK 4.3");

		if (context == null) {
			throw new IllegalArgumentException("context must not be null.");
		}

		this.initialized = false;

		this.context = context.getApplicationContext();

		AdobeMethod trackActionMethod = createTrackActionAdobeMethod();

		this.methods = new HashMap<String, AdobeMethod>(22);

		this.methods.put("set_privacy_status", createSetPrivacyStatusAdobeMethod());
		this.methods.put("collect_lifecycle_data", createCollectLifecycleDataAdobeMethod());
		this.methods.put("track_state", createTrackStateAdobeMethod());
		this.methods.put("track_action", trackActionMethod);
		this.methods.put("track_action_from_background", trackActionMethod);

		this.methods.put("track_lifetime_value_increase", createTrackLifetimeValueIncreaseAdobeMethod());
		this.methods.put("track_location", createTrackLocationAdobeMethod());
		this.methods.put("media_track", createMediaTrackAdobeMethod());
		this.methods.put("media_click", createMediaClickAdobeMethod());
		this.methods.put("media_stop", createMediaStopAdobeMethod());

		this.methods.put("media_complete", createMediaCompleteAdobeMethod());
		this.methods.put("media_play", createMediaPlayAdobeMethod());
		this.methods.put("media_close", createMediaCloseAdobeMethod());
		this.methods.put("tracking_clear_queue", createTrackingClearQueueAdobeMethod());
		this.methods.put("tracking_send_queued_hits", createTrackingSendQueuedHitsAdobeMethod());

		this.methods.put("track_timed_action_start", createTrackTimedActionStartAdobeMethod());
		this.methods.put("track_timed_action_update", createTrackTimedActionUpdateAdobeMethod());
		this.methods.put("tracking_clear_current_beacon", createTrackingClearCurrentBeaconAdobeMethod());
		this.methods.put("track_timed_action_end", createTrackTimedActionEndAdobeMethod());
		this.methods.put("track_beacon", createTrackBeaconAdobeMethod());

		this.methods.put("pause_collecting_lifecycle_data", createPauseCollectingLifecycleDataAdobeMethod());
		this.methods.put("set_user_identifier", createSetUserIdentifierAdobeMethod());
	}

	@Override
	protected void onInvoke(Response response) throws Throwable {

		if (!initialized) {
			Config.setContext(context);
			initialized = true;
		}

		final JSONObject payload = response.getRequestPayload();
		final String methodName = payload.optString(KEY_METHOD, null);
		final JSONObject args = payload.optJSONObject(KEY_ARGS);

		if (methodName == null) {
			response.setStatus(Response.STATUS_BAD_REQUEST);
			response.setBody(String.format(Locale.ROOT, "no method was specified."));
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

		final AdobeMethod method = this.methods.get(methodName);
		if (method == null) {
			throw new IllegalArgumentException(String.format(
				Locale.ROOT,
				"method with name \"%s\" was not found.",
				methodName));
		}

		method.invoke(args);
	}

	private static AdobeMethod createSetUserIdentifierAdobeMethod() {
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Config.setUserIdentifier(extractString(args, "identifier"));
			}
		};
	}

	private static AdobeMethod createTrackingClearCurrentBeaconAdobeMethod() {

		// "tracking_clear_current_beacon" args: none
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				Analytics.clearBeacon();
			}
		};
	}

	private static AdobeMethod createTrackTimedActionStartAdobeMethod() {

		// "track_timed_action_start" args: "action" string "custom_data"
		// dictionary
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Analytics.trackTimedActionStart(
					extractString(args, "action"),
					jsonObjectToMap(args.optJSONObject("custom_data")));
			}
		};
	}

	private static AdobeMethod createTrackTimedActionUpdateAdobeMethod() {

		// "track_timed_action_update", args: "action" string "custom_data"
		// dictionary
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Map<String, Object> customData = jsonObjectToMap(args.optJSONObject("custom_data"));

				if (customData == null) {
					throw new IllegalArgumentException("\"custom_data\" missing from \"arguments\".");
				}

				Analytics.trackTimedActionUpdate(
					extractString(args, "action"),
					customData);
			}
		};
	}

	private static AdobeMethod createTrackTimedActionEndAdobeMethod() {

		/*
		 * track_timed_action_end
		 * 
		 * Arguments:
		 * 
		 * action : string
		 * 
		 * custom_data : object
		 * 
		 * should_send_event : string of true | false
		 */

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				final Map<String, Object> extractedData = jsonObjectToMap(args.optJSONObject("custom_data"));
				final boolean shouldSend = args.optBoolean("should_send_event", true);

				Analytics.trackTimedActionEnd(
					extractString(args, "action"),
					new TimedActionBlock<Boolean>() {
						@Override
						public Boolean call(long inAppDuration, long totalDuration, Map<String, Object> customData) {

							if (extractedData != null) {
								customData.putAll(extractedData);
							}

							return shouldSend;
						}
					});
			}
		};
	}

	private static AdobeMethod createTrackingSendQueuedHitsAdobeMethod() {

		// "tracking_send_queued_hits" args: none
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				Analytics.sendQueuedHits();
			}
		};
	}

	private static AdobeMethod createTrackingClearQueueAdobeMethod() {

		// "tracking_clear_queue" args: none
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				Analytics.clearQueue();
			}
		};
	}

	private static AdobeMethod createMediaCloseAdobeMethod() {

		// "media_close" args: "name" string
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Media.close(extractString(args, "name"));
			}
		};
	}

	private static AdobeMethod createMediaPlayAdobeMethod() {

		// "media_play", args: "name" string "offset" string - doubleValue
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Media.play(
					extractString(args, "name"),
					extractDouble(args, "offset"));
			}
		};
	}

	private static AdobeMethod createMediaCompleteAdobeMethod() {

		// "media_complete", args: "name" string "offset" string - doubleValue
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Media.complete(
					extractString(args, "name"),
					extractDouble(args, "offset"));
			}
		};
	}

	private static AdobeMethod createMediaStopAdobeMethod() {

		// "media_stop", args: "name" string "offset" string - doubleValue
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Media.stop(
					extractString(args, "name"),
					extractDouble(args, "offset"));
			}
		};
	}

	private static AdobeMethod createMediaClickAdobeMethod() {

		// "media_click", args: "name" string "offset" string - doubleValue
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Media.click(
					extractString(args, "name"),
					extractDouble(args, "offset"));
			}
		};
	}

	private static AdobeMethod createMediaTrackAdobeMethod() {

		// "media_track" args: "name" string "custom_data" dictionary
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Media.track(
					extractString(args, "media_track"),
					jsonObjectToMap(args.optJSONObject("custom_data")));
			}
		};
	}

	private static AdobeMethod createPauseCollectingLifecycleDataAdobeMethod() {
		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				Config.pauseCollectingLifecycleData();
			}
		};
	}

	private static AdobeMethod createTrackBeaconAdobeMethod() {

		// "track_beacon" args: "beacon_uuid" string of UUID should match
		// proximity UUID for a CLBeacon class store in a tagBridge instance's
		// ivar;
		// "custom_data" dictionary

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Analytics.trackBeacon(
					extractString(args, "proximity_uuid"),
					extractString(args, "major"),
					extractString(args, "minor"),
					extractProximity(args),
					jsonObjectToMap(args.optJSONObject("custom_data")));
			}

			private BEACON_PROXIMITY extractProximity(JSONObject args) {

				final int prox = args.optInt("proximity", -1);

				switch (prox) {
				case 0:
					return BEACON_PROXIMITY.PROXIMITY_UNKNOWN;
				case 1:
					return BEACON_PROXIMITY.PROXIMITY_IMMEDIATE;
				case 2:
					return BEACON_PROXIMITY.PROXIMITY_NEAR;
				case 3:
					return BEACON_PROXIMITY.PROXIMITY_FAR;
				}

				throw new IllegalArgumentException("value for key \"proximity\" in \"arguments\" must be 0-3.");
			}

		};
	}

	private static AdobeMethod createTrackLocationAdobeMethod() {
		// "track_location" args: "latitude" string "longitude" string
		// "custom_data"
		// dictionary

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Analytics.trackLocation(
					extractLocation(args),
					jsonObjectToMap(args.optJSONObject("custom_data")));
			}

			private Location extractLocation(JSONObject args) {
				final String lat = args.optString("latitude", null);
				final String lon = args.optString("longitude", null);

				if (lat == null || lon == null) {
					throw new IllegalArgumentException("\"latitude\" and \"longitude\" must be present in \"arguments\".");
				}

				try {
					Location loc = new Location((String) null);
					loc.setLatitude(Long.parseLong(lat));
					loc.setLongitude(Long.parseLong(lon));
					return loc;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(String.format(
						Locale.ROOT,
						"\"%s\" or \"%s\" is not a parcelable value.",
						lat, lon));
				}
			}
		};
	}

	private static AdobeMethod createTrackLifetimeValueIncreaseAdobeMethod() {

		// "track_lifetime_value_increase" args: "amount" decimal number
		// "custom_data" dictionary

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Analytics.trackLifetimeValueIncrease(
					extractAmount(args),
					jsonObjectToMap(args.optJSONObject("custom_data")));

			}

			private BigDecimal extractAmount(JSONObject args) {
				final String amount = args.optString("amount", null);

				if (amount == null) {
					throw new IllegalArgumentException("\"amount\" was not specified.");
				}

				try {
					return new BigDecimal(amount);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(String.format(Locale.ROOT, "\"%s\" was not a parsable amount.", amount));
				}
			}
		};
	}

	private static AdobeMethod createTrackActionAdobeMethod() {

		// "track_action" args: "action_name" string "custom_data" dictionary

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Analytics.trackAction(
					extractString(args, "action_name"),
					jsonObjectToMap(args.optJSONObject("custom_data")));
			}
		};
	}

	private static AdobeMethod createTrackStateAdobeMethod() {

		// "track_state" args: "state_name" string "custom_data" dictionary

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				Analytics.trackState(
					extractString(args, "state_name"),
					jsonObjectToMap(args.optJSONObject("custom_data")));
			}
		};
	};

	private static AdobeMethod createCollectLifecycleDataAdobeMethod() {

		// "collect_lifecycle_data" args: none

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				Config.collectLifecycleData();
			}
		};
	}

	private static AdobeMethod createSetPrivacyStatusAdobeMethod() {

		// "set_privacy_status" args: "privacy_status" string,
		// integerValue
		// 1 = OptIn, 2 = OptOut, 3 = Unknown

		return new AdobeMethod() {
			@Override
			public void invoke(JSONObject args) {
				switch (extractPrivacyStatus(args)) {
				case 1:
					Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN);
					break;
				case 2:
					Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_OUT);
					break;
				case 3:
					Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_UNKNOWN);
					break;
				}
			}

			private int extractPrivacyStatus(JSONObject args) {
				int option;

				if (args == null) {
					throw new IllegalArgumentException(MSG_ARGS_MISSING);
				}

				if ((option = args.optInt("privacy_status", -1)) == -1) {
					throw new IllegalArgumentException("arguments missing key \"privacy_status\".");
				}

				if (option < 1 || option > 3) {
					throw new IllegalArgumentException("value for key \"privacy_status\" must be 0-3.");
				}

				return option;
			}
		};
	}

	/**
	 * Converts a JSONObject to a Map.
	 * 
	 * @param jsonObject
	 *            if null, returns null; otherwise, iterates through the
	 *            JSONObject performing put operations into the new map.
	 * @return null or populated map.
	 * 
	 * */
	private static HashMap<String, Object> jsonObjectToMap(JSONObject jsonObject) {
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
	 * @param args
	 *            source containing the value for the key.
	 * @param key
	 *            key to fetch the value from the args.
	 * @throws IllegalArgumentException
	 *             if key is missing.
	 * 
	 * */
	private static String extractString(JSONObject args, String key) {
		final String value = args.optString(key, null);
		if (value == null) {
			throw new IllegalArgumentException(String.format(Locale.ROOT, "\"%s\" is missing from \"arguments\".", key));
		}
		return value;
	}

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
}
