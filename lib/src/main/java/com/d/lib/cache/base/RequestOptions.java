package com.d.lib.cache.base;

import android.support.annotation.Nullable;

public class RequestOptions<PlaceHolder> {
    public PlaceHolder mPlaceHolder;
    public PlaceHolder mError;

    public RequestOptions<PlaceHolder> placeholder(@Nullable PlaceHolder placeholder) {
        this.mPlaceHolder = placeholder;
        return this;
    }

    public RequestOptions<PlaceHolder> error(@Nullable PlaceHolder error) {
        this.mError = error;
        return this;
    }
}
