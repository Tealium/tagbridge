package com.tealium.blankapp.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.webkit.WebView;

import com.tealium.blankapp.BuildConfig;
import com.tealium.library.Tealium;
//import com.tealium.location.LocationRemoteCommand;
import com.tealium.location.LocationRemoteCommand;

public final class TealiumHelper {

	@SuppressLint("NewApi")
	public static void initialize(Application application) {
		Tealium.Config config = Tealium.Config.create(application, "tealiummobile", "demo", "dev")
			.setLibraryLogLevel(Tealium.LogLevel.VERBOSE)
			.addRemoteCommand(new LocationRemoteCommand(application));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
			WebView.setWebContentsDebuggingEnabled(true);
		}

		Tealium.initialize(config);
	}

	public static void onResume(Activity activity) {
		Tealium.onResume(activity);
	}

	public static void onPause(Activity activity) {
		Tealium.onPause(activity);
	}
}
