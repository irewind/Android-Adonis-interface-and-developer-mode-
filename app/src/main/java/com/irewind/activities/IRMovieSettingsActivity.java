package com.irewind.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.common.IOnSearchCallback;
import com.irewind.fragments.IRVideoDetailsFragment;
import com.irewind.fragments.movies.IRVideoSettingsFragment;
import com.irewind.sdk.model.Video;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class IRMovieSettingsActivity extends ActionBarActivity {

    public static ImageButton abBack, abSearch;
    public static TextView abTitle;
    public static Button abAction;
    private IRVideoSettingsFragment mFragment;
    public static IOnSearchCallback onSearchCallback;

    public static MenuItem searchItem;
    public static SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iraccount_general_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            int statusBarHeight = (int) Math.ceil(80 * getResources().getDisplayMetrics().density);
            findViewById(R.id.activityRoot).setPadding(0, statusBarHeight, 0, 0);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);

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
        abAction = (Button) view.findViewById(R.id.action);
        getSupportActionBar().setCustomView(view);

        if (savedInstanceState == null) {
            mFragment = IRVideoSettingsFragment.newInstance();
            mFragment.video = (Video) getIntent().getSerializableExtra("video");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

// traverse the view to the widget containing the hint text
        try {
            //This might fail for some reason. Cuz android thats why!!
            LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
            LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
            LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
            SearchView.SearchAutoComplete autoComplete = (SearchView.SearchAutoComplete) ll3.getChildAt(0);
// set the hint text color
            autoComplete.setHintTextColor(Color.WHITE);
        } catch (Exception e) {
        }

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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
