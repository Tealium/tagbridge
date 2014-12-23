package com.tealium.location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.test.ApplicationTestCase;

import com.tealium.library.RemoteCommand.Response;
import com.tealium.location.test.Util;

public class QueuedRequestTest extends ApplicationTestCase<Application> {

	public QueuedRequestTest() {
		super(Application.class);
	}

	public void testConstructor() throws Throwable {

		LocationManager lm = getLocationManager();
		Response res = Util.createResponse();
		Handler handler = new Handler(Looper.getMainLooper());

		try {
			new QueuedRequest(
				null, //
				res,
				handler);
			Assert.fail();
		} catch (IllegalArgumentException e) {

		}

		try {
			new QueuedRequest(
				lm,
				null,
				handler);
			Assert.fail();
		} catch (IllegalArgumentException e) {

		}

		try {
			new QueuedRequest(
				lm, //
				res,
				null);
			Assert.fail();
		} catch (IllegalArgumentException e) {

		}
	}

	public void testLocationChanged() throws Throwable {

		LocationManager lm = getLocationManager();
		Response res = Util.createResponse();
		Handler handler = new Handler(Looper.getMainLooper());

		QueuedRequest qr = new QueuedRequest(lm, res, handler);
		qr.register();
		qr.onLocationChanged(new Location((String) null));

		Assert.assertTrue(qr.getResonse().isSent());

		try {
			qr.unregister();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

		try {
			qr.sendDefault();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

		try {
			qr.register();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

	}

	public void testRun() throws Throwable {
		LocationManager lm = getLocationManager();
		Response res = Util.createResponse(1);
		Handler handler = new Handler(Looper.getMainLooper());

		QueuedRequest qr = new QueuedRequest(lm, res, handler);

		try {
			qr.run();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

		qr.register();
		qr.run();

		Assert.assertTrue(qr.getResonse().isSent());

		try {
			qr.unregister();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

		try {
			qr.sendDefault();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

		try {
			qr.register();
			Assert.fail();
		} catch (IllegalStateException e) {
		}

	}

	public void testRegisterAndUnregister() throws Throwable {

		QueuedRequest qr = new QueuedRequest(
			getLocationManager(),
			Util.createResponse(),
			new Handler(Looper.getMainLooper()));

		qr.register();

		try {
			qr.register();
			Assert.fail();
		} catch (IllegalStateException e) {

		}

		qr.unregister();

		try {
			qr.unregister();
			Assert.fail();
		} catch (IllegalStateException e) {

		}

		qr.sendDefault();

		try {
			qr.register();
			Assert.fail();
		} catch (IllegalStateException e) {

		}

		try {
			qr.sendDefault();
			Assert.fail();
		} catch (IllegalStateException e) {

		}

	}

	public void testExtractProvider() throws Throwable {

		final JSONObject args = new JSONObject();

		Method extractProvidersMethod = QueuedRequest.class.getDeclaredMethod(
			"extractProviders", JSONObject.class);
		extractProvidersMethod.setAccessible(true);

		try {
			extractProvidersMethod.invoke(QueuedRequest.class, args);
			Assert.fail();
		} catch (InvocationTargetException e) {
			Assert.assertEquals(IllegalArgumentException.class, e.getTargetException().getClass());
		}

		args.put("provider", "123456");
		Assert.assertEquals(1, ((String[]) extractProvidersMethod.invoke(QueuedRequest.class, args)).length);

		JSONArray ids = new JSONArray();
		ids.put(123);
		ids.put(321);

		args.put("provider", ids);
		Assert.assertEquals(2, ((String[]) extractProvidersMethod.invoke(QueuedRequest.class, args)).length);

		args.put("provider", new JSONObject());
		Assert.assertEquals(1, ((String[]) extractProvidersMethod.invoke(QueuedRequest.class, args)).length);

		try {
			args.put("provider", new JSONArray());
			extractProvidersMethod.invoke(QueuedRequest.class, args);
			Assert.fail();
		} catch (InvocationTargetException e) {
			Assert.assertEquals(IllegalArgumentException.class, e.getCause().getClass());
		}

	}

	public void testExtractMinDistance() throws Throwable {

		final JSONObject args = new JSONObject();

		Method extractMinDistanceMethod = QueuedRequest.class.getDeclaredMethod(
			"extractMinDistance", JSONObject.class);
		extractMinDistanceMethod.setAccessible(true);

		Assert.assertEquals(0.0f, extractMinDistanceMethod.invoke(QueuedRequest.class, args));

		try {
			args.put("min_distance", "blah");
			extractMinDistanceMethod.invoke(QueuedRequest.class, args);
			Assert.fail();
		} catch (InvocationTargetException e) {
			Assert.assertEquals(IllegalArgumentException.class, e.getTargetException().getClass());
		}

		args.put("min_distance", 500.5f);
		Assert.assertEquals(500.5f, extractMinDistanceMethod.invoke(QueuedRequest.class, args));

	}

	private static void addTestProvider(LocationManager lm) {
		String name = "test";
		boolean requiresNetwork = true;
		boolean requiresSatellite = false;
		boolean requiresCell = false;
		boolean hasMonetaryCost = false;
		boolean supportsAltitude = true;
		boolean supportsSpeed = true;
		boolean supportsBearing = true;
		int powerRequirement = 0;
		int accuracy = 50;

		if (lm.getAllProviders().contains(name)) {
			return;
		}

		lm.addTestProvider(
			name,
			requiresNetwork,
			requiresSatellite,
			requiresCell,
			hasMonetaryCost,
			supportsAltitude,
			supportsSpeed,
			supportsBearing,
			powerRequirement,
			accuracy);
	}

	private LocationManager getLocationManager() {
		this.createApplication();
		LocationManager locationManager = (LocationManager) this.getContext().getSystemService(
			Context.LOCATION_SERVICE);
		addTestProvider(locationManager);
		return locationManager;
	}

}
