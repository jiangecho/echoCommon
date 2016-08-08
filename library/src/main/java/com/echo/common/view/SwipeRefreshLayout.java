package com.echo.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.echo.common.R;

/**
 * Created by czy on 2015/9/21.
 * modified and refactor by jiangecho
 */
public class SwipeRefreshLayout extends LinearLayout {

    private static final int STATE_PULL = 0x100;
    private static final int STATE_REFRESHING = 0x101; //already play animation ;

    private View childView;
    private FrameLayout refreshView;
    /***
     * 下拉动画view
     */
    private ImageView pullIconView;
    private int pullState;
    private int lastY, lastX;
    /***
     * 下拉动画view的最大高度，大于这个值不再变大
     */
    private int maxRefreshViewHeight;
    /***
     * 下拉起始图片
     */
    private Drawable startDrawable;
    private int closePullSpeed = 10;
    private boolean isRefreshing;
    private OnRefreshListener listener;
    /***
     * 刷新中动画
     */
    private AnimationDrawable refreshAnimation;
    private boolean enabled = true;

    public SwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    public void setRefreshing(boolean isRefreshing) {
        this.isRefreshing = true;
        if (isRefreshing) {
            pullState = STATE_REFRESHING;
            updateRefreshViewMarginTop(maxRefreshViewHeight);
            startRefreshAnimation();
        } else {
            refreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRefreshAnimationAndHideView();
                }
            }, 1000);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        setOrientation(LinearLayout.VERTICAL);
        maxRefreshViewHeight = (int) (getResources().getDisplayMetrics().density * 60);
        initRefreshView();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        initChildView();
        super.onLayout(changed, l, t, r, b);
    }

    void initChildView() {
        if (childView == null) {
            childView = getChildAt(0);
        }
    }

    void initRefreshView() {
        if (refreshView == null) {
            refreshView = new FrameLayout(getContext());

            pullIconView = new ImageView(getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            refreshView.addView(pullIconView, params);

            addView(refreshView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxRefreshViewHeight));
            LayoutParams layoutParams = (LayoutParams) refreshView.getLayoutParams();
            layoutParams.topMargin = -maxRefreshViewHeight;

            startDrawable = getDrawable(getContext(), R.drawable.anim_play3);
            pullIconView.setImageDrawable(startDrawable);
        }
    }

    public static Drawable getDrawable(Context context, int res) {
        if (Build.VERSION.SDK_INT > 20) {
            return context.getDrawable(res);
        } else {
            return context.getResources().getDrawable(res);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabled)
            interceptTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (enabled) {
            if (interceptTouchEvent(event))
                return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean interceptTouchEvent(MotionEvent event) {
        if (isRefreshing)
            return false;
        int y = (int) event.getY();
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = 0;
                pullState = 0;
                break;
            case MotionEvent.ACTION_MOVE:

                if (pullState == 0 && shouldInterceptTouchEvent()) {
                    if (lastY == 0) {
                        lastY = y;
                        lastX = x;
                    } else {
                        int subX = x - lastX;
                        if (subX == 0 || Math.abs((y - lastY) / (subX)) > 1.1f) {
                            int height = y - lastY;
                            if (height > 4) {
                                initRefreshView();
                                updateRefreshViewMarginTop(height);
                                if (pullState < STATE_PULL) {
                                    pullState = STATE_PULL;
                                    pullIconView.setImageDrawable(startDrawable);
                                    return true;
                                }
                            }
                        } else {
                            return false;
                        }
                    }
                } else if (pullState == STATE_PULL) {
                    int height = y - lastY;
                    updateRefreshViewMarginTop(height);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (pullState != 0 && lastY != 0) {
                    int height = y - lastY;
                    if (refreshView != null) {
                        if (height > maxRefreshViewHeight) {
                            if (listener != null) {
                                isRefreshing = true;
                                pullState = STATE_REFRESHING;
                                Log.e("jyj", "actionUp");
                                listener.onRefresh();
                                startRefreshAnimation();
                            } else {
                                //closePull(height);
                                hideRefreshView(0);
                            }
                        } else {
                            //closePull(height);
                            hideRefreshView(-maxRefreshViewHeight + height);
                        }
                    }
                }
                break;
        }
        return false;
    }

    private boolean shouldInterceptTouchEvent() {
        if (childView instanceof ListView) {
            if (((ListView) childView).getFirstVisiblePosition() == 0) {
                View child = ((ListView) childView).getChildAt(0);
                if (child != null) {
                    int y = (int) child.getY();
                    if (y > -2 && y < 1) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (childView.getScrollY() < 3) {
            return true;
        }
        return false;
    }

    /***
     * 播放刷新动画
     */
    private void startRefreshAnimation() {

        Log.e("jyj", "startRefreshAnimation");
        if (refreshAnimation == null)
            refreshAnimation = (AnimationDrawable) getDrawable(getContext(), R.drawable.refresh_animation);
        pullIconView.setImageDrawable(refreshAnimation);
        refreshAnimation.start();
    }


    /***
     * 刷新完成，结束刷新动画
     */
    private void stopRefreshAnimationAndHideView() {
        isRefreshing = false;
        if (refreshAnimation != null)
            refreshAnimation.stop();
        if (refreshView != null && pullState != 0) {
            pullState = 0;
            pullIconView.setImageDrawable(startDrawable);
            hideRefreshView(0);
        }
    }

    private void hideRefreshView(int currentTopMargin) {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                int targetTopMargin = -maxRefreshViewHeight;
                //super.applyTransformation(interpolatedTime, t);
                LayoutParams layoutParams = (LayoutParams) refreshView.getLayoutParams();
                layoutParams.topMargin = (int) (currentTopMargin + (targetTopMargin - currentTopMargin) * interpolatedTime);
                refreshView.setLayoutParams(layoutParams);
            }
        };
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        refreshView.startAnimation(animation);
    }

    /***
     * 更新下拉view的marginTop
     *
     * @param moveHeight
     */
    private void updateRefreshViewMarginTop(int moveHeight) {
        moveHeight = moveHeight < 0 ? 0 : moveHeight;
        int marginTop = (moveHeight - maxRefreshViewHeight) < 0 ? moveHeight - maxRefreshViewHeight : 0;
        LayoutParams layoutParams = (LayoutParams) refreshView.getLayoutParams();
        layoutParams.topMargin = marginTop;
        refreshView.setLayoutParams(layoutParams);

        pullIconView.invalidate();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
