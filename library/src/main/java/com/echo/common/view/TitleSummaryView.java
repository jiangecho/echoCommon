package com.echo.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echo.common.R;


public class TitleSummaryView extends LinearLayout {

    private TextView titleTV;
    private TextView summaryTV;
    ImageView iconImageView;

    private Drawable icon;
    private String title;
    private String summary;

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
        title = typedArray.getString(R.styleable.TitleSummaryView_titleSummarySummary);
        summary = typedArray.getString(R.styleable.TitleSummaryView_titleSummarySummary);
        icon = typedArray.getDrawable(R.styleable.TitleSummaryView_titleSummaryIcon);

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

        if (icon != null) {
            iconImageView.setImageDrawable(icon);
        }

        if (title != null) {
            titleTV.setText(title);
        }
        if (summary != null) {
            summaryTV.setText(summary);
        } else {
            summaryTV.setVisibility(GONE);
        }
    }

    public void setSummary(String summary) {
        if (summary != null) {
            this.summary = summary;
            summaryTV.setVisibility(VISIBLE);
            summaryTV.setText(summary);
        }
    }
}
