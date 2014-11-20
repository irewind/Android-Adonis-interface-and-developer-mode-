package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.R;

import com.irewind.adapters.IRCommentsAdapter;
import com.irewind.models.CommentItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.irewind.sdk.model.Video;


public class IRCommentsFragment extends Fragment {

    @InjectView(R.id.commentsListView)
    PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    private IRCommentsAdapter mAdapter;
    public Video video;

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

        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {

            }
        });
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                mPullToRefreshListView.onRefreshComplete(); //TODO Move this line in onPostExecute if AsyncTask
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        populate();
    }

    private void populate(){
        //TODO REMOVE THIS
        List<CommentItem> data = new ArrayList<CommentItem>();
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));
        data.add(new CommentItem(0, "","", Calendar.getInstance().getTime(), "link", "comments"));

        mAdapter = new IRCommentsAdapter(getActivity(), R.layout.row_comments_list, data);
        mListView.setAdapter(mAdapter);
    }
}
