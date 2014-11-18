package com.irewind;

import android.content.Context;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.SessionClient;
import com.irewind.sdk.iRewindConfig;

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

    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    @Singleton
    @Provides
    iRewindConfig provideConfig() {
        return new iRewindConfig("http://web01.dev.irewind.com/api", "web-client", "web-client-secret");
    }

    @Singleton
    @Provides
    SessionClient provideSessionClient(final Context context, final iRewindConfig config, final EventBus eventBus) {
        SessionClient sessionClient = new SessionClient(context, config, eventBus);
        return sessionClient;
    }

    @Singleton
    @Provides
    ApiClient provideApiClient(final Context context, final iRewindConfig config, final EventBus eventBus) {
        ApiClient apiClient = new ApiClient(context, config.getBaseURL(), eventBus);
        return apiClient;
    }
}
