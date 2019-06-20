package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.utils.threadpool.Schedulers;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by D on 2017/10/18.
 */
public class CompressFileCacheFetcher extends CompressCacheFetcher<File> {

    private static class Singleton {
        private volatile static LruCacheMap<String, File> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, File> getInstance() {
            if (CACHE == null) {
                synchronized (Singleton.class) {
                    if (CACHE == null) {
                        CACHE = new LruCacheMap<>(12);
                    }
                }
            }
            return CACHE;
        }

        private static void release() {
            CACHE = null;
        }
    }

    @Override
    public LruCache<String, File> getLruCache() {
        return Singleton.getInstance().mLruCache;
    }

    @Override
    public HashMap<String, List<CacheListener<File>>> getHashMap() {
        return Singleton.getInstance().mHashMap;
    }

    public CompressFileCacheFetcher(@NonNull Context context,
                                    @NonNull CompressOptions requestOptions,
                                    @Schedulers.Scheduler int scheduler,
                                    @Schedulers.Scheduler int observeOnScheduler) {
        super(context, requestOptions, scheduler, observeOnScheduler);
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.COMPRESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final String url, final CacheListener<File> listener) {
        try {
            File result = needCompress() ? compressFile() : new File(mCompressOptions.provider.getPath());
            putDisk(url, result);
            success(url, result, listener);
        } catch (Throwable e) {
            e.printStackTrace();
            error(url, e, listener);
        }
    }

    public static void release() {
        Singleton.release();
    }
}
