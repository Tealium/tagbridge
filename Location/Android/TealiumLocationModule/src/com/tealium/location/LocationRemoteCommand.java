package com.tealium.location;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;

import com.tealium.library.RemoteCommand;
import com.tealium.location.QueuedRequest.OnResponseSentListener;

/**
 * TagBridge Module used to capture device locations.
 * 
 * @version 1
 * @since 2014-12-19
 * 
 * */
public class LocationRemoteCommand extends RemoteCommand {

	private static final String METHOD_GET_LOC = "get_location";
	private static final String METHOD_CANCEL = "cancel";

	private final Map<String, QueuedRequest> queuedRequests;
	private final LocationManager locationManager;
	private final Handler handler;

	/**
	 * @param context
	 *            required to access the LocationManager and receive updates.
	 * @throws IllegalArgumentException
	 *             when context is null.
	 * */
	public LocationRemoteCommand(Context context) {
		super("_location", "Calling this TagBridge command will track device location.");

		if (context == null) {
			throw new IllegalArgumentException("context must not be null.");
		}

		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		this.queuedRequests = new ConcurrentHashMap<String, QueuedRequest>();
		this.handler = new Handler(Looper.getMainLooper());
	}

	/**
	 * Enqueues the response and performs it when a location update occurs or a
	 * timeout occurs. If the given args object are invalid, a bad-request
	 * response will be sent, and the response will not be enqueued.
	 * 
	 * @param response
	 *            the {@link Response} object provided by TagBridge.
	 * @param args
	 *            the object from the "arguments" key from the request payload.
	 * 
	 * */
	private void enqueue(Response response) {
		try {
			final QueuedRequest queuedRequest = new QueuedRequest(
				this.locationManager,
				response,
				this.handler);
			queuedRequest.setOnResponseSentListener(
				createOnResponseSentListener(this.queuedRequests));
			queuedRequest.register();
			this.queuedRequests.put(response.getId(), queuedRequest);
		} catch (IllegalArgumentException e) {
			response.setStatus(Response.STATUS_BAD_REQUEST);
			response.setBody(String.format(Locale.ROOT, "unable to perform \"get_location\": %s.", e.getMessage()));
			response.send();
		}
	}

	/**
	 * Dequeue responses based on the args provided. If null or missing the
	 * \"response_ids\" key, all responses will force their timeout and
	 * dequeued.
	 * 
	 * @param args
	 *            arguments specified by the key "arguments" in the request
	 *            payload.
	 * 
	 * */
	private void dequeue(final JSONObject args) throws JSONException {

		if (args == null || !args.has("response_ids")) {
			clear();
			return;
		}

		final JSONArray responseIds = args.optJSONArray("response_ids");
		final String responseId = args.optString("response_ids", null);

		if (responseIds != null) {
			for (int i = 0; i < responseIds.length(); i++) {
				this.dequeueRequest(responseIds.getString(i));
			}
		} else {
			this.dequeueRequest(responseId);
		}
	}

	/**
	 * Call {@link QueuedRequest#sendDefault()} of the request that matches the
	 * provided requestId.
	 * 
	 * @param responseId
	 *            response id of the desired request to remove.
	 * 
	 * @throws IllegalArgumentException
	 *             when responseId is null.
	 * 
	 * */
	private void dequeueRequest(String responseId) {
		QueuedRequest queuedRequest = this.queuedRequests.remove(responseId);
		if (queuedRequest != null) {
			queuedRequest.unregister();
			queuedRequest.sendDefault();
		}
	}

	/**
	 * Dequeue all entries and call {@link QueuedRequest#sendDefault()}.
	 * 
	 * */
	private void clear() {
		for (QueuedRequest request : this.queuedRequests.values()) {
			request.unregister();
			request.sendDefault();
		}
		this.queuedRequests.clear();
	}

	// Implementations

	@Override
	protected void onInvoke(Response response) throws Throwable {

		if (response.getId() == null) {
			// No point in performing operation if there's no callback.
			return;
		}

		final JSONObject payload = response.getRequestPayload();
		final String methodName = payload.optString("method", null);

		if (METHOD_GET_LOC.equals(methodName)) {
			this.enqueue(response);
		} else if (METHOD_CANCEL.equals(methodName)) {
			this.dequeue(payload.optJSONObject("arguments"));
			response.send();
		} else {
			response.setStatus(Response.STATUS_BAD_REQUEST);
			response.setBody(String.format(Locale.ROOT, "method \"%s\" is unknown.", methodName));
			response.send();
		}
	}

	private static OnResponseSentListener createOnResponseSentListener(final Map<String, QueuedRequest> requests) {
		return new OnResponseSentListener() {
			@Override
			public void onResponseSent(QueuedRequest queuedRequest) {
				requests.remove(queuedRequest.getResonse().getId());
			}
		};
	}

}
