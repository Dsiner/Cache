package com.d.lib.cache.base;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.d.lib.cache.listener.CacheListener;

import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class DurationCacheManager extends AbstractCacheManager<Long> {
    private static DurationCacheManager manager;

    public static DurationCacheManager getInstance(Context context) {
        if (manager == null) {
            synchronized (DurationCacheManager.class) {
                if (manager == null) {
                    manager = new DurationCacheManager(context);
                }
            }
        }
        return manager;
    }

    private DurationCacheManager(Context context) {
        super(context);
        lruCache.setCount(180);
    }

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
            //Get duration(milliseconds)
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
        return (Long) aCache.getAsObject(PreFix.DURATION + url);
    }

    @Override
    protected void putDisk(String url, Long value) {
        aCache.put(PreFix.DURATION + url, value);
    }
}
