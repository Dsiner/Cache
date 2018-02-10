package com.d.lib.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.d.lib.cache.base.FrameCacheManager;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.listener.FrameView;

/**
 * Cache -获取视频第一帧&时长
 * Created by D on 2017/10/19.
 */
public class FrameCache {
    private Context context;
    private String url;
    private Drawable placeholder;

    public static FrameCache with(Context context) {
        FrameCache cache = new FrameCache();
        cache.context = context;
        return cache;
    }

    public FrameCache load(String url) {
        this.url = url;
        return this;
    }

    public FrameCache placeholder(Drawable drawable) {
        this.placeholder = drawable;
        return this;
    }

    public void into(final View view) {
        if (view == null) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            //just error
            if (view instanceof FrameView) {
                ((FrameView) view).setFrame(placeholder, 0L);
            }
            return;
        }
        Object tag = view.getTag(R.id.lib_cache_tag_frame);
        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
            //not refresh
            return;
        }
        view.setTag(R.id.lib_cache_tag_frame, url);
        FrameCacheManager.getInstance(context).load(context, url, new CacheListener<FrameBean>() {
            @Override
            public void onLoading() {
                Object tag = view.getTag(R.id.lib_cache_tag_frame);
                if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                    if (view instanceof FrameView) {
                        ((FrameView) view).setFrame(placeholder, 0L);
                    }
                }
            }

            @Override
            public void onSuccess(FrameBean result) {
                Object tag = view.getTag(R.id.lib_cache_tag_frame);
                if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                    if (view instanceof FrameView) {
                        ((FrameView) view).setFrame(result.drawable, result.duration);
                    }
                }
            }

            @Override
            public void onError() {
                Object tag = view.getTag(R.id.lib_cache_tag_frame);
                if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                    if (view instanceof FrameView) {
                        ((FrameView) view).setFrame(placeholder, 0L);
                    }
                }
            }
        });
    }

    public void listener(CacheListener<FrameBean> l) {
        if (TextUtils.isEmpty(url)) {
            //just error
            if (l != null) {
                l.onError();
            }
            return;
        }
        FrameCacheManager.getInstance(context).load(context, url, l);
    }

    public static class FrameBean {
        public Drawable drawable;
        public Long duration;
    }
}
