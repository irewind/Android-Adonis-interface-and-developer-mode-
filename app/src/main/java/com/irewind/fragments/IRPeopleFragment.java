package com.irewind.fragments;

import android.content.Intent;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRPersonActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRPeopleAdapter;
import com.irewind.common.IOnSearchCallback;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.UserListEvent;
import com.irewind.sdk.api.event.UserListFailEvent;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.utils.AppStatus;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRPeopleFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.peopleListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;

    private ListView mListView;
    private IRPeopleAdapter mAdapter;

    @Inject
    ApiClient apiClient;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    private String searchQuery = "";

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

        mAdapter = new IRPeopleAdapter(getActivity(), R.layout.row_people_list);
        mListView.setAdapter(mAdapter);
        mPullToRefreshListView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.people));
        IRTabActivity.abSearch.setVisibility(View.VISIBLE);
        IRTabActivity.abAction.setVisibility(View.GONE);
        IRTabActivity.abSearch.setOnClickListener(this);
        IRTabActivity.onSearchCallback = new IOnSearchCallback() {
            @Override
            public void execute(String query) {
                searchQuery = query;
                fetch(0);
            }
        };

        if (IRTabActivity.searchItem != null)
            IRTabActivity.searchItem.collapseActionView();

        searchQuery = "";

        apiClient.getEventBus().register(this);
        fetch(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        mListView.setEmptyView(null);

        apiClient.getEventBus().unregister(this);

        apiClient.cancelListUsersTask();
        apiClient.cancelSearchUsersTask();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = mAdapter.getItem(position - 1);

        Intent personIntent = new Intent(getActivity(), IRPersonActivity.class);
        personIntent.putExtra("user", user);
        startActivity(personIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                if (IRTabActivity.searchView != null){
                    IRTabActivity.searchView.setQueryHint(getString(R.string.search_people));
                }
                IRTabActivity.searchItem.expandActionView();
                break;
        }
    }

    void fetch(int page) {
        if (mAdapter.getCount() == 0){
            mPullToRefreshListView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }

        if (searchQuery != null && searchQuery.length() > 0) {
            apiClient.searchUsers(searchQuery, page, 200);
        } else {
            apiClient.listUsers(page, 200);
        }
    }

    @Subscribe
    public void onEvent(UserListEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!AppStatus.getInstance(getActivity()).isOnline()){
            emptyText.setText(getString(R.string.no_internet_connection));
        } else {
            emptyText.setText(getString(R.string.no_people));
        }
        mListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

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
    }

    @Subscribe
    public void onEvent(UserListFailEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        if (!AppStatus.getInstance(getActivity()).isOnline()){
            emptyText.setText(getString(R.string.no_internet_connection));
        } else {
            emptyText.setText(getString(R.string.no_people));
        }
        mListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

        if (mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
        }
    }
}
