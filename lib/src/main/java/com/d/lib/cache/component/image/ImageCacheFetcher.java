package com.d.lib.cache.component.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.AbstractCacheFetcher;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.component.fetcher.BitmapHunter;
import com.d.lib.cache.component.fetcher.DataFetcher;
import com.d.lib.cache.component.fetcher.HttpStreamFetcher;
import com.d.lib.cache.component.fetcher.Priority;
import com.d.lib.cache.component.fetcher.Request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class ImageCacheFetcher extends AbstractCacheFetcher<ImageCacheFetcher,
        String, Bitmap> {

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

    public ImageCacheFetcher(Context context, int scheduler, int observeOnScheduler) {
        super(context, scheduler, observeOnScheduler);
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.IMAGE;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final String url, final CacheListener<Bitmap> listener) {
        final DataFetcher<InputStream> dataFetcher = new HttpStreamFetcher(url);
        dataFetcher.loadData(Priority.HIGH, new DataFetcher.DataCallback<InputStream>() {
            @Override
            public void onDataReady(@Nullable InputStream data) {
                // Save to disk
                try {
                    Bitmap bitmap = BitmapHunter.decodeStream(data, new Request());
                    dataFetcher.cleanup();
                    putDisk(url, bitmap);
                    success(url, bitmap, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                    onLoadFailed(e);
                }
            }

            @Override
            public void onLoadFailed(@NonNull Exception e) {
                dataFetcher.cleanup();
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
