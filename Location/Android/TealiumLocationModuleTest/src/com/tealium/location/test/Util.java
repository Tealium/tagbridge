package com.tealium.location.test;

import org.json.JSONObject;

import com.tealium.library.RemoteCommand.Response;

public final class Util {
	private Util() {
	}

	public static final String VALUE_PROVIDER = "network";

	public static Response createResponse(String responseId, long timeout) throws Throwable {

		JSONObject args = new JSONObject();
		args.put("provider", VALUE_PROVIDER);
		args.put("timeout", timeout);

		JSONObject requestPayload = new JSONObject();
		requestPayload.put("arguments", args);

		return new Response("_location", responseId, requestPayload);
	}

	public static Response createResponse(long timeout) throws Throwable {
		return createResponse("response-id", 10000);
	}

	public static Response createResponse() throws Throwable {
		return createResponse(10000);
	}

}
