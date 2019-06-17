package com.d.lib.cache.component.compress;

import android.support.annotation.Nullable;

/**
 * RequestOptions
 * Created by D on 2018/12/20.
 **/
public class RequestOptions<T> extends com.d.lib.cache.base.RequestOptions<T> {
    public InputStreamProvider provider;

    public int leastCompressSize;
    public boolean focusAlpha;

    /**
     * do not compress when the origin image file size less than one value
     *
     * @param size the value of file size, unit KB, default 100K
     */
    public RequestOptions ignoreBy(int size) {
        this.leastCompressSize = size;
        return this;
    }

    /**
     * Do I need to keep the image's alpha channel
     *
     * @param focusAlpha <p> true - to keep alpha channel, the compress speed will be slow. </p>
     *                   <p> false - don't keep alpha channel, it might have a black background.</p>
     */
    public RequestOptions setFocusAlpha(boolean focusAlpha) {
        this.focusAlpha = focusAlpha;
        return this;
    }

    @Override
    public RequestOptions<T> placeholder(@Nullable T placeholder) {
        return (RequestOptions<T>) super.placeholder(placeholder);
    }

    @Override
    public RequestOptions<T> error(@Nullable T error) {
        return (RequestOptions<T>) super.error(error);
    }
}
