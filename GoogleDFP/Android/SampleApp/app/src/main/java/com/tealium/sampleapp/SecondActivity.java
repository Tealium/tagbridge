package com.tealium.sampleapp;

import android.app.Activity;
import android.os.Bundle;

import com.tealium.sampleapp.helper.TealiumHelper;


public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
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
