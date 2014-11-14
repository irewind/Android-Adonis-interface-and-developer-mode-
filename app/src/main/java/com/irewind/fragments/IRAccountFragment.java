package com.irewind.fragments;

import android.content.Intent;
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
import com.irewind.ui.methods.PagerItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRAccountFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    @InjectView(R.id.listViewAccount)
    ListView mAccountListView;
    @InjectView(R.id.btnLogout)
    Button mLogout;

    private IRAccountAdapter mAccountAdapter;

    public static IRAccountFragment newInstance() {
        IRAccountFragment fragment = new IRAccountFragment();
        return fragment;
    }

    public IRAccountFragment() {
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
        return inflater.inflate(R.layout.fragment_iraccount, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        setupAdapter();
        mLogout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.account));
    }

    private void setupAdapter(){
        mAccountListView.setOnItemClickListener(this);
        List<String> dataList = new ArrayList<String>();
        dataList.add("Change personal data");
        dataList.add("Change password");
        dataList.add("Change notification settings");

        mAccountAdapter = new IRAccountAdapter(getActivity(), R.layout.row_account_list, dataList);
        mAccountListView.setAdapter(mAccountAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                IRTabActivity.mAccountFragment = IRAccountPersonalFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
            case 1:
                IRTabActivity.mAccountFragment = IRAccountPasswordFragment.newInstance();
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft2 = fragmentManager2.beginTransaction();
                ft2.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft2.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
            case 2:
                IRTabActivity.mAccountFragment = IRAccountNotificationFragment.newInstance();
                FragmentManager fragmentManager3 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft3 = fragmentManager3.beginTransaction();
                ft3.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft3.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), IRLoginActivity.class);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
