package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.DiskCacheStrategies;
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
public class CompressDrawableCacheFetcher extends CompressCacheFetcher<Drawable> {

    private static class Singleton {
        private volatile static LruCacheMap<String, Drawable> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, Drawable> getInstance() {
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
    public LruCache<String, Drawable> getLruCache() {
        return Singleton.getInstance().mLruCache;
    }

    @Override
    public HashMap<String, List<CacheListener<Drawable>>> getHashMap() {
        return Singleton.getInstance().mHashMap;
    }

    public CompressDrawableCacheFetcher(@NonNull Context context,
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
    protected void absLoad(Context context, final String url, final CacheListener<Drawable> listener) {
        try {
            File result = needCompress() ? compressFile() : new File(mCompressOptions.provider.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
            Drawable drawable = bitmap != null ? new BitmapDrawable(bitmap) : null;
            putDisk(url, drawable);
            success(url, drawable, listener);
        } catch (Throwable e) {
            e.printStackTrace();
            error(url, e, listener);
        }
    }

    @Override
    protected Drawable getDisk(String url) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return null;
        }
        Drawable drawable = A_CACHE.getAsDrawable(getPreFix() + url);
        if (drawable == null) {
            return null;
        }
        return drawable;
    }

    @Override
    protected void putDisk(String url, Drawable value) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return;
        }
        A_CACHE.put(getPreFix() + url, value);
    }

    public static void release() {
        Singleton.release();
    }
}
