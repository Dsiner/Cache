package com.d.lib.cache;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.d.lib.cache.base.DurationCacheManager;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.listener.DurationView;

/**
 * Cahce -获取媒体时长
 * Created by D on 2017/10/19.
 */
public class DurationCache {
    private Context context;
    private String url;
    private long placeholder;

    public static DurationCache with(Context context) {
        DurationCache cache = new DurationCache();
        cache.context = context;
        return cache;
    }

    public DurationCache load(String url) {
        this.url = url;
        return this;
    }

    public DurationCache placeholder(long duration) {
        this.placeholder = duration;
        return this;
    }

    public void into(final View view) {
        if (view == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            //just error
            if (view instanceof DurationView) {
                ((DurationView) view).setDuration(placeholder);
            }
            return;
        }
        Object tag = view.getTag(R.id.cache_duration);
        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
            //not refresh
            return;
        }
        view.setTag(R.id.cache_duration, url);
        DurationCacheManager.getInstance(context).load(context, url, new CacheListener<Long>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(Long result) {
                Object tag = view.getTag(R.id.cache_duration);
                if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                    if (view instanceof DurationView) {
                        ((DurationView) view).setDuration(result);
                    }
                }
            }

            @Override
            public void onError() {
                Object tag = view.getTag(R.id.cache_duration);
                if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                    if (view instanceof DurationView) {
                        ((DurationView) view).setDuration(placeholder);
                    }
                }
            }
        });
    }

    public void listener(CacheListener<Long> l) {
        if (TextUtils.isEmpty(url)) {
            //just error
            if (l != null) {
                l.onError();
            }
            return;
        }
        DurationCacheManager.getInstance(context).load(context, url, l);
    }
}
