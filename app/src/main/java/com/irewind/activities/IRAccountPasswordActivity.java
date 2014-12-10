package com.irewind.activities;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.fragments.IRAccountPasswordFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class IRAccountPasswordActivity extends IRBaseActivity{

    private ImageButton abBack, abSearch;
    private TextView abTitle;
    private Button abAction;
    private IRAccountPasswordFragment mFragment;

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
            mFragment = IRAccountPasswordFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        abBack.setVisibility(View.VISIBLE);
        abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        abTitle.setText(getString(R.string.change_password));
        abSearch.setVisibility(View.GONE);
        abAction.setVisibility(View.GONE);
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
