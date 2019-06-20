package com.d.lib.cache.base;

import java.util.HashMap;
import java.util.List;

public class LruCacheMap<K, V> {
    public final LruCache<K, V> mLruCache;
    public final HashMap<K, List<CacheListener<V>>> mHashMap;

    public LruCacheMap(int count) {
        mLruCache = new LruCache<>(count);
        mHashMap = new HashMap<>();
    }
}
