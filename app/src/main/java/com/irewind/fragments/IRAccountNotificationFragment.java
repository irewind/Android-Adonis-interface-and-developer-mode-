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

import com.irewind.R;
import com.irewind.activities.IRTabActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRAccountNotificationFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    @InjectView(R.id.switchNotif1)
    Switch switchCommentVideo;
    @InjectView(R.id.switchNotif2)
    Switch switchLikeVideo;

    private SharedPreferences sp;
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
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sp.edit();
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

        switchCommentVideo.setChecked(sp.getBoolean(getString(R.string.notif_comment_video), getResources().getBoolean(R.bool.default_notif_comment_video)));
        switchLikeVideo.setChecked(sp.getBoolean(getString(R.string.notif_like_video), getResources().getBoolean(R.bool.default_notif_like_video)));
        switchCommentVideo.setOnCheckedChangeListener(this);
        switchLikeVideo.setOnCheckedChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        switch (buttonView.getId()){
            case R.id.switchNotif1:
                editor.putBoolean(getString(R.string.notif_comment_video), isChecked);
                break;
            case R.id.switchNotif2:
                editor.putBoolean(getString(R.string.notif_like_video), isChecked);
                break;
        }
    }
}
