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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.R;
import com.irewind.adapters.IRRelatedAdapter;
import com.irewind.models.RelatedItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRRelatedFragment extends Fragment {

    @InjectView(R.id.relatedListView)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptyText)
    TextView emptyText;

    private ListView mListView;
    private IRRelatedAdapter mAdapter;

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

        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                //TODO on last item visible
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

        mListView.setEmptyView(emptyText);

        populate();
    }

    private void populate() { //TODO Remove this
        List<RelatedItem> data = new ArrayList<RelatedItem>();
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));
        data.add(new RelatedItem(0, "", "", Calendar.getInstance().getTime(), 10, 10, ""));

        mAdapter = new IRRelatedAdapter(getActivity(), R.layout.row_related_list, data);
        mListView.setAdapter(mAdapter);
    }
}
