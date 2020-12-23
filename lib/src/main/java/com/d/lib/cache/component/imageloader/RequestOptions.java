package com.d.lib.cache.component.imageloader;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * RequestOptions
 * Created by D on 2018/12/20.
 **/
public class RequestOptions {
    public Drawable mPlaceHolder;
    public Drawable mError;

    public RequestOptions placeholder(@Nullable Drawable drawable) {
        this.mPlaceHolder = drawable;
        return this;
    }

    public RequestOptions error(@Nullable Drawable drawable) {
        this.mError = drawable;
        return this;
    }
}
