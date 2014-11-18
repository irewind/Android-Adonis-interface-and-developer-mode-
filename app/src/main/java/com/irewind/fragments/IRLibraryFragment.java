package com.irewind.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.irewind.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRMovieGridAdapter;
import com.irewind.common.IOnSearchCallback;
import com.irewind.models.MovieGridItem;
import com.jazzyviewpager.JazzyViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IRLibraryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @InjectView(R.id.moviesGridView)
    PullToRefreshGridView mPullToRefreshGridView;
    @InjectView(R.id.emptyTextGrid)
    TextView emptyText;

    private GridView mGridView;
    private IRMovieGridAdapter mAdapter;

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
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                mPullToRefreshGridView.onRefreshComplete(); //TODO Move to onPostExecute;
            }
        });

        mPullToRefreshGridView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                //TODO add elements;
            }
        });

        mGridView = mPullToRefreshGridView.getRefreshableView();
        mGridView.setEmptyView(emptyText);
        mGridView.setOnItemClickListener(this);

        populate();
    }

    @Override
    public void onResume() {
        super.onResume();
        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.movies));
        IRTabActivity.abSearch.setVisibility(View.VISIBLE);
        IRTabActivity.abSearch.setOnClickListener(this);
        IRTabActivity.onSearchCallback = new IOnSearchCallback() {
            @Override
            public void execute() {
                //TODO set search videos
            }
        };
    }

    private void populate(){
        //TODO remove this
        List<MovieGridItem> movieList = new ArrayList<MovieGridItem>();
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));
        movieList.add(new MovieGridItem(0, "", "", "Edit la Freeride Transilvania 12-15 martie 2014 traversare locatie hello lorem ipsum tralalalalalalal sau nu", Calendar.getInstance().getTime()));


        mAdapter = new IRMovieGridAdapter(getActivity(), R.layout.cell_movie_grid, movieList);
        mGridView.setAdapter(mAdapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO on item click
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                IRTabActivity.searchItem.expandActionView();
                break;
        }
    }
}
