package com.irewind.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.common.IOnSearchCallback;
import com.irewind.fragments.IRAccountFragment;
import com.irewind.fragments.IRLibraryFragment;
import com.irewind.fragments.IRMoreFragment;
import com.irewind.fragments.IRPeopleFragment;
import com.irewind.fragments.IRRewindFunctionalityFragment;
import com.irewind.utils.Log;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRTabActivity extends IRBaseActivity implements View.OnClickListener{

    @InjectView(R.id.circle)
    CircleButton mCircleButton;
    @InjectView(R.id.library)
    ImageButton mLibrary;
    @InjectView(R.id.people)
    ImageButton mPeople;
    @InjectView(R.id.account)
    ImageButton mAccount;
    @InjectView(R.id.more)
    ImageButton mMore;

    public static ImageButton abBack, abSearch;
    public static TextView abTitle;

    public static Fragment mLibraryFragment, mPeopleFragment, mAccountFragment, mMoreFragment;
    private Menu mMenu;
    public static MenuItem searchItem;
    public static SearchView searchView;
    public static IOnSearchCallback onSearchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irtab);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            int statusBarHeight = (int) Math.ceil(80 * getResources().getDisplayMetrics().density);
            findViewById(R.id.activityRoot).setPadding(0, statusBarHeight, 0, 0);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

        ButterKnife.inject(this);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_bar));
        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setLogo(0);

        View view = getLayoutInflater().inflate(R.layout.actionbar, null);
        abTitle = (TextView) view.findViewById(R.id.title);
        abSearch = (ImageButton) view.findViewById(R.id.btn_search);
        abBack = (ImageButton) view.findViewById(R.id.btn_back);
        getSupportActionBar().setCustomView(view);

        if (savedInstanceState == null) {
            mLibraryFragment = IRLibraryFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mLibraryFragment)
                    .disallowAddToBackStack()
                    .commit();
            mLibrary.setSelected(true);
        }

        mLibrary.setOnClickListener(this);
        mPeople.setOnClickListener(this);
        mAccount.setOnClickListener(this);
        mMore.setOnClickListener(this);
        mCircleButton.setOnClickListener(this);

        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        if (heightDiff > 160) { // if more than 100 pixels, its probably a keyboard...
                            findViewById(R.id.tabLayout).setVisibility(View.GONE);
                            FrameLayout v = (FrameLayout) findViewById(R.id.container);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                            params.setMargins(0,0,0,0);
                            v.setLayoutParams(params);
                        } else {
                            findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                            Resources r = IRTabActivity.this.getResources();
                            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -20, r.getDisplayMetrics());
                            FrameLayout v = (FrameLayout) findViewById(R.id.container);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
                            params.setMargins(0,0,0, px);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.library:
                if(switchSelection(mLibrary)){
                    if (mLibraryFragment == null){
                        mLibraryFragment = IRLibraryFragment.newInstance();
                    }
                    switchFragment(mLibraryFragment);
                }
                break;
            case R.id.people:
                if(switchSelection(mPeople)){
                    if (mPeopleFragment == null){
                        mPeopleFragment = IRPeopleFragment.newInstance();
                    }
                    switchFragment(mPeopleFragment);
                }
                break;
            case R.id.account:
                if(switchSelection(mAccount)){
                    if (mAccountFragment == null){
                        mAccountFragment = IRAccountFragment.newInstance();
                    }
                    switchFragment(mAccountFragment);
                }
                break;
            case R.id.more:
                if(switchSelection(mMore)){
                    if (mMoreFragment == null){
                        mMoreFragment = IRMoreFragment.newInstance();
                    }
                    switchFragment(mMoreFragment);
                }
                break;
            case R.id.circle:
                if(switchSelectionMiddle(mCircleButton)){
                    switchFragment(IRRewindFunctionalityFragment.newInstance());
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        mMenu = menu;

        searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());

        searchView.setSearchableInfo(info);
        searchView.setIconifiedByDefault(true);
        // Set on click to open a fragment, not a activity
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                onSearchCallback.execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SEARCH", query);
                searchView.clearFocus();
                MenuItemCompat.collapseActionView(searchItem);
                onSearchCallback.execute(query);
                return false;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem arg0) {
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem arg0) {
                return true;
            }


        });

        return super.onCreateOptionsMenu(menu);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private boolean switchSelection(ImageButton btn){
        if (!btn.isSelected()){
            btn.setSelected(true);
            if (btn != mAccount){
                mAccount.setSelected(false);
            }
            if (btn != mLibrary){
                mLibrary.setSelected(false);
            }
            if (btn != mMore){
                mMore.setSelected(false);
            }
            if (btn != mPeople){
                mPeople.setSelected(false);
            }
            mCircleButton.setSelected(false);
            return true;
        }
        return false;
    }

    private boolean switchSelectionMiddle(CircleButton btn){
        if (!btn.isSelected()){
            btn.setSelected(true);
            mAccount.setSelected(false);
            mLibrary.setSelected(false);
            mMore.setSelected(false);
            mPeople.setSelected(false);
            return true;
        }
        return false;
    }

    private void switchFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment)
          .disallowAddToBackStack()
          .commit();
    }
}
