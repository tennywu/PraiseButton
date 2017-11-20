package com.tenny;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * 点赞顶部的烟花
 */
public class FireworkView extends View {
    private static final int LINES_COUNT = 5;
    private static final int LINES_POSITION_ANGLE = 51;

    private int FIREWORK_COLOR = 0xFFFF5722;

    private int width = 0;
    private int height = 0;

    private Paint fireworkPaint = new Paint();

    private int centerX;
    private int centerY;

    private float maxFireworkRadius;

    private float currentProgress = 0;


    private float startRadius = 0;
    private float endRadius = 0;


    public FireworkView(Context context) {
        super(context);
        init();
    }

    public FireworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FireworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        fireworkPaint.setStyle(Paint.Style.FILL);
        fireworkPaint.setStrokeWidth(5);
        fireworkPaint.setStrokeCap(Paint.Cap.ROUND);
        fireworkPaint.setAlpha(0);
        fireworkPaint.setColor(FIREWORK_COLOR);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        maxFireworkRadius = w / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < LINES_COUNT; i++) {
            int start_X = (int) (centerX + startRadius * Math.cos(((2-i) * LINES_POSITION_ANGLE + 270) * Math.PI / 180));
            int start_Y = (int) (centerY + startRadius * Math.sin(((2-i) * LINES_POSITION_ANGLE + 270) * Math.PI / 180));
            int end_X = (int) (centerX + endRadius * Math.cos(((2-i) * LINES_POSITION_ANGLE + 270) * Math.PI / 180));
            int end_Y = (int) (centerY + endRadius * Math.sin(((2-i) * LINES_POSITION_ANGLE + 270) * Math.PI / 180));
            canvas.drawLine(start_X, start_Y, end_X, end_Y, fireworkPaint);
        }
    }


    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;

        updateLinesPosition();
        updateFireworkAlpha();

        postInvalidate();
    }

    public float getCurrentProgress() {
        return currentProgress;
    }

    private void updateLinesPosition() {
        this.startRadius = (0.2f + 0.8f * currentProgress) * maxFireworkRadius;
        this.endRadius = (0.3f + 0.7f * currentProgress) * maxFireworkRadius;
    }


    public void setColor(@ColorInt int color) {
        FIREWORK_COLOR = color;
        fireworkPaint.setColor(color);
        invalidate();
    }

    private void updateFireworkAlpha() {
        float progress = (float) Utils.clamp(currentProgress, 0.6f, 1f);
        int alpha = (int) Utils.mapValueFromRangeToRange(progress, 0.6f, 1f, 255, 0);
        fireworkPaint.setAlpha(alpha);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        fireworkPaint.setStrokeWidth(width/20);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (width != 0 && height != 0)
            setMeasuredDimension(width, height);
    }


    public static final Property<FireworkView, Float> FIREWORK_PROGRESS = new Property<FireworkView, Float>(Float.class, "fireworkProgress") {
        @Override
        public Float get(FireworkView object) {
            return object.getCurrentProgress();
        }

        @Override
        public void set(FireworkView object, Float value) {
            object.setCurrentProgress(value);
        }
    };

}