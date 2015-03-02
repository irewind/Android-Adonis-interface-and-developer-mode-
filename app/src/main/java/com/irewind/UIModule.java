package com.irewind;

import com.irewind.activities.IRAddCommentActivity;
import com.irewind.activities.IREditVideoTitleActivity;
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
import com.irewind.fragments.IRPersonFragment;
import com.irewind.fragments.IRVideoDetailsFragment;
import com.irewind.fragments.VideoExoPlayerFragment;
import com.irewind.fragments.VideoPlayerFragment;
import com.irewind.fragments.movies.IRAboutFragment;
import com.irewind.fragments.movies.IRCommentsFragment;
import com.irewind.fragments.movies.IRRelatedFragment;
import com.irewind.fragments.movies.IRVideoSettingsFragment;

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

                IRLibraryFragment.class,
                IRVideoDetailsFragment.class,
                VideoPlayerFragment.class,
                VideoExoPlayerFragment.class,
                IRAboutFragment.class,
                IRVideoSettingsFragment.class,
                IREditVideoTitleActivity.class,
                IRCommentsFragment.class,
                IRAddCommentActivity.class,
                IRRelatedFragment.class,

                IRPeopleFragment.class,
                IRPersonFragment.class,

                IRAccountFragment.class,
                IRAccountPersonalFragment.class,
                IRAccountPasswordFragment.class,
                IRAccountNotificationFragment.class,

                IRMoreFragment.class
        },

        library = true
)

public class UIModule {

}
