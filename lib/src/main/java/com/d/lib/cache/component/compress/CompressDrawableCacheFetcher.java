package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<Drawable>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public CompressDrawableCacheFetcher(Context context,
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
    protected void absLoad(Context context, final String url, final CacheListener<Drawable> listener) {
        compress(new CacheListener<File>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(File result) {
                Bitmap bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
                Drawable drawable = Util.bitmapToDrawableByBD(bitmap);
                bitmap.recycle();
                putDisk(url, drawable);
                success(url, drawable, listener);
            }

            @Override
            public void onError(Throwable e) {
                error(url, e, listener);
            }
        });
    }

    @Override
    protected Drawable getDisk(String url) {
        Drawable drawable = aCache.getAsDrawable(getPreFix() + url);
        if (drawable == null) {
            return null;
        }
        return drawable;
    }

    @Override
    protected void putDisk(String url, Drawable value) {
        aCache.put(getPreFix() + url, value);
    }

    public static void release() {
        Singleton.release();
    }
}
