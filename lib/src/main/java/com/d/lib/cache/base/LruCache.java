package com.d.lib.cache.base;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by D on 2017/10/19.
 */
public class LruCache<K, V> {
    private int mCount;
    private LinkedHashMap<K, V> mMap;

    public LruCache() {
        this(12);
    }

    public LruCache(int count) {
        this.mMap = new LinkedHashMap<>();
        this.mCount = count;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public void put(K key, V value) {
        calculateSize(key);
        if (mCount > 0) {
            mMap.put(key, value);
        }
    }

    public V get(K key) {
        return mMap.get(key);
    }

    public boolean containsKey(K key) {
        return mMap.containsKey(key);
    }

    public void remove(K key) {
        mMap.remove(key);
    }

    public void clear() {
        mMap.clear();
    }

    private void calculateSize(K key) {
        if (mMap.size() >= mCount && !mMap.containsKey(key)) {
            Iterator ite = mMap.entrySet().iterator();
            if (ite.hasNext()) {
                Map.Entry<K, V> entry = (Map.Entry<K, V>) ite.next();
                mMap.remove(entry.getKey());
            }
        }
    }
}
