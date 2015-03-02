package com.irewind.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.fragments.IRVideoDetailsFragment;
import com.irewind.sdk.model.Video;

public class IRMovieActivity extends IRBaseActivity {

    public ImageButton abBack, abSearch;
    public TextView abTitle;
    public Button abAction;
    private IRVideoDetailsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iraccount_general_layout);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_bar));
        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setLogo(0);



        View view = getLayoutInflater().inflate(R.layout.actionbar, null);
        abTitle = (TextView) view.findViewById(R.id.title);
        abSearch = (ImageButton) view.findViewById(R.id.btn_search);
        abBack = (ImageButton) view.findViewById(R.id.btn_back);
        abAction = (Button) view.findViewById(R.id.action);
        getSupportActionBar().setCustomView(view);

        abTitle.setText(getString(R.string.movies));
        abSearch.setVisibility(View.GONE);
        abAction.setVisibility(View.GONE);

        abBack.setVisibility(View.VISIBLE);
        abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                if (mFragment != null) {
                    mFragment.stopPlayback();
                }
            }
        });

        int orientation = getScreenOrientation();

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getSupportActionBar().show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState == null) {
            mFragment = new IRVideoDetailsFragment();
            mFragment.setVideo((Video) getIntent().getSerializableExtra("video"));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getSupportActionBar().show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mFragment != null) {
            mFragment.stopPlayback();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else {
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }
}
