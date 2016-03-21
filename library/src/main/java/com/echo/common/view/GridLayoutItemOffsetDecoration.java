package com.echo.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jiangecho on 16/2/25.
 */
public class GridLayoutItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public GridLayoutItemOffsetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public GridLayoutItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        super.getItemOffsets(outRect, view, parent, state);
        if (position <= 1) {
            outRect.set(0, 0, 0, 0);
        } else {
            if (position % 2 == 0) {
                outRect.set(mItemOffset, mItemOffset / 2, mItemOffset / 2, mItemOffset / 2);
            } else {
                outRect.set(mItemOffset / 2, mItemOffset / 2, mItemOffset, mItemOffset / 2);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
