package com.d.lib.cache.component.compress;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * RequestOptions
 * Created by D on 2018/12/20.
 **/
public class RequestOptions extends com.d.lib.cache.base.RequestOptions<Drawable> {
    public InputStreamProvider mProvider;

    public int mLeastCompressSize;
    public boolean mFocusAlpha;

    /**
     * do not compress when the origin image file size less than one value
     *
     * @param size the value of file size, unit KB, default 100K
     */
    public RequestOptions ignoreBy(int size) {
        this.mLeastCompressSize = size;
        return this;
    }

    /**
     * Do I need to keep the image's alpha channel
     *
     * @param focusAlpha <p> true - to keep alpha channel, the compress speed will be slow. </p>
     *                   <p> false - don't keep alpha channel, it might have a black background.</p>
     */
    public RequestOptions setFocusAlpha(boolean focusAlpha) {
        this.mFocusAlpha = focusAlpha;
        return this;
    }

    /**
     * Returns a {@link RequestOptions} object with {@link #placeholder(Drawable)} set.
     */
    @Override
    public RequestOptions placeholder(@Nullable Drawable drawable) {
        this.mPlaceHolder = drawable;
        return this;
    }

    @Override
    public RequestOptions error(@Nullable Drawable drawable) {
        this.mError = drawable;
        return this;
    }
}
