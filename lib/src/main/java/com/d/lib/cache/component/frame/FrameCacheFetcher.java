package com.d.lib.cache.component.frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
public class FrameCacheFetcher extends AbstractCacheFetcher<FrameCacheFetcher,
        String, FrameBean> {

    public FrameCacheFetcher(@NonNull Context context,
                             @NonNull RequestOptions requestOptions,
                             @Schedulers.Scheduler int scheduler,
                             @Schedulers.Scheduler int observeOnScheduler) {
        super(context, requestOptions, scheduler, observeOnScheduler);
    }

    public static void release() {
        Singleton.release();
    }

    /**
     * This method finds a representative frame at any time position if possible,
     * and returns it as a bitmap. This is useful for generating a thumbnail
     * for an input data source.
     */
    public static Bitmap getFrameAtTime(Context context, String uri) {
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
            // Get the first frame picture
            return mmr.getFrameAtTime();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    public static Object[] getThumbnailInfo(Context context, String uri) {
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
            // Get the first frame picture
            Bitmap bitmap = mmr.getFrameAtTime();

            // Get duration(milliseconds)
            long duration;
            try {
                duration = Long.parseLong(mmr.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION));
            } catch (Throwable e) {
                e.printStackTrace();
                duration = -1;
            }
            return new Object[]{bitmap, duration};
        } catch (Throwable e) {
            e.printStackTrace();
            return new Object[]{null, -1};
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    @Override
    public LruCache<String, FrameBean> getLruCache() {
        return Singleton.getInstance().mLruCache;
    }

    @Override
    public HashMap<String, List<CacheListener<FrameBean>>> getHashMap() {
        return Singleton.getInstance().mHashMap;
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
        try {
            final Object[] thumbnailInfo = getThumbnailInfo(context, url);
            final Bitmap bitmap = (Bitmap) thumbnailInfo[0];
            final long duration = (long) thumbnailInfo[1];
            FrameBean frameBean = new FrameBean(bitmap != null ? new BitmapDrawable(bitmap) : null,
                    duration, null);
            // Save to disk
            putDisk(url, frameBean);
            success(url, frameBean, listener);
        } catch (Throwable e) {
            Log.e("Cache", e.toString());
            e.printStackTrace();
            error(url, e, listener);
        }
    }

    @Override
    protected FrameBean getDisk(String url) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return null;
        }
        Drawable drawable = A_CACHE.getAsDrawable(getPreFix() + url);
        Long duration = (Long) A_CACHE.getAsObject(getPreFixDuration() + url);
        if (drawable == null || duration == null) {
            return null;
        }
        return new FrameBean(drawable, duration, null);
    }

    @Override
    protected void putDisk(String url, FrameBean value) {
        if (mRequestOptions.diskCacheStrategy == DiskCacheStrategies.NONE) {
            return;
        }
        A_CACHE.put(getPreFix() + url, value.drawable);
        A_CACHE.put(getPreFixDuration() + url, value.duration);
    }

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
}
