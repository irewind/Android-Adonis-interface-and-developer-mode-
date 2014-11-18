package com.irewind.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.SessionClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.api.event.UserNotificationSettingsLoadedEvent;
import com.irewind.sdk.api.event.UserNotificationSettingsUpdateFailEvent;
import com.irewind.sdk.api.event.UserNotificationSettingsUpdateSuccessEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import javax.inject.Inject;

public class IRAccountNotificationFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    @InjectView(R.id.switchCommentNotifications)
    Switch switchCommentNotifications;
    @InjectView(R.id.switchLikeNotifications)
    Switch switchLikeNotifications;

    @Inject
    SessionClient sessionClient;

    @Inject
    ApiClient apiClient;

    @Inject
    ImageLoader imageLoader;

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.emailTextView)
    TextView emailTextView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static IRAccountNotificationFragment newInstance() {
        IRAccountNotificationFragment fragment = new IRAccountNotificationFragment();
        return fragment;
    }

    public IRAccountNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irnotifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        switchCommentNotifications.setChecked(sharedPreferences.getBoolean(getString(R.string.notif_comment_video), getResources().getBoolean(R.bool.default_notif_comment_video)));
        switchLikeNotifications.setChecked(sharedPreferences.getBoolean(getString(R.string.notif_like_video), getResources().getBoolean(R.bool.default_notif_like_video)));
        switchCommentNotifications.setOnCheckedChangeListener(this);
        switchLikeNotifications.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        apiClient.loadActiveUserInfo();

        IRTabActivity.abBack.setVisibility(View.VISIBLE);
        IRTabActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IRTabActivity.mAccountFragment = IRAccountFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
            }
        });
        IRTabActivity.abTitle.setText(getString(R.string.notifications));
        IRTabActivity.abSearch.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchCommentNotifications:
                apiClient.toggleCommentNotifications(sessionClient.getActiveSession(), isChecked);
                break;
            case R.id.switchLikeNotifications:
                apiClient.toggleLikeNotifications(sessionClient.getActiveSession(), isChecked);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().length() > 0) {
                imageLoader.displayImage(user.getPicture(), profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getFirstname() + " " + user.getLastname());
            emailTextView.setText(user.getEmail());
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            emailTextView.setText("");
        }
    }

    private void fetchUserNotificationSettings() {
        apiClient.getUserNotificationSettings(sessionClient.getActiveSession(), apiClient.getActiveUser());
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
    public void onEvent(UserNotificationSettingsLoadedEvent event) {
        editor.putBoolean(getString(R.string.notif_comment_video), event.notificationSettings.isCommentNotification());
        editor.putBoolean(getString(R.string.notif_like_video), event.notificationSettings.isLikeNotification());

        switchCommentNotifications.setOnCheckedChangeListener(null);
        switchLikeNotifications.setOnCheckedChangeListener(null);
        switchCommentNotifications.setChecked(sharedPreferences.getBoolean(getString(R.string.notif_comment_video), getResources().getBoolean(R.bool.default_notif_comment_video)));
        switchLikeNotifications.setChecked(sharedPreferences.getBoolean(getString(R.string.notif_like_video), getResources().getBoolean(R.bool.default_notif_like_video)));
        switchCommentNotifications.setOnCheckedChangeListener(this);
        switchLikeNotifications.setOnCheckedChangeListener(this);
    }

    @Subscribe
    public void onEvent(UserNotificationSettingsUpdateSuccessEvent event) {
        fetchUserNotificationSettings();
    }

    @Subscribe
    public void onEvent(UserNotificationSettingsUpdateFailEvent event) {
        fetchUserNotificationSettings();
    }
}
