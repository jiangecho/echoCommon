package com.echo.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echo.common.R;


public class TitleSummaryView extends RelativeLayout {

    private TextView titleTV;
    private TextView summaryTV;
    private TextView tipTextView;
    private ImageView iconImageView;
    private ImageView indicatorImageView;
    private View dividerView;

    private Drawable icon;
    private String title;
    private String summary;
    private String tip;
    private boolean showIndicator;
    private boolean showDivider;

    public TitleSummaryView(Context context) {
        this(context, null);
        init(context, null, 0);
    }

    public TitleSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TitleSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStypeAttr) {
        LayoutInflater.from(context).inflate(R.layout.title_summary, this); // Attention: must pass this into inflate

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleSummaryView);
        title = typedArray.getString(R.styleable.TitleSummaryView_titleSummaryTitle);
        summary = typedArray.getString(R.styleable.TitleSummaryView_titleSummarySummary);
        icon = typedArray.getDrawable(R.styleable.TitleSummaryView_titleSummaryIcon);
        tip = typedArray.getString(R.styleable.TitleSummaryView_titleSummaryTip);
        showIndicator = typedArray.getBoolean(R.styleable.TitleSummaryView_titleSummaryShowIndicator, false);
        showDivider = typedArray.getBoolean(R.styleable.TitleSummaryView_titleSummaryShowDivider, true);

        typedArray.recycle();
    }

    /**
     * Finalize inflating a view from XML.  This is called as the last phase
     * of inflation, after all child views have been added.
     * <p>
     * <p>Even if the subclass overrides onFinishInflate, they should always be
     * sure to call the super method, so that we get called.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iconImageView = (ImageView) findViewById(R.id.icon);
        titleTV = (TextView) findViewById(R.id.title);
        summaryTV = (TextView) findViewById(R.id.summary);
        tipTextView = (TextView) findViewById(R.id.tip);
        indicatorImageView = (ImageView) findViewById(R.id.indicator);
        dividerView = findViewById(R.id.divider);

        if (icon != null) {
            iconImageView.setImageDrawable(icon);
        }

        titleTV.setText(title);
        if (summary != null) {
            summaryTV.setText(summary);
        } else {
            summaryTV.setVisibility(GONE);
        }

        tipTextView.setText(tip);
        indicatorImageView.setVisibility(showIndicator ? VISIBLE : GONE);
        dividerView.setVisibility(showDivider ? VISIBLE : GONE);
    }

    public void setDividerVisibility(int visibility) {
        if (dividerView != null) {
            dividerView.setVisibility(visibility);
        }
        showDivider = visibility == VISIBLE ? true : false;
    }

    public void setSummary(String summary) {
        this.summary = summary;
        if (summaryTV != null) {
            summaryTV.setVisibility(VISIBLE);
            summaryTV.setText(summary);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleTV != null) {
            titleTV.setVisibility(VISIBLE);
            titleTV.setText(title);
        }
    }

    public void showIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
        if (indicatorImageView != null) {
            indicatorImageView.setVisibility(showIndicator ? VISIBLE : GONE);

        }

    }

    public void setTip(String tip) {
        this.tip = tip;
        if (tipTextView != null) {
            tipTextView.setVisibility(VISIBLE);
            tipTextView.setText(tip);
        }

    }

    public void setTitleColor(int color){
        if(titleTV != null){
            tipTextView.setVisibility(VISIBLE);
            titleTV.setTextColor(color);
        }
    }

}
