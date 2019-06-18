package com.d.lib.cache.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.d.lib.cache.utils.threadpool.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public abstract class AbstractCacheFetcher<R extends AbstractCacheFetcher, K, T> extends CacheManager {
    protected final int mScheduler;
    protected final int mObserveOnScheduler;

    protected AbstractCacheFetcher(Context context) {
        this(context, Schedulers.io(), Schedulers.mainThread());
    }

    protected AbstractCacheFetcher(Context context,
                                   @Schedulers.Scheduler int scheduler,
                                   @Schedulers.Scheduler int observeOnScheduler) {
        super(context);
        mScheduler = scheduler;
        mObserveOnScheduler = observeOnScheduler;
    }

    public void load(final Context context, final K key, final CacheListener<T> listener) {
        if (key == null || key instanceof CharSequence && TextUtils.isEmpty((CharSequence) key)) {
            error(key, new CacheException("Uri must not be empty!"), listener);
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
                successImplementation(key, value);
            }
        });
    }

    private void successImplementation(final K key, final T value) {
        // Save to cache
        putLru(key, value);
        ArrayList<CacheListener<T>> listeners = getHashMap().get(key);
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
                errorImplementation(key, e);
            }
        });
    }

    private void errorImplementation(final K key, final Throwable e) {
        ArrayList<CacheListener<T>> listeners = getHashMap().get(key);
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
                ArrayList<CacheListener<T>> listeners = getHashMap().get(key);
                listeners.add(l);
                l.onLoading();
            }
            return true;
        }
        if (l != null) {
            l.onLoading();
            ArrayList<CacheListener<T>> listeners = new ArrayList<>();
            listeners.add(l);
            getHashMap().put(key, listeners);
        }
        return false;
    }

    protected boolean isLru(final K key, final CacheListener<T> listener) {
        final T valueLru = getLruCache().get(key);
        if (valueLru != null) {
            success(key, valueLru, listener);
            return true;
        }
        return false;
    }

    protected void putLru(K key, T value) {
        getLruCache().put(key, value);
    }

    protected boolean isDisk(final K key, final CacheListener<T> listener) {
        final T valueDisk = getDisk(key);
        if (valueDisk != null) {
            success(key, valueDisk, listener);
            return true;
        }
        return false;
    }

    @NonNull
    protected abstract String getPreFix();

    public abstract LruCache<K, T> getLruCache();

    public abstract HashMap<K, ArrayList<CacheListener<T>>> getHashMap();

    protected abstract void absLoad(final Context context, final K key, final CacheListener<T> listener);

    protected abstract T getDisk(final K key);

    protected abstract void putDisk(K key, T value);
}
