package com.irewind;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.support.multidex.MultiDex;

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

        instance = this;

        Injector.init(getRootModule(), this);

        CalligraphyConfig.initDefault("fonts/OpenSans-Regular.ttf", R.attr.fontPath);
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
