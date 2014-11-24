package com.irewind.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRMoreAdapter;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRMoreFragment extends Fragment implements AdapterView.OnItemClickListener {

    @Inject
    ApiClient apiClient;

    @Inject
    ImageLoader imageLoader;

    @InjectView(R.id.listViewMore)
    ListView mAccountListView;

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.date)
    TextView date;

    private IRMoreAdapter mMoreAdapter;

    public static IRMoreFragment newInstance() {
        IRMoreFragment fragment = new IRMoreFragment();
        return fragment;
    }

    public IRMoreFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irmore, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        setupAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        apiClient.loadActiveUserInfo();

        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abAction.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.more));
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    private void setupAdapter() {
        mAccountListView.setOnItemClickListener(this);
        List<String> dataList = new ArrayList<String>();
        dataList.add("Terms & conditions");
        dataList.add("Privacy policy");
        dataList.add("Feed-back form");

        mMoreAdapter = new IRMoreAdapter(getActivity(), R.layout.row_account_list, dataList);
        mAccountListView.setAdapter(mMoreAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                IRTabActivity.mMoreFragment = IRMoreTermsFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.container, IRTabActivity.mMoreFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
            case 1:
                IRTabActivity.mMoreFragment = IRMorePrivacyFragment.newInstance();
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft2 = fragmentManager2.beginTransaction();
                ft2.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft2.replace(R.id.container, IRTabActivity.mMoreFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
            case 2:
                PackageInfo pInfo = null;
                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                String uriText =
                        "mailto:contact@irewind.com" +
                                "?subject=" + "[iRewind] Feedback" +
                                "&body=" + "iRewind version: " + version + "\n\n";

                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                startActivity(Intent.createChooser(sendIntent, "Send email"));
                break;
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @Subscribe
    public void onEvent(UserInfoLoadedEvent event) {
        updateUserInfo(event.user);
    }

    @Subscribe
    public void onEvent(NoActiveUserEvent event) {
        updateUserInfo(null);
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().length() > 0) {
                imageLoader.displayImage(user.getPicture(), profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            date.setText(user.getCreatedDate() + "");
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            date.setText("");
        }
    }
}
