package com.irewind.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.NotificationSettingsListSuccessEvent;
import com.irewind.sdk.api.event.NotificationSettingsUpdateFailEvent;
import com.irewind.sdk.api.event.NotificationSettingsUpdateSuccessEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRAccountNotificationFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @InjectView(R.id.switchCommentNotifications)
    CheckBox switchCommentNotifications;
    @InjectView(R.id.switchLikeNotifications)
    CheckBox switchLikeNotifications;
    @InjectView(R.id.switchShareNotifications)
    CheckBox switchShareNotifications;
    @InjectView(R.id.switchMessageNotifications)
    CheckBox switchMessageNotifications;

    @InjectView(R.id.checkLikeVideo)
    Button checkLikeVideo;
    @InjectView(R.id.checkCommentVideo)
    Button checkCommentVideo;
    @InjectView(R.id.checkShareVideo)
    Button checkShareVideo;
    @InjectView(R.id.checkMessageVideo)
    Button checkMessageVideo;

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.date)
    TextView date;

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
        switchShareNotifications.setChecked(sharedPreferences.getBoolean(getString(R.string.notif_share_video), getResources().getBoolean(R.bool.default_notif_share_video)));
        switchMessageNotifications.setChecked(sharedPreferences.getBoolean(getString(R.string.notif_message_video), getResources().getBoolean(R.bool.default_notif_message_video)));
        switchCommentNotifications.setOnCheckedChangeListener(this);
        switchLikeNotifications.setOnCheckedChangeListener(this);
        switchShareNotifications.setOnCheckedChangeListener(this);
        switchMessageNotifications.setOnCheckedChangeListener(this);

        checkCommentVideo.setOnClickListener(this);
        checkLikeVideo.setOnClickListener(this);
        checkShareVideo.setOnClickListener(this);
        checkMessageVideo.setOnClickListener(this);

        checkCommentVideo.setSoundEffectsEnabled(false);
        checkLikeVideo.setSoundEffectsEnabled(false);
        checkShareVideo.setSoundEffectsEnabled(false);
        checkMessageVideo.setSoundEffectsEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        apiClient.loadActiveUserInfo();
        fetchUserNotificationSettings();

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
        IRTabActivity.abTitle.setText(getString(R.string.notif_email));
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abAction.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkCommentVideo:
                switchCommentNotifications.setChecked(!switchCommentNotifications.isChecked());
                break;
            case R.id.checkLikeVideo:
                switchLikeNotifications.setChecked(!switchLikeNotifications.isChecked());
                break;
            case R.id.checkShareVideo:
                switchShareNotifications.setChecked(!switchShareNotifications.isChecked());
                break;
            case R.id.checkMessageVideo:
                switchMessageNotifications.setChecked(!switchMessageNotifications.isChecked());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchCommentNotifications:
                apiClient.toggleCommentNotifications(isChecked);
                break;
            case R.id.switchLikeNotifications:
                apiClient.toggleLikeNotifications(isChecked);
                break;
            case R.id.switchShareNotifications:
                apiClient.toggleShareNotifications(isChecked);
                break;
            case R.id.switchMessageNotifications:
                apiClient.toggleMessageNotifications(isChecked);
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

    private void fetchUserNotificationSettings() {
        apiClient.getUserNotificationSettings(apiClient.getActiveUser());
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
    public void onEvent(NotificationSettingsListSuccessEvent event) {

        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (editor == null)
            editor = sharedPreferences.edit();

        editor.putBoolean(getString(R.string.notif_comment_video), event.notificationSettings.commentNotificationEnabled());
        editor.putBoolean(getString(R.string.notif_like_video), event.notificationSettings.likeNotificationEnabled());
        editor.putBoolean(getString(R.string.notif_share_video), event.notificationSettings.shareNotificationEnabled());
        editor.putBoolean(getString(R.string.notif_message_video), event.notificationSettings.msgNotificationEnabled());
        editor.commit();

        switchCommentNotifications.setOnCheckedChangeListener(null);
        switchLikeNotifications.setOnCheckedChangeListener(null);
        switchShareNotifications.setOnCheckedChangeListener(null);
        switchMessageNotifications.setOnCheckedChangeListener(null);
        switchCommentNotifications.setChecked(event.notificationSettings.commentNotificationEnabled());
        switchLikeNotifications.setChecked(event.notificationSettings.likeNotificationEnabled());
        switchShareNotifications.setChecked(event.notificationSettings.shareNotificationEnabled());
        switchMessageNotifications.setChecked(event.notificationSettings.msgNotificationEnabled());
        switchCommentNotifications.setOnCheckedChangeListener(this);
        switchLikeNotifications.setOnCheckedChangeListener(this);
        switchShareNotifications.setOnCheckedChangeListener(this);
        switchMessageNotifications.setOnCheckedChangeListener(this);
    }

    @Subscribe
    public void onEvent(NotificationSettingsUpdateSuccessEvent event) {
        fetchUserNotificationSettings();
    }

    @Subscribe
    public void onEvent(NotificationSettingsUpdateFailEvent event) {
        fetchUserNotificationSettings();
    }
}
