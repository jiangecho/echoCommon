/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.echo.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echo.common.R;

import java.util.Locale;


public class WechatTab extends HorizontalScrollView {

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;
    private int width;
    private boolean isTabsAdded = false;

    private int currentPosition = 0;
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int underlineColor = Color.parseColor("#1A000000");
    private int dividerColor = Color.parseColor("#1A000000");

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 2;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int tabPadding = 2; // tab 为 textview 时的padding
    private int dividerWidth = 1;
    private boolean showUnderLine = true;

    private int mMyUnderlinePadding = 12;

    private int tabTextSize = 12;
    private int tabTextColor = Color.parseColor("#FF666666");
    private int selectedTabTextColor = Color.BLACK;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;
    private int tabWidth;

    private int tabBackgroundResId = R.drawable.background_tab;

    private Locale locale;
    private Drawable indicatorDrawable;

    public WechatTab(Context context) {
        this(context, null);
    }

    public WechatTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WechatTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        //tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WechatTab);

        underlineColor = a.getColor(R.styleable.WechatTab_underlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.WechatTab_dividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.WechatTab_indicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.WechatTab_underlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.WechatTab_tabDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.WechatTab_tabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.WechatTab_weChatTabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.WechatTab_shouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.WechatTab_scrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.WechatTab_tabTextAllCaps, textAllCaps);
        mMyUnderlinePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMyUnderlinePadding, dm);
        indicatorDrawable = a.getDrawable(R.styleable.WechatTab_indicatorDrawable);
        showUnderLine = a.getBoolean(R.styleable.WechatTab_showUnderLine, true);

        tabTextSize = a.getDimensionPixelSize(R.styleable.WechatTab_tabTextSize, tabTextSize);
        tabTextColor = a.getColor(R.styleable.WechatTab_weChatTabTextColor, tabTextColor);
        selectedTabTextColor = a.getColor(R.styleable.WechatTab_focusedTabTextColor, selectedTabTextColor);

        a.recycle();


        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        //defaultTabLayoutParams = new LinearLayout.LayoutParams(200, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }

    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        setShouldExpand(true);
        // 设置Tab的分割线是透明的
        setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3, dm));
        // 设置Tab标题文字的大小
