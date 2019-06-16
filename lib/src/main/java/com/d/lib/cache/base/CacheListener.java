package com.d.lib.cache.base;

/**
 * Created by D on 2017/10/19.
 */
public interface CacheListener<T> {

    /**
     * Execute in the called thread
     */
    void onLoading();

    void onSuccess(T result);

    void onError(Throwable e);
}
