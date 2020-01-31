package com.appsolace.estore.customanimation;


public interface CircularAnimator {


    /**
     * Whether enable {@link android.graphics.Canvas} to clip
     * outlines of the certain or not
     *
     * @param isAnimated Whether it's animated or not
     * @see #setCenter(int, int)
     * @see #setRadius(float)
     */
    void setAnimated(boolean isAnimated);

    /**
     * Sets central points where to start clipping
     * certain child
     *
     * @param cx x point of child
     * @param cy y point of child
     * @see #setAnimated(boolean) (float, float)
     * @see #setRadius(float)
     */
    void setCenter(int cx, int cy);

    /**
     * Used with animator to animate view clipping
     *
     * @param value clip radius
     */
    void setRadius(float value);

    /**
     * Used with animator to animate view clipping
     *
     * @return current radius
     */
    float getRadius();

}