package com.irewind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRVideoGridAdapter;
import com.irewind.common.IOnSearchCallback;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoListEvent;
import com.irewind.sdk.api.event.VideoListFailEvent;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Video;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRLibraryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.moviesGridView)
    PullToRefreshGridView mPullToRefreshGridView;
    @InjectView(R.id.emptyTextGrid)
    TextView emptyText;

    private GridView mGridView;
    private IRVideoGridAdapter mAdapter;

    @Inject
    ApiClient apiClient;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    private String searchQuery = "";

    public static IRLibraryFragment newInstance() {
        IRLibraryFragment fragment = new IRLibraryFragment();
        return fragment;
    }

    public IRLibraryFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irlibrary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mPullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                fetch(0);
            }
        });

        mPullToRefreshGridView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (lastPageListed + 1 < numberOfPagesAvailable) {
                    fetch(lastPageListed + 1);
                }
            }
        });

        mGridView = mPullToRefreshGridView.getRefreshableView();
        mGridView.setOnItemClickListener(this);

        mAdapter = new IRVideoGridAdapter(getActivity(), R.layout.cell_movie_grid);
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abAction.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.movies));
        IRTabActivity.abSearch.setVisibility(View.VISIBLE);
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

        apiClient.getEventBus().unregister(this);

        apiClient.cancelListVideosTask();
        apiClient.cancelSearchVideosTask();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Video video = mAdapter.getItem(position);

        IRVideoDetailsFragment fragment = IRVideoDetailsFragment.newInstance();
        fragment.video = video;

        IRTabActivity.mLibraryFragment = fragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.replace(R.id.container, IRTabActivity.mLibraryFragment)
                .disallowAddToBackStack()
                .commit();
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
        if (searchQuery != null && searchQuery.length() > 0) {
            apiClient.searchVideos(searchQuery, page, 200);
        } else {
            apiClient.listVideos(page, 200);
        }
    }

    @Subscribe
    public void onEvent(VideoListEvent event) {
        List<Video> videos = event.videos;
        PageInfo pageInfo = event.pageInfo;

        if (pageInfo.getNumber() == 0) {
            mAdapter.setVideos(videos);

            if (mPullToRefreshGridView.isRefreshing()) {
                mPullToRefreshGridView.onRefreshComplete();
            }
        } else {
            mAdapter.appendVideos(videos);
        }

        lastPageListed = pageInfo.getNumber();
        numberOfPagesAvailable = pageInfo.getTotalPages();

        if (mGridView.getAdapter() == null) {
            mGridView.setAdapter(mAdapter);
            mGridView.setEmptyView(emptyText);
        }
    }

    @Subscribe
    public void onEvent(VideoListFailEvent event) {
        if (mPullToRefreshGridView.isRefreshing()) {
            mPullToRefreshGridView.onRefreshComplete();
        }

        if (mGridView.getAdapter() == null) {
            mGridView.setAdapter(mAdapter);
            mGridView.setEmptyView(emptyText);
        }
    }
}
