package com.irewind.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRLoginActivity;
import com.irewind.common.KeyboardDismisser;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.UserDeleteFailEvent;
import com.irewind.sdk.api.event.UserDeleteSuccessEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.api.event.UserInfoUpdateFailEvent;
import com.irewind.sdk.api.event.UserInfoUpdateSuccessEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.utils.AppStatus;
import com.irewind.utils.CheckUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import utils.IrewindBackend;


public class IRAccountPersonalFragment extends Fragment implements View.OnClickListener {

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.date)
    TextView date;

    @InjectView(R.id.contentView)
    View contentView;
    @InjectView(R.id.progressView)
    View progressView;

    @InjectView(R.id.editFirst)
    EditText mFirst;
    @InjectView(R.id.editLast)
    EditText mLast;

    @InjectView(R.id.btnDelete)
    Button btnDelete;

    @InjectView(R.id.btnChange)
    Button btnChange;

    public static IRAccountPersonalFragment newInstance() {
        IRAccountPersonalFragment fragment = new IRAccountPersonalFragment();
        return fragment;
    }

    public IRAccountPersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_iraccount_personal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        btnChange.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        apiClient.loadActiveUserInfo();
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);

        KeyboardDismisser.hideSoftKeyboard(this.getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChange:
                change();
                break;
            case R.id.btnDelete:
                new AlertDialog.Builder(this.getActivity())
                        .setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.delete_confirmation_msg))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }
    }

    /**
     * Shows the progress view and hides the content view.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
        contentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void change() {
        if (!AppStatus.getInstance(getActivity()).isOnline()){
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
        View focusView = null;
        boolean cancel = false;

        String firstname = mFirst.getText().toString();
        String lastname = mLast.getText().toString();

        if (TextUtils.isEmpty(firstname)){
            mFirst.setError(getString(R.string.error_field_required));
            focusView = mFirst;
            cancel = true;
        } else if (firstname.length() > 20) {
            mFirst.setError(getString(R.string.error_name_too_long));
            focusView = mFirst;
            cancel = true;
        } else if (TextUtils.isEmpty(lastname)){
            mLast.setError(getString(R.string.error_field_required));
            focusView = mLast;
            cancel = true;
        } else if (lastname.length() > 20) {
            mLast.setError(getString(R.string.error_name_too_long));
            focusView = mLast;
            cancel = true;
        }

        mFirst.setError(null);
        mLast.setError(null);

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            apiClient.updateUser(apiClient.getActiveUser(), firstname, lastname);
        }
    }

    public void delete() {
        if (!AppStatus.getInstance(getActivity()).isOnline()){
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
        showProgress(true);

        apiClient.deleteUser(apiClient.getActiveUser());

        if (isCheckInEnabled()) {
            IrewindBackend.Instance.stopRecording();
        }
        IrewindBackend.Instance = null;
    }

    public boolean isCheckInEnabled() {
        return IrewindBackend.Instance != null && IrewindBackend.Instance.recordingState;
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                Picasso.with(getActivity()).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                Date parsedDate = fromUser.parse(user.getCreatedDate());
                date.setText("Joined: " + DateUtils.getRelativeTimeSpanString(parsedDate.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        //    date.setText("Joined: " + user.getCreatedDate());
            mFirst.setText(user.getFirstname());
            mLast.setText(user.getLastname());
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            date.setText("");
            mFirst.setText("");
            mLast.setText("");
        }
    }

    // --- Events ---//

    @Subscribe
    public void onEvent(UserInfoLoadedEvent event) {
        updateUserInfo(event.user);
    }

    @Subscribe
    public void onEvent(NoActiveUserEvent event) {
        updateUserInfo(null);
    }

    @Subscribe
    public void onEvent(UserInfoUpdateFailEvent event) {
        showProgress(false);

        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(UserInfoUpdateSuccessEvent event) {
        showProgress(false);

        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.profile_update_succeded), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(UserDeleteSuccessEvent event) {
        apiClient.closeSessionAndClearTokenInformation();

        Intent intent = new Intent(getActivity(), IRLoginActivity.class);
        intent.putExtra(IRLoginActivity.EXTRA_SHOULD_LOGOUT_FIRST, true);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(UserDeleteFailEvent event) {
        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
    }
}
