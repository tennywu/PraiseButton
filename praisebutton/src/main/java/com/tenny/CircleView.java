package com.tenny;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

/**
 * 点赞里面的圆形背景
 */

public class CircleView extends View {

    private int CIRCLE_COLOR = 0xFFFFC107;

    private Paint circlePaint = new Paint();
    private Paint maskPaint = new Paint();

    private Bitmap tempBitmap;
    private Canvas tempCanvas;

    private float circleRadiusProgress = 0f;

    private int width = 0;
    private int height = 0;

    private int maxCircleSize;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAlpha(0);
        circlePaint.setColor(CIRCLE_COLOR);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
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


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxCircleSize = w / 2;
        tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        tempCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR);
        tempCanvas.drawCircle(getWidth() / 2, getHeight() / 2, circleRadiusProgress * maxCircleSize, circlePaint);
        tempCanvas.drawCircle(getWidth() / 2, getHeight() / 2, circleRadiusProgress * maxCircleSize/3, maskPaint);
        canvas.drawBitmap(tempBitmap, 0, 0, null);
    }


    public void setCircleRadiusProgress(float circleRadiusProgress) {
        this.circleRadiusProgress = circleRadiusProgress;
        updateCircleAlpha();
        postInvalidate();
    }

    private void updateCircleAlpha() {
        float progress = (float) Utils.clamp(circleRadiusProgress, 0.9f, 1f);
        int alpha = (int) Utils.mapValueFromRangeToRange(progress, 0.6f, 1f, 255, 0);
        circlePaint.setAlpha(alpha);
    }

    public float getCircleRadiusProgress() {
        return circleRadiusProgress;
    }


    public static final Property<CircleView, Float> CIRCLE_RADIUS_PROGRESS =
            new Property<CircleView, Float>(Float.class, "circleRadiusProgress") {
                @Override
                public Float get(CircleView object) {
                    return object.getCircleRadiusProgress();
                }

                @Override
                public void set(CircleView object, Float value) {
                    object.setCircleRadiusProgress(value);
                }
            };

    public void setColor(@ColorInt int color) {
        CIRCLE_COLOR = color;
        circlePaint.setColor(CIRCLE_COLOR);
        invalidate();
    }

}