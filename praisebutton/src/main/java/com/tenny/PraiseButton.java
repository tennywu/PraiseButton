package com.tenny;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tenny.praiseView.R;

import java.util.List;


public class PraiseButton extends FrameLayout implements View.OnClickListener {

    private BgView bgView;
    private ImageView icon;
    private FireworkView firewokView;
    private CircleView circleView;
    private OnPraiseListener likeListener;
    private OnAnimationEndListener animationEndListener;
    private int fireworkColor;
    private int circleColor;
    private int iconSize;


    private float animationScaleFactor;

    private boolean isChecked;


    private boolean isEnabled;
    private AnimatorSet animatorSet;

    private Drawable likeDrawable;
    private Drawable unLikeDrawable;

    public PraiseButton(Context context) {
        this(context, null);
    }

    public PraiseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(!isInEditMode())
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(getContext()).inflate(R.layout.praiseview, this, true);
        bgView = (BgView) findViewById(R.id.bg);
        icon = (ImageView) findViewById(R.id.icon);
        firewokView = (FireworkView) findViewById(R.id.fireworks);
        circleView = (CircleView) findViewById(R.id.circle);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PraiseButton, defStyle, 0);

        iconSize = array.getDimensionPixelSize(R.styleable.PraiseButton_icon_size, -1);
        if (iconSize == -1)
            iconSize = 40;

        likeDrawable = getDrawableFromResource(array, R.styleable.PraiseButton_like_drawable);

        if (likeDrawable != null)
            setLikeDrawable(likeDrawable);

        unLikeDrawable = getDrawableFromResource(array, R.styleable.PraiseButton_unlike_drawable);

        if (unLikeDrawable != null)
            setUnlikeDrawable(unLikeDrawable);

        circleColor = array.getColor(R.styleable.PraiseButton_circle_color, 0);

        if (circleColor != 0)
            circleView.setColor(circleColor);


        fireworkColor = array.getColor(R.styleable.PraiseButton_firework_color, 0);
        if (fireworkColor != 0){
            firewokView.setColor(fireworkColor);
        }



        setEnabled(array.getBoolean(R.styleable.PraiseButton_is_enabled, true));
        Boolean status = array.getBoolean(R.styleable.PraiseButton_liked, false);
        setAnimationScaleFactor(array.getFloat(R.styleable.PraiseButton_anim_scale_factor, 2));
        setLiked(status);
        setOnClickListener(this);
        array.recycle();
    }

    private Drawable getDrawableFromResource(TypedArray array, int styleableIndexId) {
        int id = array.getResourceId(styleableIndexId, -1);

        return (-1 != id) ? ContextCompat.getDrawable(getContext(), id) : null;
    }


    @Override
    public void onClick(View v) {

        if (!isEnabled)
            return;

        isChecked = !isChecked;

        icon.setImageDrawable(isChecked ? likeDrawable : unLikeDrawable);

        if (likeListener != null) {
            if (isChecked) {
                likeListener.praised(this);
            } else {
                likeListener.unpraised(this);
            }
        }

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (isChecked) {
            icon.animate().cancel();
//            icon.setScaleX(0);
//            icon.setScaleY(0);
            firewokView.setVisibility(VISIBLE);
            firewokView.setCurrentProgress(0);
            circleView.setCircleRadiusProgress(0);

            animatorSet = new AnimatorSet();

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(icon, ImageView.SCALE_Y, 1.0f, 2f, 1.0f);
            starScaleYAnimator.setDuration(500);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(icon, ImageView.SCALE_X, 1.0f, 2f, 1.0f);
            starScaleXAnimator.setDuration(500);

            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(icon, "translationY", 0, -20, 0);
            translationYAnimator.setDuration(500);

            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(icon, "rotation", 0, 10, 0, -10, 0);
            rotateAnimator.setDuration(500);

            ObjectAnimator fireworkAnimator = ObjectAnimator.ofFloat(firewokView, FireworkView.FIREWORK_PROGRESS, 0, 1f);
            fireworkAnimator.setDuration(250);
            rotateAnimator.setStartDelay(250);

            ObjectAnimator roundAnimator = ObjectAnimator.ofFloat(circleView, CircleView.CIRCLE_RADIUS_PROGRESS, 0f, 1f);
            roundAnimator.setDuration(250);
            rotateAnimator.setStartDelay(250);

            animatorSet.playTogether(
                    roundAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    translationYAnimator,
                    rotateAnimator,
                    fireworkAnimator
            );

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    firewokView.setCurrentProgress(0);
                    firewokView.setVisibility(INVISIBLE);
                    circleView.setCircleRadiusProgress(0);
                    icon.setRotation(0);
                    icon.setTranslationY(0);
                    icon.setScaleX(1);
                    icon.setScaleY(1);
                }

                @Override public void onAnimationEnd(Animator animation) {
                    if(animationEndListener != null) {
                      animationEndListener.onAnimationEnd(PraiseButton.this);
                    }
                }
            });

            animatorSet.start();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled)
            return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;
        }
        return true;
    }


    public void setLikeDrawableRes(@DrawableRes int resId) {
        likeDrawable = ContextCompat.getDrawable(getContext(), resId);

        if (iconSize != 0) {
            likeDrawable = Utils.resizeDrawable(getContext(), likeDrawable, iconSize, iconSize);
        }

        if (isChecked) {
            icon.setImageDrawable(likeDrawable);
        }
    }


    public void setLikeDrawable(Drawable likeDrawable) {
        this.likeDrawable = likeDrawable;

        if (iconSize != 0) {
            this.likeDrawable = Utils.resizeDrawable(getContext(), likeDrawable, iconSize, iconSize);
        }

        if (isChecked) {
            icon.setImageDrawable(this.likeDrawable);
        }
    }


    public void setUnlikeDrawableRes(@DrawableRes int resId) {
        unLikeDrawable = ContextCompat.getDrawable(getContext(), resId);

        if (iconSize != 0) {
            unLikeDrawable = Utils.resizeDrawable(getContext(), unLikeDrawable, iconSize, iconSize);
        }

        if (!isChecked) {
            icon.setImageDrawable(unLikeDrawable);
        }
    }


    public void setUnlikeDrawable(Drawable unLikeDrawable) {
        this.unLikeDrawable = unLikeDrawable;

        if (iconSize != 0) {
            this.unLikeDrawable = Utils.resizeDrawable(getContext(), unLikeDrawable, iconSize, iconSize);
        }

        if (!isChecked) {
            icon.setImageDrawable(this.unLikeDrawable);
        }
    }


    /**
     *设置icon大小
     * @param iconSize
     */

    public void setIconSizeDp(int iconSize) {
        setIconSizePx((int) Utils.dipToPixels(getContext(), (float) iconSize));
    }

    /**
     *设置icon大小
     * @param iconSize
     */
    public void setIconSizePx(int iconSize) {
        this.iconSize = iconSize;
        setEffectsViewSize();
        this.unLikeDrawable = Utils.resizeDrawable(getContext(), unLikeDrawable, iconSize, iconSize);
        this.likeDrawable = Utils.resizeDrawable(getContext(), likeDrawable, iconSize, iconSize);
    }


    public void setOnLikeListener(OnPraiseListener likeListener) {
        this.likeListener = likeListener;
    }


    public void setOnAnimationEndListener(OnAnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }



    public void setFireworkColorInt(@ColorInt int color) {
        this.fireworkColor = color;
        firewokView.setColor(fireworkColor);
    }

    public void setFireworkColorRes(@ColorRes int color) {
        this.fireworkColor = ContextCompat.getColor(getContext(), color);
        firewokView.setColor(fireworkColor);
    }

    public void setCircleColorInt(@ColorInt int circleColor) {
        this.circleColor = circleColor;
        circleView.setColor(circleColor);
    }

    public void setCircleColorRes(@ColorRes int circleColor) {
        this.circleColor = ContextCompat.getColor(getContext(), circleColor);
        circleView.setColor(this.circleColor);
    }

    /**
     * 设置背景烟花及圆圈大小
     */
    private void setEffectsViewSize() {
        if (iconSize != 0) {
            firewokView.setSize((int) (iconSize * animationScaleFactor), (int) (iconSize * animationScaleFactor));
            circleView.setSize((int) (iconSize * animationScaleFactor), (int) (iconSize * animationScaleFactor));
            bgView.setSize((int) (iconSize * animationScaleFactor*1.5), (int) (iconSize * animationScaleFactor*1.5));
        }
    }


    public void setLiked(Boolean status) {
        if (status) {
            isChecked = true;
            icon.setImageDrawable(likeDrawable);
        } else {
            isChecked = false;
            icon.setImageDrawable(unLikeDrawable);
        }
    }


    public boolean isLiked() {
        return isChecked;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


    /**
     * 设置背景烟花及圆圈等动画效果的大小
     */
    public void setAnimationScaleFactor(float animationScaleFactor) {
        this.animationScaleFactor = animationScaleFactor;

        setEffectsViewSize();
    }

}
