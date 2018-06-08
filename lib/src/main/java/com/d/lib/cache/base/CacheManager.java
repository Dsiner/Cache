package com.d.lib.cache.base;

import android.content.Context;

/**
 * Created by D on 2017/10/18.
 */
public class CacheManager {
    protected static ACache aCache;

    protected CacheManager(Context context) {
        init(context.getApplicationContext());
    }

    private void init(Context context) {
        if (aCache == null) {
            synchronized (CacheManager.class) {
                if (aCache == null) {
                    aCache = ACache.get(context);
                }
            }
        }
    }
}
