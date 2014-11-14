package com.irewind;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by tzmst on 11/10/2014.
 */
public class IRApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("fonts/OpenSans-Regular.ttf", R.attr.fontPath);
    }
}
