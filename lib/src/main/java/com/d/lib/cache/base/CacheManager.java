package com.d.lib.cache.base;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by D on 2017/10/18.
 */
public class CacheManager {
    protected volatile static ACache A_CACHE;

    protected CacheManager(@NonNull Context context) {
        init(context.getApplicationContext());
    }

    private void init(Context context) {
        if (A_CACHE == null) {
            synchronized (CacheManager.class) {
                if (A_CACHE == null) {
                    A_CACHE = ACache.get(context);
                }
            }
        }
    }
}
