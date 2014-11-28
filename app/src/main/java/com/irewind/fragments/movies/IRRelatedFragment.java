package com.irewind.fragments.movies;

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
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRRelatedAdapter;
import com.irewind.fragments.IRVideoDetailsFragment;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoListEvent;
import com.irewind.sdk.api.event.VideoListFailEvent;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRRelatedFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.relatedListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;

    private ListView mListView;
    private IRRelatedAdapter mAdapter;

    public Video video;
    public User person;

    @Inject
    ApiClient apiClient;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

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

        mAdapter = new IRRelatedAdapter(getActivity(), R.layout.row_related_list);
        mListView.setAdapter(mAdapter);
        mPullToRefreshListView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
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

        mListView.setEmptyView(null);

        apiClient.getEventBus().unregister(this);

        apiClient.cancelListRelatedVideosTask();
    }

    void fetch(int page) {
        apiClient.listRelatedVideos(video.getId(), page, 200);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Video video = mAdapter.getItem(position - 1);

//        try {
//            IRVideoDetailsFragment.stop();
//            IRVideoDetailsFragment.sVideoView.stopPlayback();
//            IRVideoDetailsFragment.sVideoView.clearAnimation();
//            IRVideoDetailsFragment.sVideoView = null;
//        } catch (Exception e) {
//
//        }

        IRVideoDetailsFragment fragment = IRVideoDetailsFragment.newInstance();
        fragment.video = video;

        if (person != null) {
            fragment.person = person;

            IRTabActivity.mPeopleFragment = fragment;
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            ft.replace(R.id.container, IRTabActivity.mPeopleFragment)
                    .disallowAddToBackStack()
                    .commit();
        } else {
            IRTabActivity.mLibraryFragment = fragment;
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            ft.replace(R.id.container, IRTabActivity.mLibraryFragment)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(VideoListEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        mListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

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
    }

    @Subscribe
    public void onEvent(VideoListFailEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        mListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

        if (mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
        }
    }
}
