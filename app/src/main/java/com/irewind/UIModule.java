package com.irewind;

import android.content.Context;

import com.irewind.activities.IRForgotPasswordActivity;
import com.irewind.activities.IRLoginActivity;
import com.irewind.activities.IRRegisterActivity;
import com.irewind.activities.IRSplashActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.fragments.IRAccountFragment;
import com.irewind.fragments.IRAccountNotificationFragment;
import com.irewind.fragments.IRAccountPasswordFragment;
import com.irewind.fragments.IRAccountPersonalFragment;
import com.irewind.fragments.IRLibraryFragment;
import com.irewind.fragments.IRMoreFragment;
import com.irewind.fragments.IRPeopleFragment;
import com.irewind.fragments.IRRewindFunctionalityFragment;
import com.irewind.fragments.IRVideoDetailsFragment;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

                IRLibraryFragment.class,
                IRVideoDetailsFragment.class,

                IRPeopleFragment.class,

                IRRewindFunctionalityFragment.class,

                IRAccountFragment.class,
                IRAccountPersonalFragment.class,
                IRAccountPasswordFragment.class,
                IRAccountNotificationFragment.class,

                IRMoreFragment.class
        },

        library = true
)

public class UIModule {

    @Singleton
    @Provides
    public ImageLoaderConfiguration provideImageLoaderConfig(Context context) {
        return new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
    }

    @Singleton
    @Provides
    public ImageLoader provideImageLoader(ImageLoaderConfiguration config) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        return imageLoader;
    }
}
