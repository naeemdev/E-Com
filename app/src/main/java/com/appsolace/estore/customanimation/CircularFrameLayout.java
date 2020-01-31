package com.appsolace.estore.customanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class CircularFrameLayout extends FrameLayout implements CircularAnimator {

    Path mRevealPath;

    boolean mClipOutlines;

    int mCenterX;
    int mCenterY;
    float mRadius;

    View mTarget;
    View mSource;


    public CircularFrameLayout(Context context) {
        this(context, null);
    }

    public CircularFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRevealPath = new Path();
    }


    /**
     * Reference the target of circular animation to appear
     *
     * @param target View to clip outlines
     */
    public void setTarget(View target) {
        mTarget = target;
    }

    /**
     * Reference the target of circular animation to disappear
     *
     * @param source View to clip outlines
     */
    public void setSource(View source) {
        mSource = source;
    }

    /**
     * Epicenter of animation circle reveal
     */
    @Override
    public void setCenter(int centerX, int centerY) {
        mCenterX = centerX;
        mCenterY = centerY;
    }

    /**
     * Flag that animation is enabled (so the view should use the clipping)
     * Clipping won't work if it's not set to true
     */
    @Override
    public void setAnimated(boolean isAnimated) {
        mClipOutlines = isAnimated;
    }

    /**
     * Sets the clipping circle radius size. Radius won't be smaller than 1F
     */
    @Override
    public void setRadius(float radius) {
        mRadius = Math.max(1F, radius);
        invalidate();
    }

    /**
     * Returns the clipping circle radius size
     */
    @Override
    public float getRadius() {
        return mRadius;
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        if (mClipOutlines && (child == mTarget || child == mSource)) {

            canvas.save();

            mRevealPath.reset();
            mRevealPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);

            if (child == mTarget) {
                //appearing
                canvas.clipPath(mRevealPath, Region.Op.INTERSECT);
            } else {
                //disappearing
                canvas.clipPath(mRevealPath, Region.Op.DIFFERENCE);
            }

            boolean isInvalided = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return isInvalided;
        }

        return super.drawChild(canvas, child, drawingTime);
    }

}
