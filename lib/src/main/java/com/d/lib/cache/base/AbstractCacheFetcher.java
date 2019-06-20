package com.d.lib.cache.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.cache.utils.threadpool.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by D on 2017/10/18.
 */
public abstract class AbstractCacheFetcher<R extends AbstractCacheFetcher, K, T> extends CacheManager {
    protected final RequestOptions mRequestOptions;
    protected final int mScheduler;
    protected final int mObserveOnScheduler;

    protected AbstractCacheFetcher(@NonNull Context context,
                                   @NonNull RequestOptions requestOptions,
                                   @Schedulers.Scheduler int scheduler,
                                   @Schedulers.Scheduler int observeOnScheduler) {
        super(context);
        mRequestOptions = requestOptions;
        mScheduler = scheduler;
        mObserveOnScheduler = observeOnScheduler;
    }

    public void load(final Context context, final K key, final CacheListener<T> listener) {
        if (key == null || key instanceof CharSequence && TextUtils.isEmpty((CharSequence) key)) {
            error(key, new IllegalArgumentException("Uri must not be empty!"), listener);
            return;
        }
        if (isLoading(key, listener)) {
            return;
        }
        if (isLru(key, listener)) {
            return;
        }
        Schedulers.switchThread(mScheduler, new Runnable() {
            @Override
            public void run() {
                if (isDisk(key, listener)) {
                    return;
                }
                absLoad(context, key, listener);
            }
        });
    }

    protected void success(final K key, final T value, final CacheListener<T> l) {
        Schedulers.switchThread(mObserveOnScheduler, new Runnable() {
            @Override
            public void run() {
                successImpl(key, value);
            }
        });
    }

    private void successImpl(final K key, final T value) {
        // Save to cache
        putLru(key, value);
        List<CacheListener<T>> listeners = getHashMap().get(key);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                CacheListener<T> listener = listeners.get(i);
                listener.onSuccess(value);
            }
            getHashMap().remove(key);
        }
    }

    protected void error(final K key, final Throwable e, final CacheListener<T> l) {
        Schedulers.switchThread(mObserveOnScheduler, new Runnable() {
            @Override
            public void run() {
                errorImpl(key, e);
            }
        });
    }

    private void errorImpl(final K key, final Throwable e) {
        List<CacheListener<T>> listeners = getHashMap().get(key);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onError(e);
            }
            getHashMap().remove(key);
        }
    }

    protected boolean isLoading(final K key, final CacheListener<T> l) {
        if (getHashMap().containsKey(key)) {
            if (l != null) {
                List<CacheListener<T>> listeners = getHashMap().get(key);
                listeners.add(l);
                l.onLoading();
            }
            return true;
        }
        if (l != null) {
            l.onLoading();
            List<CacheListener<T>> listeners = new ArrayList<>();
            listeners.add(l);
            getHashMap().put(key, listeners);
        }
        return false;
    }

    protected boolean isLru(final K key, final CacheListener<T> listener) {
        if (!mRequestOptions.isCacheable) {
            return false;
        }
        final T valueLru = getLruCache().get(key);
        if (valueLru != null) {
            success(key, valueLru, listener);
            return true;
        }
        return false;
    }

    protected void putLru(K key, T value) {
        if (!mRequestOptions.isCacheable) {
            return;
        }
        getLruCache().put(key, value);
    }

    protected boolean isDisk(final K key, final CacheListener<T> listener) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return false;
        }
        final T valueDisk = getDisk(key);
        if (valueDisk != null) {
            success(key, valueDisk, listener);
            return true;
        }
        return false;
    }

    @NonNull
    protected abstract String getPreFix();

    @NonNull
    public abstract LruCache<K, T> getLruCache();

    @NonNull
    public abstract HashMap<K, List<CacheListener<T>>> getHashMap();

    protected abstract void absLoad(final Context context, final K key, final CacheListener<T> listener);

    protected abstract T getDisk(final K key);

    protected abstract void putDisk(K key, T value);
}
