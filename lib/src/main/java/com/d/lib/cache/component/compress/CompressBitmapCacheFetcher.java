package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class CompressBitmapCacheFetcher extends CompressCacheFetcher<Bitmap> {

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

    @Override
    public LruCache<String, Bitmap> getLruCache() {
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<Bitmap>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public CompressBitmapCacheFetcher(Context context,
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
    protected void absLoad(Context context, final String url, final CacheListener<Bitmap> listener) {
        compress(new CacheListener<File>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(File result) {
                Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
                putDisk(url, bitmap);
                success(url, bitmap, listener);
            }

            @Override
            public void onError(Throwable e) {
                error(url, e, listener);
            }
        });
    }

    @Override
    protected Bitmap getDisk(String url) {
        Bitmap bitmap = aCache.getAsBitmap(getPreFix() + url);
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    @Override
    protected void putDisk(String url, Bitmap value) {
        aCache.put(getPreFix() + url, value);
    }

    public static void release() {
        Singleton.release();
    }
}
