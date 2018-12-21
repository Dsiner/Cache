package com.d.lib.cache.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.AbstractCacheManager;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.component.compress.Compress;
import com.d.lib.cache.component.compress.InputStreamProvider;
import com.d.lib.cache.component.compress.RequestOptions;
import com.d.lib.cache.listener.CacheListener;

import java.io.File;

/**
 * Created by D on 2017/10/18.
 */
public class CompressBitmapCacheManager extends AbstractCacheManager<InputStreamProvider, Bitmap> {
    private volatile static CompressBitmapCacheManager instance;

    private RequestOptions mRequestOptions;

    public static CompressBitmapCacheManager getIns(Context context) {
        if (instance == null) {
            synchronized (CompressBitmapCacheManager.class) {
                if (instance == null) {
                    instance = new CompressBitmapCacheManager(context);
                }
            }
        }
        return instance;
    }

    private CompressBitmapCacheManager(Context context) {
        super(context);
        mLruCache.setCount(12);
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
