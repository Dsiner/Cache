package com.d.lib.cache.component.duration;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheException;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.RequestOptions;
import com.d.lib.cache.utils.Util;

/**
 * Cache - Get media duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class DurationCache extends AbstractCache<DurationCache, View, String, RequestOptions<Long>> {

    private DurationCache(Context context) {
        super(context);
    }

    private static int getTag() {
        return R.id.lib_cache_tag_duration;
    }

    @UiThread
    public static DurationCache with(Context context) {
        return new DurationCache(context);
    }

    @Override
    public DurationCache load(String url) {
        return super.load(url);
    }

    public Observe apply(RequestOptions<Long> options) {
        this.mRequestOptions = options;
        return new Observe();
    }

    public class Observe extends AbsObserve<Observe, View, Long> {
        Observe() {
        }

        @Override
        public void into(final View view) {
            if (isFinishing() || view == null) {
                return;
            }
            if (TextUtils.isEmpty(mKey)) {
                // Just error
                if (view instanceof IDuration) {
                    ((IDuration) view).setDuration(mRequestOptions.mError != null
                            ? mRequestOptions.mError : mRequestOptions.mPlaceHolder);
                } else if (view instanceof TextView) {
                    long time = mRequestOptions.mError != null
                            ? mRequestOptions.mError
                            : mRequestOptions.mPlaceHolder != null ? mRequestOptions.mPlaceHolder : 0;
                    ((TextView) view).setText(Util.formatTime(time));
                }
                return;
            }
            setTarget(view);
            Object tag = view.getTag(getTag());
            if (tag != null && tag instanceof String
                    && TextUtils.equals((String) tag, mKey)) {
                // Not refresh
                return;
            }
            view.setTag(getTag(), mKey);
            new DurationCacheManager(getContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .load(getContext(), mKey, new CacheListener<Long>() {
                        @Override
                        public void onLoading() {
                            if (isFinished()) {
                                return;
                            }
                            if (mRequestOptions.mPlaceHolder == null) {
                                return;
                            }
                            setTarget(mRequestOptions.mPlaceHolder);
                        }

                        @Override
                        public void onSuccess(Long result) {
                            if (isFinished()) {
                                return;
                            }
                            setTarget(result);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (isFinished()) {
                                return;
                            }
                            if (mRequestOptions.mError == null) {
                                return;
                            }
                            setTarget(mRequestOptions.mError);
                        }

                        private void setTarget(Long value) {
                            if (getTarget() instanceof IDuration) {
                                ((IDuration) getTarget()).setDuration(value);
                            } else if (getTarget() instanceof TextView) {
                                long time = value != null ? value : 0;
                                ((TextView) getTarget()).setText(Util.formatTime(time));
                            }
                        }

                        private boolean isFinished() {
                            if (isFinishing() || getTarget() == null) {
                                return true;
                            }
                            Object tag = getTarget().getTag(getTag());
                            return tag == null || !(tag instanceof String)
                                    || !TextUtils.equals((String) tag, mKey);
                        }
                    });
        }

        @Override
        public void listener(CacheListener<Long> l) {
            if (isFinishing()) {
                return;
            }
            if (TextUtils.isEmpty(mKey)) {
                // Just error
                if (l != null) {
                    l.onError(new CacheException("Url must not be empty!"));
                }
                return;
            }
            new DurationCacheManager(getContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .load(getContext(), mKey, l);
        }
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void clear(View view) {
        if (view == null) {
            return;
        }
        view.setTag(getTag(), "");
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void release(Context context) {
        if (context == null) {
            return;
        }
        DurationCacheManager.release();
    }
}
