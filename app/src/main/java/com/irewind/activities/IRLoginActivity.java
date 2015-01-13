package com.irewind.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.SessionOpenFailEvent;
import com.irewind.sdk.api.event.SessionOpenedEvent;
import com.irewind.utils.AppStatus;
import com.irewind.utils.CheckUtil;
import com.irewind.utils.Log;
import com.irewind.utils.ProjectFonts;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRLoginActivity extends IRBaseActivity implements OnClickListener {

    private static final String TAG = IRLoginActivity.class.getSimpleName();

    public static final String EXTRA_SHOULD_LOGOUT_FIRST = "should_logout_first";

    // UI references.
    @InjectView(R.id.login_form)
    View mLoginFormView;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;
    @InjectView(R.id.email)
    EditText mEmailView;
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
    @InjectView(R.id.errorLayout)
    RelativeLayout errorLayout;
    @InjectView(R.id.errorText)
    TextView errorText;

    private int currentHeight, prevHeight;

    private String userEmail = "";
    private boolean shouldLogoutFirst;

    // --- Facebook --- //

    private UiLifecycleHelper fbUIHelper;

    // --- Google --- //

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int GPLUS_RESOLUTION_REQ_CODE = 49404;

    // A flag to stop multiple dialogues appearing for the user
    private boolean gPlusAutoResolveOnFail;

    // This is the helper object that connects to Google Play Services.
    private PlusClient gPlusClient;
    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult gPlusConnectionResult;

    @Inject
    protected ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_irlogin);

        ButterKnife.inject(this);
        Injector.inject(this);

        Intent intent = getIntent();
        Bundle extraBundle = intent.getExtras();

        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        currentHeight = heightDiff;
