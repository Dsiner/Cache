package com.d.lib.cache.component.duration;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.d.lib.cache.base.AbstractCacheManager;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.PreFix;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class DurationCacheManager extends AbstractCacheManager<DurationCacheManager,
        String, Long> {

    private static class Singleton {
        private volatile static LruCache<String, Long> LRUCACHE = new LruCache<>(180);
        private volatile static HashMap<String, ArrayList<CacheListener<Long>>> HASHMAP = new HashMap<>();

        private static LruCache<String, Long> getLruCache() {
            if (LRUCACHE == null) {
                synchronized (Singleton.class) {
                    if (LRUCACHE == null) {
                        LRUCACHE = new LruCache<>(180);
                    }
                }
            }
            return LRUCACHE;
        }

        private static HashMap<String, ArrayList<CacheListener<Long>>> getHashMap() {
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

    public DurationCacheManager(Context context) {
        super(context);
    }

    @Override
    public LruCache<String, Long> getLruCache() {
        return Singleton.getLruCache();
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<Long>>> getHashMap() {
        return Singleton.getHashMap();
    }

    public static void release() {
        Singleton.release();
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
}
