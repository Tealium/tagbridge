package com.tealium.adobe;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.json.JSONObject;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tealium.adobe.AdobeRemoteCommand;

public class AdobeRemoteCommandTest extends ApplicationTestCase<Application> {

	public AdobeRemoteCommandTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.createApplication();
	}

	public void testConstructor() throws Throwable {
		try {
			new AdobeRemoteCommand(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {

		}
	}

	public void testInternalMethods() throws Throwable {

		final JSONObject args = new JSONObject();
		args.put("action", "");
		args.put("action_name", "");
		args.put("adobe", "");
		args.put("amount", "7");
		args.put("arguments", "");
		args.put("custom_data", new JSONObject());
		args.put("latitude", "0");
		args.put("longitude", "0");
		args.put("major", "");
		args.put("media_track", "");
		args.put("method", "");
		args.put("minor", "");
		args.put("name", "");
		args.put("offset", "5");
		args.put("privacy_status", "1");
		args.put("proximity", "0");
		args.put("proximity_uuid", "");
		args.put("should_send_event", "");
		args.put("state_name", "");
		args.put("identifier", "");

		final Method invokeMethodMethod = AdobeRemoteCommand.class.getDeclaredMethod("invokeMethod", String.class, JSONObject.class);
		invokeMethodMethod.setAccessible(true);

		AdobeRemoteCommand arc = new AdobeRemoteCommand(this.getContext());

		invokeMethodMethod.invoke(arc, "set_privacy_status", args);
		invokeMethodMethod.invoke(arc, "set_user_identifier", args);
		invokeMethodMethod.invoke(arc, "collect_lifecycle_data", args);
		invokeMethodMethod.invoke(arc, "pause_collecting_lifecycle_data", args);
		invokeMethodMethod.invoke(arc, "track_state", args);
		invokeMethodMethod.invoke(arc, "track_action", args);
		invokeMethodMethod.invoke(arc, "track_action_from_background", args);
		invokeMethodMethod.invoke(arc, "track_lifetime_value_increase", args);
		invokeMethodMethod.invoke(arc, "track_location", args);
		invokeMethodMethod.invoke(arc, "track_beacon", args);
		invokeMethodMethod.invoke(arc, "tracking_clear_current_beacon", args);
		invokeMethodMethod.invoke(arc, "track_timed_action_start", args);
		invokeMethodMethod.invoke(arc, "track_timed_action_update", args);
		invokeMethodMethod.invoke(arc, "track_timed_action_end", args);
		invokeMethodMethod.invoke(arc, "tracking_send_queued_hits", args);
		invokeMethodMethod.invoke(arc, "tracking_clear_queue", args);
		invokeMethodMethod.invoke(arc, "media_close", args);
		invokeMethodMethod.invoke(arc, "media_play", args);
		invokeMethodMethod.invoke(arc, "media_complete", args);
		invokeMethodMethod.invoke(arc, "media_stop", args);
		invokeMethodMethod.invoke(arc, "media_click", args);
		invokeMethodMethod.invoke(arc, "media_track", args);

	}
}
