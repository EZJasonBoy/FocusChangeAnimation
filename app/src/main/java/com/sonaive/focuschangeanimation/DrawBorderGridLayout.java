package com.sonaive.focuschangeanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridLayout;

import com.sonaive.library.BorderPainter;

/**
 * Created by liutao on 12/16/15.
 */
public class DrawBorderGridLayout extends GridLayout implements View.OnFocusChangeListener {

    private BorderPainter mBorderPainter;

    public DrawBorderGridLayout(Context context) {
        super(context);
    }

    public DrawBorderGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBorderPainter = new BorderPainter(this, R.drawable.border);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnFocusChangeListener(this);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mBorderPainter.draw(canvas);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mBorderPainter.setView(v);
        }
    }
}
