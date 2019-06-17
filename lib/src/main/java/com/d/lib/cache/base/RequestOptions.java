package com.d.lib.cache.base;

import android.support.annotation.Nullable;

public class RequestOptions<PlaceHolder> {
    public PlaceHolder placeHolder;
    public PlaceHolder error;

    public RequestOptions<PlaceHolder> placeholder(@Nullable PlaceHolder placeholder) {
        this.placeHolder = placeholder;
        return this;
    }

    public RequestOptions<PlaceHolder> error(@Nullable PlaceHolder error) {
        this.error = error;
        return this;
    }
}
