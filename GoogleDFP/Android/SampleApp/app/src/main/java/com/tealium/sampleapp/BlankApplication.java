package com.tealium.sampleapp;

import com.tealium.sampleapp.helper.TealiumHelper;

import android.app.Application;

public final class BlankApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// Must occur after onCreate();
		TealiumHelper.initialize(this);
	}
}
