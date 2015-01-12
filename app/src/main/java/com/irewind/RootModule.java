package com.irewind;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module(
        includes = {
                AndroidModule.class,
                SDKModule.class
        }
)
public class RootModule {
}
