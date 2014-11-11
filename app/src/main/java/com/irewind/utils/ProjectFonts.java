package com.irewind.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by tzmst on 11/11/2014.
 */
public class ProjectFonts {

    private Typeface light, normal, semibold, bold;
    private Context mContext;
    private static ProjectFonts instance;

    public static ProjectFonts newInstance(Context context){
        if (instance == null){
            instance = new ProjectFonts(context);
        }

        return instance;
    }

    public ProjectFonts(Context context) {
        mContext = context;
    }

    public Typeface getLight() {
        if (light == null){
            light = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Light.ttf");
        }
        return light;
    }

    public Typeface getNormal() {
        if (normal == null){
            normal = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Regular.ttf");
        }
        return normal;
    }

    public Typeface getSemibold() {
        if (semibold == null){
            semibold = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Semibold.ttf");
        }
        return semibold;
    }

    public Typeface getBold() {
        if (bold == null){
            bold = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");
        }
        return bold;
    }
}
