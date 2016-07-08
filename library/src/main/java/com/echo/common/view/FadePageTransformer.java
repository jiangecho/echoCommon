package com.echo.common.view;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by jiangecho on 16/7/8.
 */
public class FadePageTransformer implements ViewPager.PageTransformer {
    private float initialAlpha;

    public FadePageTransformer(float initialAlpha) {
        this.initialAlpha = initialAlpha;
    }

    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {
            page.setAlpha(initialAlpha);
        } else if (position <= 0) {
            page.setAlpha(1.0f + (1.0f - initialAlpha) * position);
        } else if (position <= 1) {
            page.setAlpha(1.0f - (1.0f - initialAlpha) * position);
        } else {
            page.setAlpha(initialAlpha);
        }
    }
}
