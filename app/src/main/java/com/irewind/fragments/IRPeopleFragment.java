package com.irewind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRPeopleAdapter;
import com.irewind.common.IOnSearchCallback;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.UserListEvent;
import com.irewind.sdk.api.event.UserListFailEvent;
import com.irewind.sdk.api.response.UserListResponse;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.util.SafeAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRPeopleFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.peopleListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;

    private ListView mListView;
    private IRPeopleAdapter mAdapter;

    @Inject
    ApiClient apiClient;

    @Inject
    ImageLoader imageLoader;

    int lastPageListed = 0;
    int numberOfPagesAvailable = 0;

    private SafeAsyncTask<UserListResponse> listTask;

    public static IRPeopleFragment newInstance() {
        IRPeopleFragment fragment = new IRPeopleFragment();
        return fragment;
    }

    public IRPeopleFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irpeople, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                fetch(0);
            }
        });
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (lastPageListed + 1 < numberOfPagesAvailable) {
                    fetch(lastPageListed + 1);
                }
            }
        });
        mListView.setOnItemClickListener(this);
        mListView.setEmptyView(emptyText);

        mAdapter = new IRPeopleAdapter(getActivity(), R.layout.row_people_list, imageLoader);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.people));
        IRTabActivity.abSearch.setVisibility(View.VISIBLE);
        IRTabActivity.abSearch.setOnClickListener(this);
        IRTabActivity.onSearchCallback = new IOnSearchCallback() {
            @Override
            public void execute() {
                //TODO set search for people
            }
        };
        if (IRTabActivity.searchItem != null)
            IRTabActivity.searchItem.collapseActionView();

        apiClient.getEventBus().register(this);
        fetch(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);

        cancelTask();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO On item click
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                IRTabActivity.searchItem.expandActionView();
                break;
        }
    }

    void fetch(int page) {
        cancelTask();
        listTask = apiClient.listUsers(page, 20);
    }

    void cancelTask() {
        if (listTask != null) {
            listTask.cancel(true);
        }
        listTask = null;
    }

    @Subscribe
    public void onEvent(UserListEvent event) {
        List<User> users = event.users;

        PageInfo pageInfo = event.pageInfo;

        if (pageInfo.getNumber() == 0) {
            mAdapter.setUsers(users);

            if (mPullToRefreshListView.isRefreshing()) {
                mPullToRefreshListView.onRefreshComplete();
            }
        } else {
            mAdapter.appendUsers(users);
        }

        lastPageListed = pageInfo.getNumber();
        numberOfPagesAvailable = pageInfo.getTotalPages();

        listTask = null;
    }

    @Subscribe
    public void onEvent(UserListFailEvent event) {
        listTask = null;

        if (mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
        }
    }
}
