package com.irewind.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.PasswordChangeFailEvent;
import com.irewind.sdk.api.event.PasswordChangeSuccessEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.utils.AppStatus;
import com.irewind.utils.CheckUtil;
import com.squareup.picasso.Picasso;

import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRAccountPasswordFragment extends Fragment implements View.OnClickListener {

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

    @InjectView(R.id.editCurrent)
    EditText editCurrent;

    @InjectView(R.id.editNew)
    EditText editNew;

    @InjectView(R.id.editConfirm)
    EditText editConfirm;

    @InjectView(R.id.btnChange)
    Button btnChange;

    public static IRAccountPasswordFragment newInstance() {
        IRAccountPasswordFragment fragment = new IRAccountPasswordFragment();
        return fragment;
    }

    public IRAccountPasswordFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_iraccount_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        btnChange.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChange:
                change();
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
        String currentPassword = editCurrent.getText().toString();
        String newPassword = editNew.getText().toString();
        String confirmPassword = editConfirm.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(currentPassword)){
            editCurrent.setError(getString(R.string.error_field_required));
            focusView = editCurrent;
            cancel = true;
        } else if (!CheckUtil.isPasswordValid(newPassword)){
            editNew.setError(getString(R.string.error_invalid_password));
            focusView = editNew;
            cancel = true;
        } else if (!CheckUtil.isPasswordValid(newPassword, confirmPassword)){
            editConfirm.setError(getString(R.string.error_match));
            focusView = editConfirm;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        } else {
            showProgress(true);
            apiClient.changeUserPassword(apiClient.getActiveUser(), currentPassword, newPassword);
        }
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().length() > 0) {
                Picasso.with(getActivity()).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            date.setText("Joined: " + DateUtils.getRelativeTimeSpanString(user.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            date.setText("");
        }
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(UserInfoLoadedEvent event) {
        updateUserInfo(event.user);
    }

    @Subscribe
    public void onEvent(NoActiveUserEvent event) {
        updateUserInfo(null);
    }

    @Subscribe
    public void onEvent(PasswordChangeSuccessEvent event) {
        showProgress(false);

        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.change_password_succeded), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(PasswordChangeFailEvent event) {
        showProgress(false);

        if (event.reason == PasswordChangeFailEvent.Reason.WrongPassword) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_incorrect_current_password), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
        }
    }
}
