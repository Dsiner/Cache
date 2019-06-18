package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.AbstractCacheFetcher;
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
public class CompressFileCacheFetcher extends AbstractCacheFetcher<CompressFileCacheFetcher,
        InputStreamProvider, File> {
    private RequestOptions mRequestOptions;

    private static class Singleton {
        private volatile static LruCacheMap<InputStreamProvider, File> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<InputStreamProvider, File> getInstance() {
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
    public LruCache<InputStreamProvider, File> getLruCache() {
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<InputStreamProvider, ArrayList<CacheListener<File>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public CompressFileCacheFetcher(Context context, int scheduler, int observeOnScheduler) {
        super(context, scheduler, observeOnScheduler);
    }

    public CompressFileCacheFetcher setRequestOptions(RequestOptions requestOptions) {
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

    public static void release() {
        Singleton.release();
    }
}
