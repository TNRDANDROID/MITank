package com.nic.MITank.support;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */
public class MyEditTextView extends EditText {

    public MyEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Avenir-Roman.ttf");
            setTypeface(tf);
        }
    }

}
