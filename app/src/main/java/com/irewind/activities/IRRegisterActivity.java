package com.irewind.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.RegisterFailEvent;
import com.irewind.sdk.api.event.RegisterSuccessEvent;
import com.irewind.utils.AppStatus;
import com.irewind.utils.CheckUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRRegisterActivity extends IRBaseActivity implements View.OnClickListener {

    @InjectView(R.id.progress)
    CircularProgressBar progressBar;
    @InjectView(R.id.first)
    EditText mFirst;
    @InjectView(R.id.last)
    EditText mLast;
    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.confirm)
    EditText mConfirm;
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
    @InjectView(R.id.register)
    Button mRegister;
    @InjectView(R.id.login_form)
    View mRegisterForm;
    @InjectView(R.id.webView)
    WebView mWebView;
    @InjectView(R.id.login)
    TextView mLogin;
    @InjectView(R.id.titleSlide)
    TextView mTitleSlide;
    @InjectView(R.id.errorLayout)
    RelativeLayout errorLayout;
    @InjectView(R.id.errorText)
    TextView errorText;
    @InjectView(R.id.textBy)
    TextView textBy;
    @InjectView(R.id.linearPopup)
    LinearLayout linearLayout;

    @Inject
    ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        Injector.inject(this);

        setContentView(R.layout.activity_irregister);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            int statusBarHeight = (int) Math.ceil(25 * getResources().getDisplayMetrics().density);
//            findViewById(R.id.sliding_layout).setPadding(0, statusBarHeight, 0, 0);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//        }
//
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setNavigationBarTintEnabled(true);

        ButterKnife.inject(this);
        Injector.inject(this);

        mTerms.setOnClickListener(this);
        mPrivacy.setOnClickListener(this);
        mCookie.setOnClickListener(this);
        mClosePanel.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        findViewById(R.id.login_form).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (errorLayout.getVisibility() == View.VISIBLE){
                    errorLayout.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        mSlidingUpLayout.setPanelHeight(0);
        mSlidingUpLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                mSlidingUpLayout.setSlidingEnabled(true);
                textBy.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
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
        mConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.confirm || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.terms:
                mWebView.loadUrl(getString(R.string.terms_link));
                mTitleSlide.setText(getString(R.string.term_cond));
                mSlidingUpLayout.expandPanel();
                break;
            case R.id.privacy:
                mWebView.loadUrl(getString(R.string.privacy_policy));
                mTitleSlide.setText(getString(R.string.policy_s));
                mSlidingUpLayout.expandPanel();
                break;
            case R.id.cookie:
                mWebView.loadUrl(getString(R.string.cookie_link));
                mTitleSlide.setText(getString(R.string.cookie_s));
                mSlidingUpLayout.expandPanel();
                break;
            case R.id.closePanel:
                mSlidingUpLayout.setSlidingEnabled(true);
                mSlidingUpLayout.collapsePanel();
                break;
            case R.id.register:
                attemptRegister();
                break;
            case R.id.login:
                finish();
                break;
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void attemptRegister() {
//        if (mAuthTask != null) {
//            return;
//        }
        if (!AppStatus.getInstance(this).isOnline()){
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        // Reset errors.
        mEmail.setError(null);
        mFirst.setError(null);
        mLast.setError(null);
        mPassword.setError(null);
        mConfirm.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String confirm = mConfirm.getText().toString();
        String firstname = mFirst.getText().toString();
        String lastname = mLast.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(firstname)) {
//            mFirst.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_missing_firstname));
            focusView = mFirst;
            cancel = true;
        } else if (TextUtils.isEmpty(lastname)) {
//            mLast.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_missing_lastname));
            focusView = mLast;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
//            mEmail.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_email_missing));
            focusView = mEmail;
            cancel = true;
        } else if (!CheckUtil.isEmailValid(email)) {
//            mEmail.setError(getString(R.string.error_invalid_email));
            errorText.setText(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
//            mPassword.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_password_missing));
            focusView = mPassword;
            cancel = true;
        } else if (!CheckUtil.isPasswordValid(password)) {
//            mPassword.setError(getString(R.string.error_invalid_password));
            errorText.setText(getString(R.string.error_invalid_password_2));
            focusView = mPassword;
            cancel = true;
        } else if (!CheckUtil.isPasswordValid(password, confirm)) {
//            mConfirm.setError(getString(R.string.error_match));
            errorText.setText(getString(R.string.error_match));
            focusView = mConfirm;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.INVISIBLE);
            showProgress(true);
            apiClient.register(email, firstname, lastname, password);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpLayout.isPanelExpanded()) {
            mSlidingUpLayout.setSlidingEnabled(true);
            mSlidingUpLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(RegisterSuccessEvent event) {
        showProgress(false);

        Toast.makeText(getApplicationContext(), getString(R.string.registration_pending_approval), Toast.LENGTH_LONG).show();

        onBackPressed();
    }

    @Subscribe
    public void onEvent(RegisterFailEvent event) {
        if (event.reason == RegisterFailEvent.Reason.UserExists) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_account_exists), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
        }
    }
}
