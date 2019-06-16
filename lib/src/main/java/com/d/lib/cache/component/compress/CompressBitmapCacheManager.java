package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class CompressBitmapCacheManager extends AbstractCacheManager<CompressBitmapCacheManager,
        InputStreamProvider, Bitmap> {
    private RequestOptions mRequestOptions;

    private static class Singleton {
        private volatile static LruCache<InputStreamProvider, Bitmap> LRUCACHE = new LruCache<>(12);
        private volatile static HashMap<InputStreamProvider, ArrayList<CacheListener<Bitmap>>> HASHMAP = new HashMap<>();

        private static LruCache<InputStreamProvider, Bitmap> getLruCache() {
            if (LRUCACHE == null) {
                synchronized (Singleton.class) {
                    if (LRUCACHE == null) {
                        LRUCACHE = new LruCache<>(12);
                    }
                }
            }
            return LRUCACHE;
        }

        private static HashMap<InputStreamProvider, ArrayList<CacheListener<Bitmap>>> getHashMap() {
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

    public CompressBitmapCacheManager(Context context) {
        super(context);
    }

    @Override
    public LruCache<InputStreamProvider, Bitmap> getLruCache() {
        return Singleton.getLruCache();
    }

    @Override
    public HashMap<InputStreamProvider, ArrayList<CacheListener<Bitmap>>> getHashMap() {
        return Singleton.getHashMap();
    }

    public static void release() {
        Singleton.release();
    }

    public CompressBitmapCacheManager setRequestOptions(RequestOptions requestOptions) {
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
    protected void absLoad(Context context, final InputStreamProvider url, final CacheListener<Bitmap> listener) {
        Compress helper = new Compress(context, mRequestOptions);
        helper.compress(new CacheListener<File>() {
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
    protected Bitmap getDisk(InputStreamProvider url) {
        Bitmap bitmap = aCache.getAsBitmap(getPreFix() + url);
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    @Override
    protected void putDisk(InputStreamProvider url, Bitmap value) {
        aCache.put(getPreFix() + url, value);
    }
}
