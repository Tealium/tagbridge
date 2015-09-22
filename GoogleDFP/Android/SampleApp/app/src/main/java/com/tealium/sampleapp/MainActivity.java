package com.tealium.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.tealium.sampleapp.helper.TealiumHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.main_button)
                .setOnClickListener(createNextViewClickListener());
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

    private static View.OnClickListener createNextViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), SecondActivity.class));
            }
        };
    }
}
