package com.irewind.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.utils.Log;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRRegisterActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.login_progress)
    View mProgressView;
    @InjectView(R.id.first)
    EditText mFirst;
    @InjectView(R.id.last)
    EditText mLast;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingUpLayout;
    @InjectView(R.id.terms)
    TextView mTerms;
    @InjectView(R.id.privacy)
    TextView mPrivacy;
    @InjectView(R.id.cookie)
    TextView mCookie;
    @InjectView(R.id.closePanel)
    Button mClosePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irregister);
        ButterKnife.inject(this);

        mTerms.setOnClickListener(this);
        mPrivacy.setOnClickListener(this);
        mCookie.setOnClickListener(this);
        mClosePanel.setOnClickListener(this);

        mSlidingUpLayout.setPanelHeight(0);
        mSlidingUpLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                mSlidingUpLayout.setSlidingEnabled(true);
            }

            @Override
            public void onPanelExpanded(View view) {
                mSlidingUpLayout.setSlidingEnabled(false);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.terms:
                mSlidingUpLayout.expandPanel();
                break;
            case R.id.privacy:
                mSlidingUpLayout.expandPanel();
                break;
            case R.id.cookie:
                mSlidingUpLayout.expandPanel();
                break;
            case R.id.closePanel:
                mSlidingUpLayout.setSlidingEnabled(true);
                mSlidingUpLayout.collapsePanel();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpLayout.isPanelExpanded()){
            mSlidingUpLayout.setSlidingEnabled(true);
            mSlidingUpLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }
}
