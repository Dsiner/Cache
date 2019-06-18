package com.d.lib.cache.base;

import java.util.ArrayList;
import java.util.HashMap;

public class LruCacheMap<K, V> {
    public final LruCache<K, V> lruCache;
    public final HashMap<K, ArrayList<CacheListener<V>>> hashMap;

    public LruCacheMap(int count) {
        lruCache = new LruCache<>(count);
        hashMap = new HashMap<>();
    }
}
