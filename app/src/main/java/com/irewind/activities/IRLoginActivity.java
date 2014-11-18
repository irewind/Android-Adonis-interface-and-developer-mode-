package com.irewind.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.model.people.Person;
import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.SessionClient;
import com.irewind.sdk.api.event.SessionOpenFailedEvent;
import com.irewind.sdk.api.event.SessionOpenedEvent;
import com.irewind.sdk.api.event.SocialLoginFailedEvent;
import com.irewind.utils.CheckUtil;
import com.irewind.utils.Log;
import com.irewind.utils.ProjectFonts;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class IRLoginActivity extends PlusBaseActivity implements LoaderCallbacks<Cursor>, OnClickListener {

    private static final String TAG = "Login";

    // UI references.
    @InjectView(R.id.login_form)
    View mLoginFormView;
    @InjectView(R.id.login_progress)
    View mProgressView;
    @InjectView(R.id.email)
    AutoCompleteTextView mEmailView;
    @InjectView(R.id.password)
    EditText mPasswordView;
    @InjectView(R.id.email_login_form)
    View mEmailLoginFormView;
    @InjectView(R.id.plus_sign_in_button)
    SignInButton mPlusSignInButton;
    @InjectView(R.id.email_sign_in_button)
    Button mSignButton;
    @InjectView(R.id.forgot_password)
    TextView mForgotPassword;
    @InjectView(R.id.register)
    TextView mRegister;
    @InjectView(R.id.email_sign_in_facebook)
    Button mSignFacebook;
    @InjectView(R.id.email_sign_in_google)
    Button mSignGoogle;
    @InjectView(R.id.facebook_sign_in_button)
    LoginButton mFacebookLogin;

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Inject
    protected SessionClient sessionClient;

    @Inject
    protected ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_irlogin);

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

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(this);
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return;
        }
        populateAutoComplete();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignFacebook.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mSignGoogle.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mEmailView.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mPasswordView.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mSignButton.setTypeface(ProjectFonts.newInstance(this).getNormal());

        mSignButton.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mSignFacebook.setOnClickListener(this);
        mSignGoogle.setOnClickListener(this);

        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        if (heightDiff > 180) { // if more than 100 pixels, its probably a keyboard...
                            findViewById(R.id.media).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.media).setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        sessionClient.getEventBus().register(this);

        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sessionClient.getEventBus().unregister(this);

        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
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

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Request request = Request.newMeRequest(session,
                    new Request.GraphUserCallback() {
                        // callback after Graph API response with user object

                        @Override
                        public void onCompleted(GraphUser user,
                                                Response response) {
                            if (user != null && user.asMap() != null && user.asMap().get("email") != null) {

                            } else {

                            }
                        }
                    });
            Request.executeBatchAsync(request);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plus_sign_in_button:
                signIn();
                break;
            case R.id.email_sign_in_button:
                attemptLogin();
                break;
            case R.id.forgot_password:
                Intent intentForgot = new Intent(this, IRForgotPasswordActivity.class);
                startActivity(intentForgot);
                break;
            case R.id.register:
                Intent intentRegister = new Intent(this, IRRegisterActivity.class);
                startActivity(intentRegister);
                break;
            case R.id.email_sign_in_google:
                Log.d("CLICK", "google");
                mPlusSignInButton.performClick();
                break;
            case R.id.email_sign_in_facebook:
                Log.d("CLICK", "facebook");
                mFacebookLogin.performClick();
                break;
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!CheckUtil.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!CheckUtil.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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

            sessionClient.openSession(email, password);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

    @Override
    protected void onPlusClientSignIn() {
        //Set up sign out and disconnect buttons.
        if (getPlusClient() != null && getPlusClient().getCurrentPerson() != null) {
            Log.d("PLUS_INFO", " " + getPlusClient().getCurrentPerson().getId() + " " + getPlusClient().getCurrentPerson().getDisplayName() + " " + getPlusClient().getCurrentPerson().getImage());

            Person person = getPlusClient().getCurrentPerson();
            String email = person.getDisplayName();
            String socialId = person.getId();
            String firstname = person.getName().getGivenName();
            String lastname = person.getName().getFamilyName();
            String pictureUrl = person.getImage().getUrl();
            sessionClient.loginGOOGLE(email, socialId, firstname, lastname, pictureUrl);
        } else {
            Log.d("PLUS_INFO", "is null");
        }
    }

    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    @Override
    protected void updateConnectButtonState() {
        //TODO: Update this logic to also handle the user logged in by email.
        boolean connected = getPlusClient().isConnected();

//        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        mEmailLoginFormView.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {

    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(IRLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(SessionOpenedEvent event) {
        showProgress(false);

        String email = mEmailView.getText().toString();
        apiClient.getActiveUserByEmail(sessionClient.getActiveSession(), email);

        Intent intent = new Intent(IRLoginActivity.this, IRTabActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(SessionOpenFailedEvent event) {
        showProgress(false);

        Toast.makeText(getApplicationContext(), getString(R.string.error_bad_credentials), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(SocialLoginFailedEvent event) {
        showProgress(false);

        Toast.makeText(getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
    }
}



