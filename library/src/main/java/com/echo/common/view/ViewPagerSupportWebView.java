package com.echo.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by jiangecho on 16/2/27.
 */
public class ViewPagerSupportWebView extends ViewPager {
    private int webViewId = -1;

    public ViewPagerSupportWebView(Context context) {
        super(context);
    }

    public ViewPagerSupportWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setWebViewId(int id) {
        this.webViewId = id;
    }

    @Override

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        View view = v.findViewById(webViewId); //res ID
        if (view != null && view instanceof WebView) {
            return ((WebView)view).canScrollHorizontally(-dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }
}
