package com.tealium.location;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tealium.library.RemoteCommand.Response;
import com.tealium.location.test.Util;

import android.app.Application;
import android.test.ApplicationTestCase;

public class LocationRemoteCommandTest extends ApplicationTestCase<Application> {

	public LocationRemoteCommandTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.createApplication();
	}

	public void testEnqueue() throws Throwable {

		LocationRemoteCommand lrc = new LocationRemoteCommand(this.getContext());

		Method enqueueMethod = LocationRemoteCommand.class.getDeclaredMethod("enqueue", Response.class);
		enqueueMethod.setAccessible(true);

		enqueueMethod.invoke(lrc, Util.createResponse("resId-0", 10000));
		enqueueMethod.invoke(lrc, Util.createResponse("resId-1", 10000));

		Assert.assertEquals(2, getQueuedRequests(lrc).size());
	}

	public void testDequeue() throws Throwable {

		Method enqueueMethod = LocationRemoteCommand.class.getDeclaredMethod("enqueue", Response.class);
		Method dequeueMethod = LocationRemoteCommand.class.getDeclaredMethod("dequeue", JSONObject.class);
		enqueueMethod.setAccessible(true);
		dequeueMethod.setAccessible(true);

		LocationRemoteCommand lrc = new LocationRemoteCommand(this.getContext());

		// Test clear by null JSONObject

		enqueueMethod.invoke(lrc, Util.createResponse("resId-0", 10000));
		enqueueMethod.invoke(lrc, Util.createResponse("resId-1", 10000));

		dequeueMethod.invoke(lrc, (JSONObject) null);

		Assert.assertEquals(0, getQueuedRequests(lrc).size());

		// Test clear by empty JSONObject (no ids specified)

		enqueueMethod.invoke(lrc, Util.createResponse("resId-0", 10000));
		enqueueMethod.invoke(lrc, Util.createResponse("resId-1", 10000));

		dequeueMethod.invoke(lrc, new JSONObject());

		Assert.assertEquals(0, getQueuedRequests(lrc).size());

		// Test remove by specifying string id.

		Response r0 = Util.createResponse("resId-0", 10000);
		Response r1 = Util.createResponse("resId-1", 10000);
		
		enqueueMethod.invoke(lrc, r0);
		enqueueMethod.invoke(lrc, r1);

		JSONObject args = new JSONObject();
		args.put("response_ids", "resId-1");

		dequeueMethod.invoke(lrc, args);

		
		
		Assert.assertEquals(1, getQueuedRequests(lrc).size());

		// Reset
		clear(lrc);
		Assert.assertEquals(0, getQueuedRequests(lrc).size());

		// Test array with single entry

		enqueueMethod.invoke(lrc, Util.createResponse("resId-0", 10000));
		enqueueMethod.invoke(lrc, Util.createResponse("resId-1", 10000));

		args.put("response_ids", new JSONArray());
		args.accumulate("response_ids", "resId-1");

		dequeueMethod.invoke(lrc, args);
		
		Assert.assertEquals(1, getQueuedRequests(lrc).size());

		// Reset
		clear(lrc);

		// Test array with multiple entries

		enqueueMethod.invoke(lrc, Util.createResponse("resId-0", 10000));
		enqueueMethod.invoke(lrc, Util.createResponse("resId-1", 10000));

		args.put("response_ids", new JSONArray());
		args.accumulate("response_ids", "resId-0");
		args.accumulate("response_ids", "resId-1");

		dequeueMethod.invoke(lrc, args);
		
		Assert.assertEquals(0, getQueuedRequests(lrc).size());
	}

	@SuppressWarnings("unchecked")
	Map<String, QueuedRequest> getQueuedRequests(LocationRemoteCommand lrc) throws Throwable {
		Field queuedRequestsField = LocationRemoteCommand.class.getDeclaredField("queuedRequests");
		queuedRequestsField.setAccessible(true);
		return (Map<String, QueuedRequest>) queuedRequestsField.get(lrc);
	}

	void clear(LocationRemoteCommand lrc) throws Throwable {
		Method clearMethod = LocationRemoteCommand.class.getDeclaredMethod("clear");
		clearMethod.setAccessible(true);
		clearMethod.invoke(lrc);
	}
}
