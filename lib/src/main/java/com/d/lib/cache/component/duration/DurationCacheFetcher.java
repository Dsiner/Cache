package com.d.lib.cache.component.duration;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.d.lib.cache.base.AbstractCacheFetcher;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.DiskCacheStrategies;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.base.RequestOptions;
import com.d.lib.cache.util.threadpool.Schedulers;

import java.util.HashMap;
import java.util.List;

/**
 * Created by D on 2017/10/18.
 */
public class DurationCacheFetcher extends AbstractCacheFetcher<DurationCacheFetcher,
        String, Long> {

    public DurationCacheFetcher(@NonNull Context context,
                                @NonNull RequestOptions requestOptions,
                                @Schedulers.Scheduler int scheduler,
                                @Schedulers.Scheduler int observeOnScheduler) {
        super(context, requestOptions, scheduler, observeOnScheduler);
    }

    public static void release() {
        Singleton.release();
    }

    public static long getThumbnailDuration(Context context, String uri) {
        // Also can use ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                    && uri.contains("://")) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN;"
                        + " MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) "
                        + "Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                mmr.setDataSource(uri, headers);
            } else {
                mmr.setDataSource(context, Uri.parse(uri));
            }
            // Get duration(milliseconds)
            long duration;
            try {
                duration = Long.parseLong(mmr.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION));
            } catch (Throwable e) {
                e.printStackTrace();
                duration = -1;
            }
            return duration;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    @Override
    public LruCache<String, Long> getLruCache() {
        return Singleton.getInstance().mLruCache;
    }

    @Override
    public HashMap<String, List<CacheListener<Long>>> getHashMap() {
        return Singleton.getInstance().mHashMap;
    }

    @Override
    protected String getPreFix() {
        return PreFix.DURATION;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, String url, CacheListener<Long> listener) {
        try {
            final long duration = getThumbnailDuration(context, url);
            putDisk(url, duration);
            success(url, duration, listener);
        } catch (Throwable e) {
            Log.e("Cache", e.toString());
            e.printStackTrace();
            error(url, e, listener);
        }
    }

    @Override
    protected Long getDisk(String url) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return null;
        }
        return (Long) A_CACHE.getAsObject(getPreFix() + url);
    }

    @Override
    protected void putDisk(String url, Long value) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return;
        }
        A_CACHE.put(getPreFix() + url, value);
    }

    private static class Singleton {
        private volatile static LruCacheMap<String, Long> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, Long> getInstance() {
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
}
