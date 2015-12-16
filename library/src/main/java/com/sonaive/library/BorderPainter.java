package com.sonaive.library;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by liutao on 12/16/15.
 */
public class BorderPainter {
    private NinePatchDrawable mNinePatchDrawable;
    private Drawable mDrawable;
    private Rect mBorderPaddingRect;
    private ViewBoundHolder mViewBoundHolder;
    private View mLastView;
    private View mParent;
    private AnimatorSet mAnimator;
    private ValueUpdateListener mAnimateUpdateListener;

    public BorderPainter(View parent, int resId) {
        Context context = parent.getContext();
        mParent = parent;
        mBorderPaddingRect = new Rect();
        mDrawable = context.getResources().getDrawable(resId);
        mViewBoundHolder = new ViewBoundHolder();
        if (mDrawable instanceof NinePatchDrawable) {
            mNinePatchDrawable = (NinePatchDrawable) mDrawable;
            mNinePatchDrawable.getPadding(mBorderPaddingRect);
        }
    }

    public void setView(View view) {
        if (view == null) {
            mLastView = null;
            return;
        }
        Rect rect = getViewPositionInParent(view);
        if (mLastView == null) {
            mLastView = view;
            mViewBoundHolder.setLeft(rect.left);
            mViewBoundHolder.setTop(rect.top);
            mViewBoundHolder.setWidth(rect.width());
            mViewBoundHolder.setHeight(rect.height());
            return;
        }
        mLastView = view;
        startAnimation(rect);
    }

    public void draw(Canvas canvas) {
        if (mLastView == null) {
            return;
        }
        if (mNinePatchDrawable != null) {
            mNinePatchDrawable.setBounds(mViewBoundHolder.getLeft() - mBorderPaddingRect.left,
                    mViewBoundHolder.getTop() - mBorderPaddingRect.top,
                    mViewBoundHolder.getLeft() + mViewBoundHolder.getWidth() + mBorderPaddingRect.right,
                    mViewBoundHolder.getTop() + mViewBoundHolder.getHeight() + mBorderPaddingRect.bottom);
            mNinePatchDrawable.draw(canvas);
        } else {
            mDrawable.setBounds(mViewBoundHolder.getLeft(),
                    mViewBoundHolder.getTop(),
                    mViewBoundHolder.getLeft() + mViewBoundHolder.getWidth(),
                    mViewBoundHolder.getTop() + mViewBoundHolder.getHeight());
            mDrawable.draw(canvas);
        }
    }

    private Rect getViewPositionInParent(View view) {
        Point point = new Point();
        point.x = view.getLeft();
        point.y = view.getTop();
        ViewParent parent = view.getParent();
        while (parent != null && parent != mParent) {
            point.x += ((View) parent).getLeft();
            point.y += ((View) parent).getTop();
            parent = parent.getParent();
        }
        return new Rect(point.x, point.y, point.x + view.getMeasuredWidth(), point.y + view.getMeasuredHeight());
    }

    private void startAnimation(Rect rect) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (mAnimateUpdateListener == null) {
            mAnimateUpdateListener = new ValueUpdateListener();
        }
        ObjectAnimator lo = ObjectAnimator.ofInt(mViewBoundHolder, "left", rect.left);
        ObjectAnimator to = ObjectAnimator.ofInt(mViewBoundHolder, "top", rect.top);
        ObjectAnimator wo = ObjectAnimator.ofInt(mViewBoundHolder, "width", rect.width());
        ObjectAnimator ho = ObjectAnimator.ofInt(mViewBoundHolder, "height", rect.height());
        lo.addUpdateListener(mAnimateUpdateListener);
        mAnimator = new AnimatorSet();
        mAnimator.playTogether(lo, to, wo, ho);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(300);
        mAnimator.start();
    }

    public class ValueUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            ViewCompat.postInvalidateOnAnimation(mParent);
        }
    }
}
