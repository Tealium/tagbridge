package com.tealium.blankapp;

import com.tealium.blankapp.helper.TealiumHelper;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		TealiumHelper.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TealiumHelper.onResume(this, null);
	}
}
