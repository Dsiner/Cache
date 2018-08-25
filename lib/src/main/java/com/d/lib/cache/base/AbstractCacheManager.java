package com.d.lib.cache.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.utils.thread.TaskScheduler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public abstract class AbstractCacheManager<T> extends CacheManager {
    protected Handler handler;
    protected LruCache<String, T> lruCache;
    protected HashMap<String, ArrayList<CacheListener<T>>> hashMap;

    protected AbstractCacheManager(Context context) {
        super(context);
        handler = new Handler(Looper.getMainLooper());
        lruCache = new LruCache<>();
        hashMap = new HashMap<>();
    }

    public void load(final Context context, final String url, final CacheListener<T> listener) {
        if (isLoading(url, listener)) {
            return;
        }
        if (isLru(url, listener)) {
            return;
        }
        TaskScheduler.executeTask(new Runnable() {
            @Override
            public void run() {
                if (isDisk(url, listener)) {
                    return;
                }
                absLoad(context, url, listener);
            }
        });
    }

    protected void success(final String url, final T value, final CacheListener<T> l) {
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                successImplementation(url, value);
            }
        });
    }

    private void successImplementation(final String url, final T value) {
        // Save to cache
        putLru(url, value);
        ArrayList<CacheListener<T>> listeners = hashMap.get(url);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                CacheListener<T> listener = listeners.get(i);
                listener.onSuccess(value);
            }
            hashMap.remove(url);
        }
    }

    protected void error(final String url, final Throwable e, final CacheListener<T> listener) {
        TaskScheduler.executeMain(new Runnable() {
            @Override
            public void run() {
                errorImplementation(url, e);
            }
        });
    }

    private void errorImplementation(final String url, final Throwable e) {
        ArrayList<CacheListener<T>> listeners = hashMap.get(url);
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onError(e);
            }
            hashMap.remove(url);
        }
    }

    protected boolean isLoading(final String url, final CacheListener<T> l) {
        if (hashMap.containsKey(url)) {
            if (l != null) {
                ArrayList<CacheListener<T>> listeners = hashMap.get(url);
                listeners.add(l);
                l.onLoading();
            }
            return true;
        }
        if (l != null) {
            l.onLoading();
            ArrayList<CacheListener<T>> listeners = new ArrayList<>();
            listeners.add(l);
            hashMap.put(url, listeners);
        }
        return false;
    }

    protected boolean isLru(final String url, final CacheListener<T> listener) {
        final T valueLru = lruCache.get(url);
        if (valueLru != null) {
            success(url, valueLru, listener);
            return true;
        }
        return false;
    }

    protected void putLru(String url, T value) {
        lruCache.put(url, value);
    }

    protected boolean isDisk(final String url, final CacheListener<T> listener) {
        final T valueDisk = getDisk(url);
        if (valueDisk != null) {
            success(url, valueDisk, listener);
            return true;
        }
        return false;
    }

    public void release() {
        lruCache.clear();
    }

    protected abstract void absLoad(final Context context, final String url, final CacheListener<T> listener);

    protected abstract T getDisk(final String url);

    protected abstract void putDisk(String url, T value);
}
