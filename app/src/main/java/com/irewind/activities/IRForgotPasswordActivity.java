package com.irewind.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.ResetPasswordFailEvent;
import com.irewind.sdk.api.event.ResetPasswordSuccesEvent;
import com.irewind.utils.CheckUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRForgotPasswordActivity extends IRBaseActivity implements View.OnClickListener {

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.submit)
    Button mSubmit;
    @InjectView(R.id.login_form)
    View mRecoverForm;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;
    @InjectView(R.id.errorLayout)
    RelativeLayout errorLayout;
    @InjectView(R.id.errorText)
    TextView errorText;

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
        findViewById(R.id.login_form).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (errorLayout.getVisibility() == View.VISIBLE){
                    errorLayout.setVisibility(View.INVISIBLE);
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
//            mEmail.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!CheckUtil.isEmailValid(email)) {
//            mEmail.setError(getString(R.string.error_invalid_email));
            errorText.setText(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            errorLayout.setVisibility(View.INVISIBLE);
            apiClient.resetPassword(email);
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

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(ResetPasswordSuccesEvent event) {
        showProgress(false);

        Toast.makeText(getApplicationContext(), getString(R.string.reset_password_succeded), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, IRLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(ResetPasswordFailEvent event) {
        showProgress(false);

        if (event.reason == ResetPasswordFailEvent.Reason.NoUser) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_email_account_missing), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
        }
    }
}
