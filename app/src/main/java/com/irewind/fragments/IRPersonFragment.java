package com.irewind.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.irewind.adapters.IRRelatedAdapter;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.VideoListEvent;
import com.irewind.sdk.api.event.VideoListFailEvent;
import com.irewind.sdk.api.response.VideoListResponse;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.sdk.util.SafeAsyncTask;
import com.irewind.ui.views.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRPersonFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.date)
    TextView date;

    @InjectView(R.id.videoListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;

    private ListView mListView;
    private IRRelatedAdapter mAdapter;

    @Inject
    ApiClient apiClient;

    public User person;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    private SafeAsyncTask<VideoListResponse> listTask;

    public static IRPersonFragment newInstance() {
        IRPersonFragment fragment = new IRPersonFragment();
        return fragment;
    }

    public IRPersonFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_irperson, container, false);
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

        mAdapter = new IRRelatedAdapter(getActivity(), R.layout.row_related_list);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUserInfo(person);

        apiClient.getEventBus().register(this);
        fetch(0);

        IRTabActivity.abBack.setVisibility(View.VISIBLE);
        IRTabActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IRTabActivity.mPeopleFragment = IRPeopleFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.container, IRTabActivity.mPeopleFragment)
                        .disallowAddToBackStack()
                        .commit();
            }
        });
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abAction.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(person.getDisplayName());
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
        cancelTask();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Video video = mAdapter.getItem(position);

        IRVideoDetailsFragment fragment = IRVideoDetailsFragment.newInstance();
        fragment.video = video;
        fragment.person = person;

        IRTabActivity.mPeopleFragment = fragment;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.replace(R.id.container, IRTabActivity.mPeopleFragment)
                .disallowAddToBackStack()
                .commit();
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().length() > 0) {
                Picasso.with(getActivity()).load(user.getPicture()).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            date.setText(DateUtils.getRelativeTimeSpanString(user.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            date.setText("");
        }
    }

    // --- Events --- //

    void fetch(int page) {
        cancelTask();

        listTask = apiClient.listVideos(page, 20);
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

        if (mListView.getAdapter() == null) {
            mListView.setAdapter(mAdapter);
        }
    }

    @Subscribe
    public void onEvent(VideoListFailEvent event) {
        if (mListView.getAdapter() == null) {
            mListView.setAdapter(mAdapter);
        }
    }
}
