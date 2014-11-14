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
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.irewind.R;
import com.irewind.activities.IRLoginActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRAccountAdapter;
import com.irewind.adapters.IRMoreAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRMoreFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.listViewMore)
    ListView mAccountListView;

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
        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.more));
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
}
