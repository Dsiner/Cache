package com.d.lib.cache.manager;

import android.content.Context;
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
public class CompressFileCacheManager extends AbstractCacheManager<InputStreamProvider, File> {
    private volatile static CompressFileCacheManager instance;

    private RequestOptions mRequestOptions;

    public static CompressFileCacheManager getIns(Context context) {
        if (instance == null) {
            synchronized (CompressFileCacheManager.class) {
                if (instance == null) {
                    instance = new CompressFileCacheManager(context);
                }
            }
        }
        return instance;
    }

    private CompressFileCacheManager(Context context) {
        super(context);
        mLruCache.setCount(12);
    }

    public CompressFileCacheManager setRequestOptions(RequestOptions requestOptions) {
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
    protected void absLoad(Context context, final InputStreamProvider url, final CacheListener<File> listener) {
        Compress helper = new Compress(context, mRequestOptions);
        helper.compress(new CacheListener<File>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(File result) {
                putDisk(url, result);
                success(url, result, listener);
            }

            @Override
            public void onError(Throwable e) {
                error(url, e, listener);
            }
        });
    }

    @Override
    protected File getDisk(InputStreamProvider url) {
        return null;
    }

    @Override
    protected void putDisk(InputStreamProvider url, File value) {

    }
}
