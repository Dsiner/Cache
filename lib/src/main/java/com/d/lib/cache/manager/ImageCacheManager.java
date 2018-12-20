package com.d.lib.cache.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.AbstractCacheManager;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.component.fetcher.BitmapHunter;
import com.d.lib.cache.component.fetcher.DataFetcher;
import com.d.lib.cache.component.fetcher.HttpStreamFetcher;
import com.d.lib.cache.component.fetcher.Priority;
import com.d.lib.cache.component.fetcher.Request;
import com.d.lib.cache.listener.CacheListener;

import java.io.InputStream;

/**
 * Created by D on 2017/10/18.
 */
public class ImageCacheManager extends AbstractCacheManager<String, Bitmap> {
    private volatile static ImageCacheManager instance;

    public static ImageCacheManager getIns(Context context) {
        if (instance == null) {
            synchronized (ImageCacheManager.class) {
                if (instance == null) {
                    instance = new ImageCacheManager(context);
                }
            }
        }
        return instance;
    }

    private ImageCacheManager(Context context) {
        super(context);
        mLruCache.setCount(12);
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
