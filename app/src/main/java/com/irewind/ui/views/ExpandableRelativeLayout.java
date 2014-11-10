package com.irewind.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by tzmst on 11/10/2014.
 */
public class ExpandableRelativeLayout extends ScrollView {
    public ExpandableRelativeLayout(Context context) {
        super(context);
    }

    public ExpandableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpandableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

        setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(metrics.heightPixels * metrics.density)));
        super.onDraw(canvas);
    }
}
