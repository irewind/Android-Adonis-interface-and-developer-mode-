package com.irewind;

import android.content.Context;

import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.iRewindConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                IRApplication.class
        },

        includes = {
                UIModule.class
        },

        library = true
)

public class IRModule {
    @Singleton
    @Provides
    EventBus provideBus() {
        return new EventBus();
    }

    @Singleton
    @Provides
    iRewindConfig provideConfig() {
        return new iRewindConfig("http://web01.dev.irewind.com/api", "web-client", "web-client-secret");
    }

    @Singleton
    @Provides
    ApiClient provideApiClient(final Context context, final iRewindConfig config, final EventBus eventBus) {
        ApiClient apiClient = new ApiClient(context, config, eventBus);
        return apiClient;
    }
}
