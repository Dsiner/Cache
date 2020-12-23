package com.d.lib.cache;

import com.d.lib.cache.util.threadpool.ThreadPool;

/**
 * Cache
 * Created by D on 2018/8/25.
 */
public class Cache {
    public static void setThreadPool(ThreadPool pool) {
        ThreadPool.setThreadPool(pool);
    }
}
