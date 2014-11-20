package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.adapters.IRRelatedAdapter;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoListEvent;
import com.irewind.sdk.api.response.VideoListResponse;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.Video;
import com.irewind.sdk.util.SafeAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRRelatedFragment extends Fragment {

    @InjectView(R.id.relatedListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;

    private ListView mListView;
    private IRRelatedAdapter mAdapter;

    public Video video;

    @Inject
    ApiClient apiClient;

    @Inject
    ImageLoader imageLoader;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    private SafeAsyncTask<VideoListResponse> listTask;

    public static IRRelatedFragment newInstance() {
        IRRelatedFragment fragment = new IRRelatedFragment();
        return fragment;
    }

    public IRRelatedFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irrelated, container, false);
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

        mListView.setEmptyView(emptyText);

        mAdapter = new IRRelatedAdapter(getActivity(), R.layout.row_related_list, imageLoader);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        fetch(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
        cancelTask();
    }

    void fetch(int page) {
        cancelTask();

        listTask = apiClient.listRelatedVideos(video.getId(), page, 20);
    }

    void cancelTask() {
        if (listTask != null) {
            listTask.cancel(true);
        }
        listTask = null;
    }

    @Subscribe
    public void onEvent(VideoListEvent event) {
        List<Video> videos = event.videos;
        PageInfo pageInfo = event.pageInfo;

        if (pageInfo.getNumber() == 0) {
            mAdapter.setVideos(videos);

            if (mPullToRefreshListView.isRefreshing()) {
                mPullToRefreshListView.onRefreshComplete();
            }
        } else {
            mAdapter.appendVideos(videos);
        }

        lastPageListed = pageInfo.getNumber();
        numberOfPagesAvailable = pageInfo.getTotalPages();

        listTask = null;
    }
}
