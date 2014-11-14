package com.irewind;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class IRApplication extends Application{

    private static IRApplication instance;

    public IRApplication() {
    }

    /**
     * Create main application
     *
     * @param context
     */
    public IRApplication(final Context context) {
        this();
        attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("fonts/OpenSans-Regular.ttf", R.attr.fontPath);

        instance = this;

        Injector.init(getRootModule(), this);
    }

    private Object getRootModule() {
        return new RootModule();
    }
    /**
     * Create main application
     *
     * @param instrumentation
     */
    public IRApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public static IRApplication getInstance() {
        return instance;
    }
}
