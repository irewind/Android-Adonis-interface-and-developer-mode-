package com.irewind.activities;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.irewind.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRWebViewActivity extends IRBaseActivity {

    @InjectView(R.id.webView)
    WebView webView;

    private ImageButton abBack, abSearch;
    private TextView abTitle;
    private Button abAction;
    private String url, title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_irweb_view);
        ButterKnife.inject(this);

        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");

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

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        abBack.setVisibility(View.VISIBLE);
        abSearch.setVisibility(View.GONE);
        abAction.setVisibility(View.GONE);
        abTitle.setText(title);
        abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
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
