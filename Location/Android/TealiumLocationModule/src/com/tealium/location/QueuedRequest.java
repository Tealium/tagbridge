package com.tealium.location;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.tealium.library.RemoteCommand.Response;

/**
 * Utility object used to manage location requests. Once a response has been
 * sent, this object is spent, and can perform no more operations.
 * 
 * @version 1
 * @since 2014-12-19
 * 
 * */
final class QueuedRequest implements Runnable, LocationListener {

	private final LocationManager locationManager;
	private final Handler handler;
	private final Response response;
	private final String[] providers;
	private final float minDistance;
	private final long minTime;
	private final long timeout;

	private OnResponseSentListener onResponseSentListener;
	private boolean isRegistered;

	/**
	 * @param locationManager
	 *            a location manager instance to subscribe location updates
	 *            from.
	 * @param response
	 *            the response for the incoming request.
	 * @param handler
	 *            to indicate where to perform the timeout operation.
	 * 
	 * @throws IllegalArgumentException
	 *             when any parameters are null or the request was malformed.
	 * 
	 * */
	public QueuedRequest(
		LocationManager locationManager,
		Response response,
		Handler handler) {

		if (locationManager == null ||
			response == null ||
			handler == null) {
			throw new IllegalArgumentException("locationManager, response, handler, and args must not be null.");
		}

		final JSONObject args = response.getRequestPayload().optJSONObject("arguments");

		if (args == null) {
			throw new IllegalArgumentException(String.format(Locale.ROOT, "unable to perform \"start\", \"arguments\" must be present."));
		}

		this.locationManager = locationManager;
		this.response = response;
		this.handler = handler;

		this.providers = extractProviders(args);
		this.minDistance = extractMinDistance(args);
		this.minTime = args.optLong("min_time", 0);
		this.timeout = extractTimeout(args);

		this.isRegistered = false;
	}

	/**
	 * @return Response object this instance was built with.
	 * */
	Response getResonse() {
		return this.response;
	}

	/**
	 * When a location update or timeout occurs, the listener assigned here will
	 * have {@link OnResponseSentListener#onResponseSent(QueuedRequest)} called.
	 * 
	 * @param onResponseSentListener
	 *            instance or null.
	 * 
	 * */
	void setOnResponseSentListener(OnResponseSentListener onResponseSentListener) {
		this.onResponseSentListener = onResponseSentListener;
	}

	/**
	 * @return whether this item is registered to listen for location updates.
	 * 
	 * */
	boolean isRegistered() {
		return this.isRegistered;
	}

	/**
	 * Register for location updates and timeout.
	 * 
	 * @throws IllegalStateException
	 *             when already registered or response was sent.
	 * 
	 * */
	void register() {
		if (this.isRegistered) {
			throw new IllegalStateException("Call to register when already registered.");
		}

		if (this.response.isSent()) {
			throw new IllegalStateException("Unable to register for updates when response has already been sent.");
		}

		for (String provider : this.providers) {
			this.locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
		}

		this.handler.postDelayed(this, this.timeout);

		this.isRegistered = true;
	}

	/**
	 * Unregister for location updates and timeout.
	 * 
	 * @throws IllegalStateException
	 *             when not registered.
	 * 
	 * */
	void unregister() {
		if (!this.isRegistered) {
			throw new IllegalStateException("Call to unregister when not registered.");
		}
		this.locationManager.removeUpdates(this);
		this.handler.removeCallbacks(this);
		this.isRegistered = false;
	}

	/**
	 * Responds to the TagBridge request with the last known location from any
	 * of its location providers, this can be null.
	 * 
	 * @throws IllegalStateException
	 *             when the response has already been sent.
	 * 
	 * */
	void sendDefault() {
		Location lastLoc;

		for (String provider : this.providers) {
			if ((lastLoc = this.locationManager.getLastKnownLocation(provider)) != null) {
				sendLocation(lastLoc);
				return;
			}
		}

		sendLocation(null);
	}

	private void sendLocation(Location location) {
		if (location != null) {
			try {
				JSONObject loc = new JSONObject();
				loc.put("latitude", location.getLatitude());
				loc.put("longitude", location.getLongitude());
				if (location.getAltitude() != 0) {
					loc.put("altitude", location.getAltitude());
				}
				response.setBody(loc.toString());
			} catch (JSONException e) {
				// Never should happen.
				throw new RuntimeException(e);
			}
		} else {
			response.setStatus(204); // No content
		}
		response.send();
	}

	// Implementations.

	/**
	 * To be called in the event of a timeout.
	 * */
	@Override
	public void run() {
		if (this.onResponseSentListener != null) {
			this.onResponseSentListener.onResponseSent(this);
		}
		this.unregister();
		this.sendDefault();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (this.onResponseSentListener != null) {
			this.onResponseSentListener.onResponseSent(this);
		}
		this.unregister();
		this.sendLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	private static String[] extractProviders(JSONObject args) {
		final JSONArray providers = args.optJSONArray("provider");
		final String provider = args.optString("provider", null);

		if (providers == null && provider == null) {
			throw new IllegalArgumentException("\"provider\" is a required key for method \"get_location\".");
		}

		if (providers != null) {

			if (providers.length() == 0) {
				throw new IllegalArgumentException("there must be at least one provider specified.");
			}

			try {
				String[] extractedProviders = new String[providers.length()];
				for (int i = 0; i < providers.length(); i++) {
					extractedProviders[i] = providers.getString(i);
				}

				return extractedProviders;
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		// provider must not be null.

		return new String[] { provider };
	}

	private static float extractMinDistance(JSONObject args) {

		final String minDistance = args.optString("min_distance", "0");

		if ("0".equals(minDistance)) {
			return 0.0f;
		}

		try {
			return Float.parseFloat(minDistance);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format(Locale.ROOT, "\"%s\" is not a valid \"min_distance\".", minDistance));
		}
	}

	private static long extractTimeout(JSONObject args) {
		final long timeout = args.optLong("timeout", 0);
		if (timeout < 1) {
			throw new IllegalArgumentException("value for \"timeout\" in \"arguments\" must be an integer greater than 0.");
		}
		return timeout;
	}

	static interface OnResponseSentListener {
		void onResponseSent(QueuedRequest queuedRequest);
	}

}
