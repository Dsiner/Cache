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

import com.d.lib.cache.base.AbstractCacheFetcher;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.LruCache;
import com.d.lib.cache.base.LruCacheMap;
import com.d.lib.cache.base.PreFix;
import com.d.lib.cache.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class FrameCacheFetcher extends AbstractCacheFetcher<FrameCacheFetcher,
        String, FrameBean> {

    private static class Singleton {
        private volatile static LruCacheMap<String, FrameBean> CACHE = new LruCacheMap<>(12);

        private static LruCacheMap<String, FrameBean> getInstance() {
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
    public LruCache<String, FrameBean> getLruCache() {
        return Singleton.getInstance().lruCache;
    }

    @Override
    public HashMap<String, ArrayList<CacheListener<FrameBean>>> getHashMap() {
        return Singleton.getInstance().hashMap;
    }

    public FrameCacheFetcher(Context context, int scheduler, int observeOnScheduler) {
        super(context, scheduler, observeOnScheduler);
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

    public static void release() {
        Singleton.release();
    }
}
