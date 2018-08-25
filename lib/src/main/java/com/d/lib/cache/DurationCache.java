package com.d.lib.cache;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.DurationCacheManager;
import com.d.lib.cache.exception.CacheException;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.listener.DurationView;
import com.d.lib.cache.utils.Util;

import java.lang.ref.WeakReference;

/**
 * Cache - Get media duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class DurationCache extends AbstractCache<DurationCache, View, String, Long, Long> {

    private DurationCache(Context context) {
        super(context);
    }

    @UiThread
    public static DurationCache with(Context context) {
        return new DurationCache(context);
    }

    @Override
    public void into(final View view) {
        if (isFinish() || view == null) {
            return;
        }
        target = new WeakReference<>(view);
        if (TextUtils.isEmpty(url)) {
            // Just error
            if (view instanceof DurationView) {
                ((DurationView) view).setDuration(error != null ? error : placeHolder);
            } else if (view instanceof TextView) {
                ((TextView) view).setText(error != null ? error.toString()
                        : placeHolder != null ? placeHolder.toString() : "");
            }
            return;
        }
        Object tag = view.getTag(R.id.lib_cache_tag_duration);
        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
            // Not refresh
            return;
        }
        view.setTag(R.id.lib_cache_tag_duration, url);
        DurationCacheManager.getInstance(getContext().getApplicationContext())
                .load(getContext().getApplicationContext(), url, new CacheListener<Long>() {
                    @Override
                    public void onLoading() {
                        if (isFinish() || getTarget() == null) {
                            return;
                        }
                        Object tag = getTarget().getTag(R.id.lib_cache_tag_duration);
                        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                            if (placeHolder == null) {
                                return;
                            }
                            if (getTarget() instanceof DurationView) {
                                ((DurationView) getTarget()).setDuration(placeHolder);
                            } else if (getTarget() instanceof TextView) {
                                ((TextView) getTarget()).setText(Util.formatTime(placeHolder));
                            }
                        }
                    }

                    @Override
                    public void onSuccess(Long result) {
                        Object tag = view.getTag(R.id.lib_cache_tag_duration);
                        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                            if (view instanceof DurationView) {
                                ((DurationView) view).setDuration(result);
                            } else if (getTarget() instanceof TextView) {
                                long time = result != null ? result : 0;
                                ((TextView) getTarget()).setText(Util.formatTime(time));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Object tag = view.getTag(R.id.lib_cache_tag_duration);
                        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, url)) {
                            if (error == null) {
                                return;
                            }
                            if (view instanceof DurationView) {
                                ((DurationView) view).setDuration(error);
                            } else if (getTarget() instanceof TextView) {
                                ((TextView) getTarget()).setText(Util.formatTime(error));
                            }
                        }
                    }
                });
    }

    @Override
    public void listener(CacheListener<Long> l) {
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
        DurationCacheManager.getInstance(getContext().getApplicationContext())
                .load(getContext().getApplicationContext(), url, l);
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void clear(View view) {
        if (view == null) {
            return;
        }
        view.setTag(R.id.lib_cache_tag_duration, "");
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void release(Context context) {
        if (context == null) {
            return;
        }
        DurationCacheManager.getInstance(context.getApplicationContext()).release();
    }
}
