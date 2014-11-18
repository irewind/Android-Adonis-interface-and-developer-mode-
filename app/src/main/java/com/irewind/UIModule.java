package com.irewind;

import com.irewind.activities.IRForgotPasswordActivity;
import com.irewind.activities.IRLoginActivity;
import com.irewind.activities.IRRegisterActivity;
import com.irewind.activities.IRSplashActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.fragments.IRAccountFragment;
import com.irewind.fragments.IRAccountPersonalFragment;
import com.irewind.fragments.IRMoreFragment;

import dagger.Module;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                IRSplashActivity.class,
                IRLoginActivity.class,
                IRForgotPasswordActivity.class,
                IRRegisterActivity.class,
                IRTabActivity.class,
                IRAccountFragment.class,
                IRAccountPersonalFragment.class,
                IRMoreFragment.class
        },

        library = true
)

public class UIModule {
}
