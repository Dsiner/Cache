package com.d.lib.cache.component.duration;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.d.lib.cache.base.AbstractCacheFetcher;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class DurationCacheFetcher extends AbstractCacheFetcher<DurationCacheFetcher,
        String, Long> {

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

    @Override
    public LruCache<String, Long> getLruCache() {
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<Long>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public DurationCacheFetcher(Context context, int scheduler, int observeOnScheduler) {
        super(context, scheduler, observeOnScheduler);
    }

    @Override
    protected String getPreFix() {
        return PreFix.DURATION;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, String url, CacheListener<Long> listener) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && url.contains("://")) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN;"
                        + " MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) "
                        + "Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                mmr.setDataSource(url, headers);
            } else {
                mmr.setDataSource(context, Uri.parse(url));
            }
            // Get duration(milliseconds)
            String strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration = Long.parseLong(strDuration);
            putDisk(url, duration);
            success(url, duration, listener);
        } catch (Throwable e) {
            Log.e("Cache", e.toString());
            e.printStackTrace();
            error(url, e, listener);
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    @Override
    protected Long getDisk(String url) {
        return (Long) aCache.getAsObject(getPreFix() + url);
    }

    @Override
    protected void putDisk(String url, Long value) {
        aCache.put(getPreFix() + url, value);
    }

    public static void release() {
        Singleton.release();
    }
}
