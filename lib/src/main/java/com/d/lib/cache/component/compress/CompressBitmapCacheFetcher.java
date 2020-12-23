package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.DiskCacheStrategies;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.util.threadpool.Schedulers;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by D on 2017/10/18.
 */
public class CompressBitmapCacheFetcher extends CompressCacheFetcher<Bitmap> {

    public CompressBitmapCacheFetcher(@NonNull Context context,
                                      @NonNull CompressOptions requestOptions,
                                      @Schedulers.Scheduler int scheduler,
                                      @Schedulers.Scheduler int observeOnScheduler) {
        super(context, requestOptions, scheduler, observeOnScheduler);
    }

    public static void release() {
        Singleton.release();
    }

    @Override
    public LruCache<String, Bitmap> getLruCache() {
        return Singleton.getInstance().mLruCache;
    }

    @Override
    public HashMap<String, List<CacheListener<Bitmap>>> getHashMap() {
        return Singleton.getInstance().mHashMap;
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.COMPRESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final String url, final CacheListener<Bitmap> listener) {
        try {
            File result = needCompress() ? compressFile() : new File(mCompressOptions.provider.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
            putDisk(url, bitmap);
            success(url, bitmap, listener);
        } catch (Throwable e) {
            e.printStackTrace();
            error(url, e, listener);
        }
    }

    @Override
    protected Bitmap getDisk(String url) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return null;
        }
        Bitmap bitmap = A_CACHE.getAsBitmap(getPreFix() + url);
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    @Override
    protected void putDisk(String url, Bitmap value) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return;
        }
        A_CACHE.put(getPreFix() + url, value);
    }

    private static class Singleton {
        private volatile static LruCacheMap<String, Bitmap> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, Bitmap> getInstance() {
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
}
