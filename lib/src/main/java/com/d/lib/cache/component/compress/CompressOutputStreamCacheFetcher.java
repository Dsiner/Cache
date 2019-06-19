package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class CompressOutputStreamCacheFetcher extends CompressCacheFetcher<OutputStream> {

    private static class Singleton {
        private volatile static LruCacheMap<String, OutputStream> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, OutputStream> getInstance() {
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
    public LruCache<String, OutputStream> getLruCache() {
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<OutputStream>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public CompressOutputStreamCacheFetcher(Context context,
                                            int scheduler, int observeOnScheduler,
                                            RequestOptions<OutputStream> requestOptions) {
        super(context, scheduler, observeOnScheduler, requestOptions);
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.COMPRESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, final String url, final CacheListener<OutputStream> listener) {
        compress(new CacheListener<File>() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onSuccess(File result) {
                try {
                    FileInputStream fis = new FileInputStream(result);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = fis.read(b)) != -1) {
                        bos.write(b, 0, len);
                    }
                    putDisk(url, bos);
                    success(url, bos, listener);
                } catch (Throwable e) {
                    e.printStackTrace();
                    onError(e);
                }
            }

            @Override
            public void onError(Throwable e) {
                error(url, e, listener);
            }
        });
    }

    public static void release() {
        Singleton.release();
    }
}
