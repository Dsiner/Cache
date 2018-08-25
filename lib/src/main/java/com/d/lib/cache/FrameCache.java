package com.d.lib.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.FrameCacheManager;
import com.d.lib.cache.bean.FrameBean;
import com.d.lib.cache.exception.CacheException;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.listener.FrameView;

import java.lang.ref.WeakReference;

/**
 * Cache - Get video first frame & duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class FrameCache extends AbstractCache<FrameCache, View, String, Drawable, FrameBean> {

    private FrameCache(Context context) {
        super(context);
    }

    @UiThread
    public static FrameCache with(Context context) {
        return new FrameCache(context);
    }

    public FrameCache placeholder(@DrawableRes int resId) {
        if (isFinish()) {
            return this;
        }
        return placeholder(ContextCompat.getDrawable(getContext(), resId));
    }

    public FrameCache error(@DrawableRes int resId) {
        if (isFinish()) {
            return this;
        }
        return error(ContextCompat.getDrawable(getContext(), resId));
    }

    @Override
    public void into(View view) {
        if (isFinish() || view == null) {
            return;
        }
        target = new WeakReference<>(view);
        if (TextUtils.isEmpty(url)) {
            // Just error
            if (view instanceof FrameView) {
                ((FrameView) view).setFrame(error != null ? error : placeHolder, 0L);
            } else if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(error != null ? error : placeHolder);
            }
            return;
        }
        Object tag = view.getTag(R.id.lib_cache_tag_frame);
        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
            // Not refresh
            return;
        }
        view.setTag(R.id.lib_cache_tag_frame, url);
        FrameCacheManager.getInstance(getContext().getApplicationContext())
                .load(getContext().getApplicationContext(), url, new CacheListener<FrameBean>() {
                    @Override
                    public void onLoading() {
                        if (isFinish() || getTarget() == null) {
                            return;
                        }
                        Object tag = getTarget().getTag(R.id.lib_cache_tag_frame);
                        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                            if (getTarget() instanceof FrameView) {
                                ((FrameView) getTarget()).setFrame(placeHolder, 0L);
                            } else if (getTarget() instanceof ImageView) {
                                ((ImageView) getTarget()).setImageDrawable(placeHolder);
                            }
                        }
                    }

                    @Override
                    public void onSuccess(FrameBean result) {
                        if (isFinish() || getTarget() == null) {
                            return;
                        }
                        Object tag = getTarget().getTag(R.id.lib_cache_tag_frame);
                        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                            if (getTarget() instanceof FrameView) {
                                ((FrameView) getTarget()).setFrame(result.drawable, result.duration);
                            } else if (getTarget() instanceof ImageView) {
                                ((ImageView) getTarget()).setImageDrawable(result.drawable);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isFinish() || getTarget() == null) {
                            return;
                        }
                        Object tag = getTarget().getTag(R.id.lib_cache_tag_frame);
                        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                            if (getTarget() instanceof FrameView) {
                                ((FrameView) getTarget()).setFrame(error != null ? error : placeHolder, 0L);
                            } else if (getTarget() instanceof ImageView) {
                                ((ImageView) getTarget()).setImageDrawable(error != null ? error : placeHolder);
                            }
                        }
                    }
                });
    }

    @Override
    public void listener(CacheListener<FrameBean> l) {
        if (isFinish()) {
            return;
        }
        if (TextUtils.isEmpty(url)) {
            // Just error
            if (l != null) {
                l.onError(new CacheException("Url must not be empty!"));
            }
            return;
        }
        FrameCacheManager.getInstance(getContext().getApplicationContext())
                .load(getContext().getApplicationContext(), url, l);
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void clear(View view) {
        if (view == null) {
            return;
        }
        view.setTag(R.id.lib_cache_tag_frame, "");
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void release(Context context) {
        if (context == null) {
            return;
        }
        FrameCacheManager.getInstance(context.getApplicationContext()).release();
    }
}