//        setTextSize((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        setTextSize(tabTextSize);
        setTextColor(tabTextColor);
        setTabBackground(0);
    }

    public void setViewPager(ViewPager pager) {
        // TODO tmp strategy
        defaultTabLayoutParams = null;
        if (pager == null || pager.getAdapter() == null) {
            return;
        }
        this.pager = pager;

        selectedPosition = pager.getCurrentItem();
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.addOnPageChangeListener(pageListener);

        notifyDataSetChanged();

        setTabsValue();
    }

    public ViewPager getViewPager() {
        return this.pager;
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        if (width == 0) {
            return;
        } else {
            addTabs();
        }
    }

    private void addTabs() {
        isTabsAdded = true;
        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                PagerAdapter adapter = pager.getAdapter();
                if (adapter instanceof WeChatTabTitleInterface) {
                    WeChatTabTitleInterface weChatTabTitleInterface = (WeChatTabTitleInterface) adapter;
                    addTextTab(i, weChatTabTitleInterface.getTabTitle(i),
                            weChatTabTitleInterface.getTabDrawableLeft(i),
                            weChatTabTitleInterface.getTabDrawableTop(i),
                            weChatTabTitleInterface.getTabDrawableRight(i),
                            weChatTabTitleInterface.getTabDrawableBottom(i));
                } else {
                    addTextTab(i, adapter.getPageTitle(i).toString());
                }
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private void addTextTab(final int position, SpannableString title, int start, int top, int end, int bottom) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        //tab.setSingleLine();
        tab.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        tab.setLineSpacing(8, 1.0f);
        addTab(position, tab);
    }

    private void addTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        //tab.setSingleLine();
        tab.setLineSpacing(5, 1.0f);
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }


    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
                currentPosition = position;
            }
        });

        //tab.setPadding(2, 0, 2, 0);
        if (defaultTabLayoutParams == null) {
            if (width == 0) {
                width = getWidth();
            }
            tabWidth = width / (tabCount > 5 ? 5 : tabCount);
            defaultTabLayoutParams = new LinearLayout.LayoutParams(tabWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        tab.setLayoutParams(defaultTabLayoutParams);
        //tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
        tabsContainer.addView(tab, position);
    }

    private void updateTabStyles() {

        PagerAdapter adapter = pager.getAdapter();
        if (adapter instanceof WeChatTabTitleInterface) {
            for (int i = 0; i < tabCount; i++) {

                View v = tabsContainer.getChildAt(i);

                v.setBackgroundResource(tabBackgroundResId);

                if (v instanceof TextView) {

                    TextView tab = (TextView) v;
                    //SpannableString spannableString = tabSpannableTitles.get(i);
                    SpannableString spannableString = ((WeChatTabTitleInterface) adapter).getTabTitle(i);
                    if (i == selectedPosition) {
//                        spannableString.setSpan(new ForegroundColorSpan(selectedTabTextColor), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                        tab.setText(spannableString);
                    } else {
                        tab.setText(spannableString);
                    }

                }
            }

        } else {
            for (int i = 0; i < tabCount; i++) {

                View v = tabsContainer.getChildAt(i);

                v.setBackgroundResource(tabBackgroundResId);

                if (v instanceof TextView) {

                    TextView tab = (TextView) v;
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                    tab.setTypeface(tabTypeface, tabTypefaceStyle);
                    tab.setTextColor(tabTextColor);

                    // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                    // pre-ICS-build
                    if (textAllCaps) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            tab.setAllCaps(true);
                        } else {
                            tab.setText(tab.getText().toString().toUpperCase(locale));
                        }
                    }
                    if (i == selectedPosition) {
                        tab.setTextColor(selectedTabTextColor);
                        //tab.setTextColor(Color.BLACK);
                    } else {
                        tab.setTextColor(tabTextColor);
                    }
                }
            }

        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int heght = getMeasuredHeight();

        if (width > 0) {
            this.width = width;
            if (!isTabsAdded && pager != null) {
                addTabs();
            }
        }
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        scrollOffset = getWidth() / 2 - tabWidth / 2;

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // OPPO device may throw nullPointerException here!!!
        try {
            super.onDraw(canvas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw underline
        rectPaint.setColor(underlineColor);
        //canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if (indicatorDrawable != null) {
            int indicatorDrawableWidth = indicatorDrawable.getIntrinsicWidth();
            indicatorDrawable.setBounds((int) (lineLeft + tabWidth / 2 - indicatorDrawableWidth / 2), height - indicatorDrawable.getIntrinsicHeight(), (int) (lineRight - tabWidth / 2 + indicatorDrawableWidth / 2), height);
            indicatorDrawable.draw(canvas);
        } else if (showUnderLine) {
            canvas.drawRect(lineLeft + mMyUnderlinePadding, height - indicatorHeight, lineRight - mMyUnderlinePadding, height, rectPaint);
        }

        // draw divider

        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;

            View view = tabsContainer.getChildAt(position);
            if (view == null) {
                return;
            }
            //scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
            scrollToChild(position, (int) (positionOffset * view.getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            updateTabStyles();
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        notifyDataSetChanged();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public void setUnderlinePadding0() {
        mMyUnderlinePadding = 0;
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setSelectedTextColor(int textColor) {
        this.selectedTabTextColor = textColor;
        updateTabStyles();
    }

    public void setSelectedTextColorResource(int resId) {
        this.selectedTabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getSelectedTextColor() {
        return selectedTabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        updateTabStyles();
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface WeChatTabTitleInterface {
        SpannableString getTabTitle(int position);

        int getTabDrawableBottom(int position);

        int getTabDrawableLeft(int position);

        int getTabDrawableRight(int position);

        int getTabDrawableTop(int position);
    }

}
