package com.d.lib.cache.base;

import android.support.annotation.Nullable;

public class RequestOptions<PlaceHolder> {
    public PlaceHolder placeHolder;
    public PlaceHolder error;
    public boolean isCacheable = true;
    public int diskCacheStrategy = DiskCacheStrategies.RESOURCE;

    public RequestOptions<PlaceHolder> placeholder(@Nullable PlaceHolder placeholder) {
        this.placeHolder = placeholder;
        return this;
    }

    public RequestOptions<PlaceHolder> error(@Nullable PlaceHolder error) {
        this.error = error;
        return this;
    }

    public RequestOptions<PlaceHolder> skipMemoryCache(boolean skip) {
        this.isCacheable = !skip;
        return this;
    }

    public RequestOptions<PlaceHolder> diskCacheStrategy(@DiskCacheStrategies.DiskCacheStrategy int strategy) {
        this.diskCacheStrategy = strategy;
        return this;
    }
}
