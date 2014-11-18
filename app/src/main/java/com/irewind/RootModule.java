package com.irewind;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module(
        includes = {
                AndroidModule.class,
                IRModule.class
        }
)
public class RootModule {
}