//                        if (heightDiff > 180) { // if more than 100 pixels, its probably a keyboard...
//                            findViewById(R.id.media).setVisibility(View.GONE);
//                        } else {
//                            findViewById(R.id.media).setVisibility(View.VISIBLE);
//                        }
                        if (currentHeight != prevHeight) {
                            if (currentHeight > prevHeight && prevHeight != 0) {
                                findViewById(R.id.media).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.media).setVisibility(View.VISIBLE);
                            }
                            prevHeight = currentHeight;
                        }
                    }
                });

        findViewById(R.id.login_form).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (errorLayout.getVisibility() == View.VISIBLE) {
                    errorLayout.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        mEmailView.setTypeface(ProjectFonts.newInstance(this).getNormal());

        mPasswordView.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL || id == EditorInfo.IME_ACTION_DONE) {
                    signIn();
                    return true;
                }
                return false;
            }
        });

        mSignButton.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mSignButton.setOnClickListener(this);

        mForgotPassword.setOnClickListener(this);

        mRegister.setOnClickListener(this);

        mSignFacebook.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mSignFacebook.setOnClickListener(this);

        mSignGoogle.setTypeface(ProjectFonts.newInstance(this).getNormal());
        mSignGoogle.setOnClickListener(this);

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(this);
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
        }

        setupFB(savedInstanceState);
        setupGPlus();

        shouldLogoutFirst = extraBundle != null && extraBundle.getBoolean(EXTRA_SHOULD_LOGOUT_FIRST);
        if (shouldLogoutFirst) {
            signOutFacebook();
            signOutPlusClient();
            shouldLogoutFirst = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Session fbSession = Session.getActiveSession();
        if (fbSession != null) {
            onFBSessionStateChange(fbSession, fbSession.getState(), null);
        }

        fbUIHelper.onResume();

        apiClient.getEventBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        fbUIHelper.onPause();

        apiClient.getEventBus().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gPlusDisconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fbUIHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fbUIHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden ==
                Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        Session.getActiveSession().onActivityResult(this, requestCode, responseCode, intent);

        updateGPlusButtonState();
        if (requestCode == GPLUS_RESOLUTION_REQ_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            gPlusAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            gPlusConnect();
        } else if (requestCode == GPLUS_RESOLUTION_REQ_CODE && responseCode != RESULT_OK) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            showProgress(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                signIn();
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
                signInGoogle();
                break;
            case R.id.email_sign_in_facebook:
                mFacebookLogin.performClick();
                break;
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

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    protected void updateGPlusButtonState() {
        boolean connected = gPlusClient.isConnected();
        mEmailLoginFormView.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void signIn() {
        if (!AppStatus.getInstance(this).isOnline()) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
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
//            mEmailView.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_email_missing));
            focusView = mEmailView;
            cancel = true;
        } else if (!CheckUtil.isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
            errorText.setText(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
//            mPasswordView.setError(getString(R.string.error_field_required));
            errorText.setText(getString(R.string.error_password_missing));
            focusView = mPasswordView;
            cancel = true;
        } else if (!CheckUtil.isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
            errorText.setText(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
            this.userEmail = email;
            apiClient.openSession(email, password);
        }
    }

    // --- FACEBOOK --- //

    private void setupFB(Bundle savedInstanceState) {
        fbUIHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(final Session session, final SessionState state, final Exception exception) {
                onFBSessionStateChange(session, state, exception);
            }
        });
        fbUIHelper.onCreate(savedInstanceState);

        mFacebookLogin.setReadPermissions(Arrays.asList("email"));
    }

    protected boolean isFacebookLoggedIn() {
        Session session = Session.getActiveSession();
        return session != null && session.isOpened();
    }

    protected void signOutFacebook() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        }
    }

    private void onFBSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            com.irewind.utils.Log.i(TAG, "Logged in...");
            Request request = Request.newMeRequest(session,
                    new Request.GraphUserCallback() {
                        // callback after Graph API response with user object

                        @Override
                        public void onCompleted(GraphUser user,
                                                Response response) {
                            if (user != null) {
                                Map<String, Object> userMap = user.asMap();
                                if (userMap != null) {
                                    String email = (String) userMap.get("email");
                                    String firstname = (String) userMap.get("first_name");
                                    String lastname = (String) userMap.get("last_name");
                                    String id = (String) userMap.get("id");
                                    String picture = "http://graph.facebook.com/" + id + "/picture?type=large";

                                    userEmail = email;

                                    apiClient.loginFACEBOOK(userEmail, id, firstname, lastname, picture);
                                }
                            }
                        }
                    });
            Request.executeBatchAsync(request);
        } else if (state.isClosed()) {
            com.irewind.utils.Log.i(TAG, "Logged out...");
        }
    }

    // --- GOOGLE --- //

    private void setupGPlus() {
        // Initialize the PlusClient connection.
        // Scopes indicate the information about the user your application will be able to access.
        PlusClient.Builder gPlusBuilder = new PlusClient.Builder(this, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                updateGPlusButtonState();
                showProgress(false);
                onGPlusSignIn();
            }

            @Override
            public void onDisconnected() {
                updateGPlusButtonState();
                onGPlusSignOut();
            }
        }, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                updateGPlusButtonState();
                showProgress(false);

                // Most of the time, the connection will fail with a user resolvable result. We can store
                // that in our gPlusConnectionResult property ready to be used when the user clicks the
                // sign-in button.
                if (connectionResult.hasResolution()) {
                    gPlusConnectionResult = connectionResult;
                    if (gPlusAutoResolveOnFail) {
                        // This is a local helper function that starts the resolution of the problem,
                        // which may be showing the user an account chooser or similar.
                        startGPlusResolution();
                    }
                }
            }
        });
        gPlusBuilder.setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_ME, Scopes.PROFILE, "https://www.googleapis.com/auth/userinfo.email");
        gPlusClient = gPlusBuilder.build();
    }

    /**
     * Try to sign in the user.
     */
    public void signInGoogle() {
        if (!gPlusClient.isConnected()) {
            // Show the dialog as we are now signing in.
            showProgress(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            gPlusAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (gPlusConnectionResult != null) {
                startGPlusResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                gPlusConnect();
            }

            updateGPlusButtonState();
        }
    }

    /**
     * Connect the {@link PlusClient} only if a connection isn't already in progress.  This will
     * call back to { onConnected(android.os.Bundle)} or
     * { #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void gPlusConnect() {
        if (!gPlusClient.isConnected() && !gPlusClient.isConnecting()) {
            gPlusClient.connect();
        }
    }

    /**
     * A helper method to flip the gPlusResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startGPlusResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            gPlusAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            gPlusConnectionResult.startResolutionForResult(this, GPLUS_RESOLUTION_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            gPlusConnectionResult = null;
            gPlusConnect();
        }
    }

    protected void onGPlusSignIn() {

        //Set up sign out and disconnect buttons.
        if (gPlusClient != null && gPlusClient.getCurrentPerson() != null) {

            Log.d("PLUS_INFO", gPlusClient.getAccountName() + " " + gPlusClient.getCurrentPerson().getId() + " " + gPlusClient.getCurrentPerson().getDisplayName() + " " + gPlusClient.getCurrentPerson().getImage());

            Person person = gPlusClient.getCurrentPerson();
            String email = gPlusClient.getAccountName();
            String socialId = person.getId();
            String firstname = person.getName().getGivenName();
            String lastname = person.getName().getFamilyName();
            String pictureUrl = person.getImage().getUrl();

            userEmail = email;

            apiClient.loginGOOGLE(userEmail, socialId, firstname, lastname, pictureUrl);
        } else {
            Log.d("PLUS_INFO", "is null");
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOutPlusClient() {

        // We only want to sign out if we're connected.
        if (gPlusClient.isConnected()) {
            // Clear the default account in order to allow the user to potentially choose a
            // different account from the account chooser.
            gPlusClient.clearDefaultAccount();

            // Disconnect from Google Play Services, then reconnect in order to restart the
            // process from scratch.
            gPlusDisconnect();

            android.util.Log.v(TAG, "Sign out successful!");

            updateGPlusButtonState();
        }
    }

    /**
     * Disconnect the {@link PlusClient} only if it is connected (otherwise, it can throw an error.)
     * This will call back to { #onDisconnected()}.
     */
    private void gPlusDisconnect() {
        if (gPlusClient.isConnected()) {
            gPlusClient.disconnect();
        }
    }

    protected void onGPlusSignOut() {

    }

    // --- Events --- //

    @Subscribe
    public void onEvent(SessionOpenedEvent event) {
        showProgress(false);

        apiClient.getUserByEmail(userEmail);

        Intent intent = new Intent(IRLoginActivity.this, IRTabActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(SessionOpenFailEvent event) {
        showProgress(false);

        if (event.reason == SessionOpenFailEvent.Reason.BadCredentials) {
//            Toast.makeText(getApplicationContext(), getString(R.string.error_bad_credentials), Toast.LENGTH_LONG).show();
            errorText.setText(getString(R.string.error_bad_credentials));
            errorLayout.setVisibility(View.VISIBLE);
        } else if (event.reason == SessionOpenFailEvent.Reason.Unknown) {
            if (event.message != null) {
                Toast.makeText(getApplicationContext(), event.message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_bad_credentials), Toast.LENGTH_LONG).show();
            }
        }

        signOutFacebook();
        signOutPlusClient();
    }
}
