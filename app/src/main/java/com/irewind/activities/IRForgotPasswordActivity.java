package com.irewind.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.SessionClient;
import com.irewind.sdk.api.event.AdminAccessTokenFailedEvent;
import com.irewind.sdk.api.event.AdminAccessTokenSuccessEvent;
import com.irewind.sdk.api.event.ResetPasswordFailedEvent;
import com.irewind.sdk.api.event.ResetPasswordSuccesEvent;
import com.irewind.utils.CheckUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRForgotPasswordActivity extends IRBaseActivity implements View.OnClickListener {

    @Inject
    SessionClient sessionClient;

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.submit)
    Button mSubmit;
    @InjectView(R.id.login_form)
    View mRecoverForm;
    @InjectView(R.id.login_progress)
    View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        Injector.inject(this);

        setContentView(R.layout.activity_irforgot_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            int statusBarHeight = (int) Math.ceil(25 * getResources().getDisplayMetrics().density);
            findViewById(R.id.activityRoot).setPadding(0, statusBarHeight, 0, 0);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        ButterKnife.inject(this);
        Injector.inject(this);

        mSubmit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sessionClient.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sessionClient.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                attemptRecover();
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

    public void attemptRecover() {

        // Reset errors.
        mEmail.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!CheckUtil.isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            sessionClient.getAdminAccessToken();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRecoverForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mRecoverForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecoverForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Subscribe
    public void onEvent(AdminAccessTokenSuccessEvent event) {
        apiClient.resetPassword(event.accessToken, mEmail.getText().toString());
    }

    @Subscribe
    public void onEvent(AdminAccessTokenFailedEvent event) {
    
    }

    @Subscribe
    public void onEvent(ResetPasswordSuccesEvent event) {
        showProgress(false);

        Intent intent = new Intent(this, IRLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(ResetPasswordFailedEvent event) {
        showProgress(false);
    }
}
