package com.echo.common.view;

/**
 * Created by jiangecho on 16/1/8.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by jiangecho on 15/10/21.
 */
/*
* A linearLayout supports block all touch event
 */
public class LinearLayoutEx extends LinearLayout {

    private boolean blockAllTouchEvent = true;

    public LinearLayoutEx(Context context) {
        super(context);
    }

    public LinearLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LinearLayoutEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isBlockAllTouchEvent() {
        return blockAllTouchEvent;
    }

    public void setBlockAllTouchEvent(boolean blockAllTouchEvent) {
        this.blockAllTouchEvent = blockAllTouchEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (blockAllTouchEvent) {
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}

