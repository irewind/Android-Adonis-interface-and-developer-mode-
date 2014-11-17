package com.irewind;

import com.facebook.LoginActivity;
import com.irewind.activities.IRForgotPasswordActivity;
import com.irewind.activities.IRRegisterActivity;
import com.irewind.activities.IRTabActivity;

import dagger.Module;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                LoginActivity.class,
                IRForgotPasswordActivity.class,
                IRRegisterActivity.class,
                IRTabActivity.class
        }
)

public class UIModule {
}
