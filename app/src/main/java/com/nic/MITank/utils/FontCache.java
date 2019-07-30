package com.nic.MITank.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontCache {

    private static FontCache sInstance;

    private Typeface mRegular;
    private Typeface mDinProMedium;


    public static FontCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FontCache();
            sInstance.mRegular = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Avenir-Black.ttf");
            sInstance.mDinProMedium = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Avenir-Black.ttf");
        }
        return sInstance;
    }

    public static FontCache getInstance() {
        if (sInstance == null) {
            throw new NullPointerException(
                    "sInstance is null call getInstance(Context)");
        }
        return sInstance;
    }

    public Typeface getFont(Font fontStyle) {
        switch (fontStyle) {
            case REGULAR:
                return mRegular;
            case MEDIUM:
                return mDinProMedium;
        }
        return null;
    }

    public enum Font {
        BOLD, REGULAR, GOTHAM, OBLIQUE, HEAVY, DEMI_BOLD, MEDIUM, ITALIC
    }

}
