package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.AbstractCacheManager;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.PreFix;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class CompressFileCacheManager extends AbstractCacheManager<CompressFileCacheManager,
        InputStreamProvider, File> {
    private RequestOptions mRequestOptions;

    private static class Singleton {
        private volatile static LruCache<InputStreamProvider, File> LRUCACHE = new LruCache<>(12);
        private volatile static HashMap<InputStreamProvider, ArrayList<CacheListener<File>>> HASHMAP = new HashMap<>();

        private static LruCache<InputStreamProvider, File> getLruCache() {
            if (LRUCACHE == null) {
                synchronized (Singleton.class) {
                    if (LRUCACHE == null) {
                        LRUCACHE = new LruCache<>(12);
                    }
                }
            }
            return LRUCACHE;
        }

        private static HashMap<InputStreamProvider, ArrayList<CacheListener<File>>> getHashMap() {
            if (HASHMAP == null) {
                synchronized (Singleton.class) {
                    if (HASHMAP == null) {
                        HASHMAP = new HashMap<>();
                    }
                }
            }
            return HASHMAP;
        }

        private static void release() {
            LRUCACHE = null;
            HASHMAP = null;
        }
    }

    public CompressFileCacheManager(Context context) {
        super(context);
    }

    @Override
    public LruCache<InputStreamProvider, File> getLruCache() {
        return Singleton.getLruCache();
    }

    @Override
    public HashMap<InputStreamProvider, ArrayList<CacheListener<File>>> getHashMap() {
        return Singleton.getHashMap();
    }

    public static void release() {
        Singleton.release();
    }

    public CompressFileCacheManager setRequestOptions(RequestOptions requestOptions) {
        this.mRequestOptions = requestOptions;
        return this;
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.COMPRESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final InputStreamProvider url, final CacheListener<File> listener) {
        Compress helper = new Compress(context, mRequestOptions);
        helper.compress(new CacheListener<File>() {
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

    @Override
    protected File getDisk(InputStreamProvider url) {
        return null;
    }

    @Override
    protected void putDisk(InputStreamProvider url, File value) {

    }
}
