package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.DiskCacheStrategies;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.util.threadpool.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by D on 2017/10/18.
 */
public class CompressOutputStreamCacheFetcher extends CompressCacheFetcher<ByteArrayOutputStream> {

    public CompressOutputStreamCacheFetcher(@NonNull Context context,
                                            @NonNull CompressOptions requestOptions,
                                            @Schedulers.Scheduler int scheduler,
                                            @Schedulers.Scheduler int observeOnScheduler) {
        super(context, requestOptions, scheduler, observeOnScheduler);
    }

    public static void release() {
        Singleton.release();
    }

    @Override
    public LruCache<String, ByteArrayOutputStream> getLruCache() {
        return Singleton.getInstance().mLruCache;
    }

    @Override
    public HashMap<String, List<CacheListener<ByteArrayOutputStream>>> getHashMap() {
        return Singleton.getInstance().mHashMap;
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.COMPRESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final String url, final CacheListener<ByteArrayOutputStream> listener) {
        try {
            ByteArrayOutputStream result = needCompress() ? compress() : convert(mCompressOptions.provider.open());
            putDisk(url, result);
            success(url, result, listener);
        } catch (Throwable e) {
            e.printStackTrace();
            error(url, e, listener);
        }
    }

    @Override
    protected ByteArrayOutputStream getDisk(String url) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return null;
        }
        byte[] value = A_CACHE.getAsBinary(getPreFix() + url);
        if (value == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(value.length);
            outputStream.write(value);
            return outputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void putDisk(String url, ByteArrayOutputStream value) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return;
        }
        A_CACHE.put(getPreFix() + url, value.toByteArray());
    }

    private static class Singleton {
        private volatile static LruCacheMap<String, ByteArrayOutputStream> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, ByteArrayOutputStream> getInstance() {
            if (CACHE == null) {
                synchronized (Singleton.class) {
                    if (CACHE == null) {
                        CACHE = new LruCacheMap<>(0);
                    }
                }
            }
            return CACHE;
        }

        private static void release() {
            CACHE = null;
        }
    }
}
