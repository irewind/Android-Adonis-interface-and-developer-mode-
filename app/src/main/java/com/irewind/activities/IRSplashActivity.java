package com.irewind.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import com.crashlytics.android.Crashlytics;
import com.irewind.R;


public class IRSplashActivity extends IRBaseActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_irsplash);

        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
                Intent mainIntent = new Intent(mContext, IRTabActivity.class);
                startActivity(mainIntent);

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onBackPressed(){
//        super.onBackPressed();
    }

}
