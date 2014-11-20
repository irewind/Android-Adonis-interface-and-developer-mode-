package com.irewind.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import javax.inject.Inject;


public class IRSplashActivity extends IRBaseActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private Context mContext;

    @Inject
    ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Crashlytics.start(this);

        Injector.inject(this);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_irsplash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();

                if (apiClient.getActiveSession().isOpened()) {
                    Intent mainIntent = new Intent(mContext, IRTabActivity.class);
                    startActivity(mainIntent);
                } else {
                    Intent mainIntent = new Intent(mContext, IRLoginActivity.class);
                    startActivity(mainIntent);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
