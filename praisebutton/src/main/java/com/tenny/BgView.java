package com.tenny;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 背景View 撑开布局
 */

public class BgView extends View {

    private int width = 0;
    private int height = 0;

    public BgView(Context context) {
        super(context);
    }

    public BgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (width != 0 && height != 0)
            setMeasuredDimension(width, height);
    }

}
