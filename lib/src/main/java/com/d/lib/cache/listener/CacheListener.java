package com.d.lib.cache.listener;

/**
 * Created by D on 2017/10/19.
 */
public interface CacheListener<T> {
    void onLoading();

    void onSuccess(T resule);

    void onError();
}
