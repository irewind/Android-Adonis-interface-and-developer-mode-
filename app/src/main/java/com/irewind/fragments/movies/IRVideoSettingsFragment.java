package com.irewind.fragments.movies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRAddCommentActivity;
import com.irewind.activities.IREditVideoTitleActivity;
import com.irewind.activities.IRMovieSettingsActivity;
import com.irewind.adapters.IRPeopleAdapter;
import com.irewind.adapters.IRVideoSettingsAdapter;
import com.irewind.common.IOnSearchCallback;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.UserListEvent;
import com.irewind.sdk.api.event.UserListFailEvent;
import com.irewind.sdk.api.event.VideoInfoEvent;
import com.irewind.sdk.api.event.VideoPermissionListEvent;
import com.irewind.sdk.api.event.VideoPermissionListFailedEvent;
import com.irewind.sdk.api.event.VideoPermissionUpdateEvent;
import com.irewind.sdk.api.event.VideoPermissionUpdateFailedEvent;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.irewind.sdk.model.VideoPermission;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IRVideoSettingsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, IRVideoSettingsAdapter.OptionDelegate {

    @InjectView(R.id.videoTitle)
    TextView videoTitle;

    @InjectView(R.id.videoCreatedDate)
    TextView videoCreatedDate;

//    @InjectView(R.id.videoDuration)
//    TextView videoDuration;

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    @InjectView(R.id.expandableListView)
    ExpandableListView mOptionListView;
    private IRVideoSettingsAdapter mOptionAdapter;

    @InjectView(R.id.searchPeopleList)
    PullToRefreshListView mPullToRefreshListView;
    private ListView mPeopleListView;
    @InjectView(R.id.emptySettingsText)
    TextView emptyText;
    private IRPeopleAdapter mPeopleAdapter;

    @InjectView(R.id.peopleProgress)
    CircularProgressBar peopleProgressBar;

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.videoPlaceholder)
    ImageView placeHolder;

    public Video video;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    private String searchQuery = "";

    public static IRVideoSettingsFragment newInstance() {
        IRVideoSettingsFragment fragment = new IRVideoSettingsFragment();
        return fragment;
    }

    public IRVideoSettingsFragment() {
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
        return inflater.inflate(R.layout.fragment_video_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        slidingUpPanelLayout.setPanelHeight(0);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                slidingUpPanelLayout.setSlidingEnabled(true);
                IRMovieSettingsActivity.abTitle.setText(getString(R.string.movie_settings));
                IRMovieSettingsActivity.abAction.setText("");
                IRMovieSettingsActivity.abSearch.setVisibility(View.GONE);
                IRMovieSettingsActivity.abAction.setVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View view) {
                slidingUpPanelLayout.setSlidingEnabled(false);
                IRMovieSettingsActivity.abTitle.setText(getString(R.string.add_another));
                IRMovieSettingsActivity.abAction.setVisibility(View.GONE);
                IRMovieSettingsActivity.abSearch.setVisibility(View.VISIBLE);

                fetchPeople(0);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });


        mPeopleListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (lastPageListed + 1 < numberOfPagesAvailable) {
                    fetchPeople(lastPageListed + 1);
                }
            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchPeople(0);
            }
        });
        mPeopleListView.setOnItemClickListener(this);

        mPeopleAdapter = new IRPeopleAdapter(getActivity(), R.layout.row_people_list);
        mPeopleListView.setAdapter(mPeopleAdapter);
        mPullToRefreshListView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
        mPeopleListView.setEmptyView(emptyText);

        if (video.getThumbnail() != null && video.getThumbnail().trim().length() > 0) {
            Picasso.with(getActivity()).load(video.getThumbnail()).into(placeHolder);
        }

        videoTitle.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (slidingUpPanelLayout.isPanelExpanded()) {
            slidingUpPanelLayout.setSlidingEnabled(false);
            IRMovieSettingsActivity.abTitle.setText(getString(R.string.add_another));
            IRMovieSettingsActivity.abAction.setVisibility(View.GONE);
            IRMovieSettingsActivity.abSearch.setVisibility(View.VISIBLE);
        } else {
            IRMovieSettingsActivity.abTitle.setText(getString(R.string.movie_settings));
            IRMovieSettingsActivity.abSearch.setVisibility(View.GONE);
            IRMovieSettingsActivity.abAction.setText(getString(R.string.save));
            IRMovieSettingsActivity.abAction.setVisibility(View.GONE);
        }
        IRMovieSettingsActivity.abBack.setVisibility(View.VISIBLE);
        IRMovieSettingsActivity.abSearch.setOnClickListener(this);
        IRMovieSettingsActivity.onSearchCallback = new IOnSearchCallback() {
            @Override
            public void execute(String query) {
                searchQuery = query;
                fetchPeople(0);
            }
        };
        IRMovieSettingsActivity.abAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout.isPanelExpanded()) {
                    slidingUpPanelLayout.setSlidingEnabled(true);
                    slidingUpPanelLayout.collapsePanel();
                }
            }
        });
        IRMovieSettingsActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout.isPanelExpanded()) {
                    slidingUpPanelLayout.setSlidingEnabled(true);
                    slidingUpPanelLayout.collapsePanel();
                } else {
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                }
            }
        });

        updateVideoInfo(video);

        apiClient.getEventBus().register(this);

        apiClient.videoById(video.getId());
        apiClient.listVideoViewPermissions(video.getId());
    }

    @Override
    public void onPause() {
        super.onPause();

        mPeopleListView.setEmptyView(null);

        apiClient.getEventBus().unregister(this);

        apiClient.cancelListUsersTask();
        apiClient.cancelSearchUsersTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                IRMovieSettingsActivity.searchItem.expandActionView();
                break;
            case R.id.videoTitle:
                updateVideoTitle();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        slidingUpPanelLayout.setSlidingEnabled(true);
        slidingUpPanelLayout.collapsePanel();

        User user = mPeopleAdapter.getItem(position - 1);
        addPerson(user);
    }


    private void updateVideoTitle() {
        Intent intent = new Intent(getActivity(), IREditVideoTitleActivity.class);
        intent.putExtra(IREditVideoTitleActivity.EXTRA_VIDEO_ID_KEY, video.getId());
        intent.putExtra(IREditVideoTitleActivity.EXTRA_VIDEO_TITLE_KEY, video.getTitle());

        User user = apiClient.getActiveUser();
        if (user != null && user.getPicture() != null) {
            intent.putExtra(IREditVideoTitleActivity.EXTRA_PROFILE_IMAGE_KEY, user.getPicture());
        }

        getActivity().startActivity(intent);
    }

    private void updatePermissionInfo(VideoPermission permission, List<User> people) {
        String accessType = VideoPermission.ACCESS_TYPE_PRIVATE;

        if (permission != null) {
            accessType = permission.getAccessType();
        }

        List<Boolean> accessTypeStates = new ArrayList<Boolean>();
        accessTypeStates.add(accessType.equals(VideoPermission.ACCESS_TYPE_PUBLIC));
        accessTypeStates.add(accessType.equals(VideoPermission.ACCESS_TYPE_PRIVATE));
        accessTypeStates.add(accessType.equals(VideoPermission.ACCESS_TYPE_CUSTOM));

        if (people == null) {
            people = new ArrayList<User>();
        }
        mOptionAdapter = new IRVideoSettingsAdapter(getActivity(), slidingUpPanelLayout, mOptionListView, accessTypeStates, people);
        mOptionAdapter.setOptionDelegate(this);
        mOptionListView.setAdapter(mOptionAdapter);
    }

    private void updateVideoInfo(Video video) {
        videoTitle.setText(video.getTitle());
        if (video.getCreatedDate() > 0) {
            videoCreatedDate.setText(DateUtils.getRelativeTimeSpanString(video.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
        } else {
            videoCreatedDate.setText("");
        }
    }

    void fetchPeople(int page) {
        if (mPeopleAdapter.getCount() == 0) {
            mPullToRefreshListView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }

        if (searchQuery != null && searchQuery.length() > 0) {
            apiClient.searchUsers(searchQuery, page, 200);
        } else {
            apiClient.listUsers(page, 200);
        }
    }

    public void setOption(int optionIndex) {
        if (optionIndex == 0) {
            apiClient.makeVideoPublic(video.getId());
        } else if (optionIndex == 1) {
            apiClient.makeVideoPrivate(video.getId());
        }
    }

    public void addPerson(User user) {
        apiClient.grantUserVideoAccess(video.getId(), user.getEmail());
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(VideoInfoEvent event) {
        this.video = event.video;
        updateVideoInfo(video);
    }

    @Subscribe
    public void onEvent(VideoPermissionListEvent event) {
        updatePermissionInfo(event.videoPermission, event.users);
    }

    @Subscribe
    public void onEvent(VideoPermissionListFailedEvent event) {
    }

    @Subscribe
    public void onEvent(VideoPermissionUpdateEvent event) {
        apiClient.listVideoViewPermissions(video.getId());
    }

    @Subscribe
    public void onEvent(VideoPermissionUpdateFailedEvent event) {
        apiClient.listVideoViewPermissions(video.getId());
    }

    @Subscribe
    public void onEvent(UserListEvent event) {
        peopleProgressBar.setVisibility(View.INVISIBLE);
        mPeopleListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

        List<User> users = event.users;
        PageInfo pageInfo = event.pageInfo;

        if (pageInfo.getNumber() == 0) {
            mPeopleAdapter.setUsers(users);

            if (mPullToRefreshListView.isRefreshing()) {
                mPullToRefreshListView.onRefreshComplete();
            }
        } else {
            mPeopleAdapter.appendUsers(users);
        }

        lastPageListed = pageInfo.getNumber();
        numberOfPagesAvailable = pageInfo.getTotalPages();
    }

    @Subscribe
    public void onEvent(UserListFailEvent event) {
        peopleProgressBar.setVisibility(View.INVISIBLE);
        mPeopleListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

        if (mPullToRefreshListView.isRefreshing()) {
            mPullToRefreshListView.onRefreshComplete();
        }
    }
}
