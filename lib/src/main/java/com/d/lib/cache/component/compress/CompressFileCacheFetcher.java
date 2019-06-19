package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<File>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public CompressFileCacheFetcher(Context context,
                                    int scheduler, int observeOnScheduler,
                                    RequestOptions requestOptions) {
        super(context, scheduler, observeOnScheduler, requestOptions);
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.COMPRESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final String url, final CacheListener<File> listener) {
        compress(new CacheListener<File>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(File result) {
                putDisk(url, result);
                success(url, result, listener);
            }

            @Override
            public void onError(Throwable e) {
                error(url, e, listener);
            }
        });
    }

    public static void release() {
        Singleton.release();
    }
}
