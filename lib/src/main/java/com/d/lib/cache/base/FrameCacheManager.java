package com.d.lib.cache.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import com.d.lib.cache.FrameCache;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.util.CacheUtil;

import java.util.HashMap;

/**
 * Created by D on 2017/10/18.
 */
public class FrameCacheManager extends AbstractCacheManager<FrameCache.FrameBean> {
    private static FrameCacheManager manager;

    public static FrameCacheManager getInstance(Context context) {
        if (manager == null) {
            synchronized (FrameCacheManager.class) {
                if (manager == null) {
                    manager = new FrameCacheManager(context);
                }
            }
        }
        return manager;
    }

    private FrameCacheManager(Context context) {
        super(context);
        lruCache.setCount(12);
    }

    @Override
    protected void absLoad(Context context, String url, CacheListener<FrameCache.FrameBean> listener) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && url.contains("://")) {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN;"
                        + " MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) "
                        + "Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
            } else {
                mmr.setDataSource(context, Uri.parse(url));
            }
            final Bitmap bitmap = mmr.getFrameAtTime();//获取第一帧图片
            long duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//时长(毫秒
            FrameCache.FrameBean frameBean = new FrameCache.FrameBean();
            frameBean.drawable = CacheUtil.bitmapToDrawableByBD(bitmap);
            frameBean.duration = duration;
            putDisk(url, frameBean);//save to disk
            success(url, frameBean, listener);
        } catch (Exception e) {
            e.printStackTrace();
            error(url, listener);
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }

    @Override
    protected FrameCache.FrameBean getDisk(String url) {
        Drawable drawable = aCache.getAsDrawable(url);
        Long duration = (Long) aCache.getAsObject(PreFix.DURATION + url);
        if (drawable == null || duration == null) {
            return null;
        }
        FrameCache.FrameBean value = new FrameCache.FrameBean();
        value.drawable = drawable;
        value.duration = duration;
        return value;
    }

    @Override
    protected void putDisk(String url, FrameCache.FrameBean value) {
        aCache.put(url, value.drawable);//save to disk
        aCache.put(PreFix.DURATION + url, value.duration);//save to disk
    }
}
