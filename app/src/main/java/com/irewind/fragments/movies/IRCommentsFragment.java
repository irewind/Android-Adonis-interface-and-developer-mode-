package com.irewind.fragments.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.irewind.activities.IRAddCommentActivity;
import com.irewind.adapters.IRCommentsAdapter;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.CommentAddEvent;
import com.irewind.sdk.api.event.CommentAddFailEvent;
import com.irewind.sdk.api.event.CommentListEvent;
import com.irewind.sdk.model.Comment;
import com.irewind.sdk.model.PageInfo;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class IRCommentsFragment extends Fragment implements IRCommentsAdapter.ActionListener {

    @InjectView(R.id.commentsListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;
    @InjectView(R.id.progress)
    CircularProgressBar progressBar;

    private ListView mListView;
    private IRCommentsAdapter mAdapter;
    public Video video;

    @Inject
    ApiClient apiClient;

    private int lastPageListed = 0;
    private int numberOfPagesAvailable = 0;

    public static IRCommentsFragment newInstance() {
        IRCommentsFragment fragment = new IRCommentsFragment();
        return fragment;
    }

    public IRCommentsFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_ircomments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mListView = mPullToRefreshListView.getRefreshableView();

        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (lastPageListed + 1 < numberOfPagesAvailable) {
                    fetch(lastPageListed + 1);
                }
            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                fetch(0);
            }
        });

        mAdapter = new IRCommentsAdapter(getActivity(), R.layout.row_comments_list);

        apiClient.loadActiveUserInfo();
        User user = apiClient.getActiveUser();
        if (user != null) {
            mAdapter.setProfileImage(user.getPicture());
        }

        mAdapter.setActionListener(this);

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
        apiClient.cancelListVideoCommentsTask();
    }

    void fetch(int page) {
        apiClient.listVideoComments(video.getId(), page, 20);
    }

    @Override
    public void addComment() {
        Intent intent = new Intent(getActivity(), IRAddCommentActivity.class);
        intent.putExtra(IRAddCommentActivity.EXTRA_VIDEO_ID_KEY, video.getId());

        User user = apiClient.getActiveUser();
        if (user != null && user.getPicture() != null) {
            intent.putExtra(IRAddCommentActivity.EXTRA_PROFILE_IMAGE_KEY, user.getPicture());
        }

        getActivity().startActivity(intent);
    }

    @Override
    public void replyComment(Comment comment) {
        Intent intent = new Intent(getActivity(), IRAddCommentActivity.class);
        intent.putExtra(IRAddCommentActivity.EXTRA_VIDEO_ID_KEY, video.getId());
        intent.putExtra(IRAddCommentActivity.EXTRA_PARENT_COMMENT_ID_KEY, comment.getId());

        User user = apiClient.getActiveUser();
        if (user != null && user.getPicture() != null) {
            intent.putExtra(IRAddCommentActivity.EXTRA_PROFILE_IMAGE_KEY, user.getPicture());
        }

        getActivity().startActivity(intent);
    }

    @Subscribe
    public void onEvent(CommentListEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        mListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);

        List<Comment> comments = event.comments;
        PageInfo pageInfo = event.pageInfo;

        if (pageInfo.getNumber() == 0) {
            mAdapter.setComments(comments);

            if (mPullToRefreshListView.isRefreshing()) {
                mPullToRefreshListView.onRefreshComplete();
            }
        } else {
            mAdapter.appendComments(comments);
        }

        lastPageListed = pageInfo.getNumber();
        numberOfPagesAvailable = pageInfo.getTotalPages();
    }

    @Subscribe
    public void onEvent(CommentAddEvent event) {
        fetch(0);
    }

    @Subscribe
    public void onEvent(CommentAddFailEvent event) {
        progressBar.setVisibility(View.INVISIBLE);
        mListView.setEmptyView(emptyText);

        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
    }
}
