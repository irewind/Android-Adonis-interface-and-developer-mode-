package com.irewind.fragments.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.irewind.R;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRSearchPeopleAdapter;
import com.irewind.adapters.IRSettingsExpandableAdapter;
import com.irewind.common.IOnSearchCallback;
import com.irewind.fragments.IRVideoDetailsFragment;
import com.irewind.models.PeopleVideoItem;
import com.irewind.sdk.model.User;
import com.irewind.sdk.model.Video;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRSettingsFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @InjectView(R.id.expandableListView)
    ExpandableListView expandableListView;
    @InjectView(R.id.searchPeopleList)
    PullToRefreshListView mPullToRefreshListView;
    @InjectView(R.id.emptySettingsText)
    TextView emptyText;

    private ListView mListView;

    private IRSettingsExpandableAdapter mAdapterExpandable;
    private IRSearchPeopleAdapter mAdapterSearch;

    Video video;

    public static IRSettingsFragment newInstance() {
        IRSettingsFragment fragment = new IRSettingsFragment();
        return fragment;
    }

    public IRSettingsFragment() {
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
        return inflater.inflate(R.layout.fragment_irsettings, container, false);
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
                IRTabActivity.abTitle.setText(getString(R.string.movie_settings));
                IRTabActivity.abAction.setText(getString(R.string.save));
                IRTabActivity.abSearch.setVisibility(View.GONE);
                IRTabActivity.abAction.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelExpanded(View view) {
                slidingUpPanelLayout.setSlidingEnabled(false);
                IRTabActivity.abTitle.setText(getString(R.string.add_another));
                IRTabActivity.abAction.setVisibility(View.GONE);
                IRTabActivity.abSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

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
                mPullToRefreshListView.onRefreshComplete();//TODO Move to onPostExecute;
            }
        });

        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setEmptyView(emptyText);

        populateExpandable();
        populateSearchList();
    }

    private void populateSearchList(){
        List<User> data = new ArrayList<User>();
        data.add(null);
        data.add(null);
        data.add(null);
        data.add(null);
        data.add(null);
        data.add(null);
        data.add(null);

        mAdapterSearch = new IRSearchPeopleAdapter(getActivity(), R.layout.row_search_people_list, null);
        mAdapterSearch.setUsers(data);
        mListView.setAdapter(mAdapterSearch);

    }

    private void populateExpandable(){
        List<PeopleVideoItem> childData = new ArrayList<PeopleVideoItem>();
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());
        childData.add(new PeopleVideoItem());

        List<Boolean> checkedStated = new ArrayList<Boolean>();
        checkedStated.add(true);
        checkedStated.add(false);
        checkedStated.add(false);

        mAdapterExpandable = new IRSettingsExpandableAdapter(getActivity(), childData, slidingUpPanelLayout, expandableListView, checkedStated);
        expandableListView.setAdapter(mAdapterExpandable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (slidingUpPanelLayout.isPanelExpanded()){
            slidingUpPanelLayout.setSlidingEnabled(false);
            IRTabActivity.abTitle.setText(getString(R.string.add_another));
            IRTabActivity.abAction.setVisibility(View.GONE);
            IRTabActivity.abSearch.setVisibility(View.VISIBLE);
        } else {
            IRTabActivity.abTitle.setText(getString(R.string.movie_settings));
            IRTabActivity.abSearch.setVisibility(View.GONE);
            IRTabActivity.abAction.setText(getString(R.string.save));
            IRTabActivity.abAction.setVisibility(View.VISIBLE);
        }
        IRTabActivity.abBack.setVisibility(View.VISIBLE);
        IRTabActivity.abSearch.setOnClickListener(this);
        IRTabActivity.onSearchCallback = new IOnSearchCallback() {
            @Override
            public void execute(String query) {

            }
        };
        IRTabActivity.abAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout.isPanelExpanded()){
                    slidingUpPanelLayout.setSlidingEnabled(true);
                    slidingUpPanelLayout.collapsePanel();
                }
            }
        });
        IRTabActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout.isPanelExpanded()){
                    slidingUpPanelLayout.setSlidingEnabled(true);
                    slidingUpPanelLayout.collapsePanel();
                } else {
                    IRVideoDetailsFragment fragment = IRVideoDetailsFragment.newInstance();
                    fragment.video = video;
                    IRTabActivity.mLibraryFragment = fragment;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                    ft.replace(R.id.container, IRTabActivity.mLibraryFragment)
                            .disallowAddToBackStack()
                            .commit();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                IRTabActivity.searchItem.expandActionView();
                break;
        }
    }
}
