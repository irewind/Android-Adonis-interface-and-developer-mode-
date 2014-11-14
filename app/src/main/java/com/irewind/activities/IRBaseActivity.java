package com.irewind.activities;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by tzmst on 11/14/2014.
 */
public class IRBaseActivity extends ActionBarActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
}
