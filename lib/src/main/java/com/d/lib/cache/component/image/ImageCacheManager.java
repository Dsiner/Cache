package com.d.lib.cache.component.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.AbstractCacheManager;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
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
public class ImageCacheManager extends AbstractCacheManager<ImageCacheManager,
        String, Bitmap> {

    private static class Singleton {
        private volatile static LruCache<String, Bitmap> LRUCACHE = new LruCache<>(12);
        private volatile static HashMap<String, ArrayList<CacheListener<Bitmap>>> HASHMAP = new HashMap<>();

        private static LruCache<String, Bitmap> getLruCache() {
            if (LRUCACHE == null) {
                synchronized (Singleton.class) {
                    if (LRUCACHE == null) {
                        LRUCACHE = new LruCache<>(12);
                    }
                }
            }
            return LRUCACHE;
        }

        private static HashMap<String, ArrayList<CacheListener<Bitmap>>> getHashMap() {
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

    public ImageCacheManager(Context context) {
        super(context);
    }

    @Override
    public LruCache<String, Bitmap> getLruCache() {
        return Singleton.getLruCache();
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<Bitmap>>> getHashMap() {
        return Singleton.getHashMap();
    }

    public static void release() {
        Singleton.release();
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
}
