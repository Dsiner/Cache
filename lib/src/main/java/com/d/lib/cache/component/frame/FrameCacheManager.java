package com.d.lib.cache.component.frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.d.lib.cache.base.AbstractCacheManager;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class FrameCacheManager extends AbstractCacheManager<FrameCacheManager,
        String, FrameBean> {

    private static class Singleton {
        private volatile static LruCache<String, FrameBean> LRUCACHE = new LruCache<>(12);
        private volatile static HashMap<String, ArrayList<CacheListener<FrameBean>>> HASHMAP = new HashMap<>();

        private static LruCache<String, FrameBean> getLruCache() {
            if (LRUCACHE == null) {
                synchronized (Singleton.class) {
                    if (LRUCACHE == null) {
                        LRUCACHE = new LruCache<>(12);
                    }
                }
            }
            return LRUCACHE;
        }

        private static HashMap<String, ArrayList<CacheListener<FrameBean>>> getHashMap() {
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

    public FrameCacheManager(Context context) {
        super(context);
    }

    @Override
    public LruCache<String, FrameBean> getLruCache() {
        return Singleton.getLruCache();
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<FrameBean>>> getHashMap() {
        return Singleton.getHashMap();
    }

    public static void release() {
        Singleton.release();
    }

    @NonNull
    @Override
    protected String getPreFix() {
        return PreFix.FRAME;
    }

    @NonNull
    protected String getPreFixDuration() {
        return PreFix.DURATION;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void absLoad(Context context, String url, CacheListener<FrameBean> listener) {
        // Also can use ThumbnailUtils.createVideoThumbnail(url, MediaStore.Images.Thumbnails.MINI_KIND);
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
            // Get the first frame picture
            Bitmap bitmap = mmr.getFrameAtTime();
            // Get duration(milliseconds)
            long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            FrameBean frameBean = new FrameBean();
            frameBean.drawable = Util.bitmapToDrawableByBD(bitmap);
            frameBean.duration = duration;
            // Save to disk
            putDisk(url, frameBean);
            success(url, frameBean, listener);
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
    protected FrameBean getDisk(String url) {
        Drawable drawable = aCache.getAsDrawable(getPreFix() + url);
        Long duration = (Long) aCache.getAsObject(getPreFixDuration() + url);
        if (drawable == null || duration == null) {
            return null;
        }
        FrameBean value = new FrameBean();
        value.drawable = drawable;
        value.duration = duration;
        return value;
    }

    @Override
    protected void putDisk(String url, FrameBean value) {
        aCache.put(getPreFix() + url, value.drawable);
        aCache.put(getPreFixDuration() + url, value.duration);
    }
}
