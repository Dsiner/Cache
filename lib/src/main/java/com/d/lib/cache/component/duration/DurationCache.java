package com.d.lib.cache.component.duration;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbsObserve;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.RequestOptions;
import com.d.lib.cache.utils.Util;

/**
 * Cache - Get media duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class DurationCache extends AbstractCache<DurationCache,
        DurationCache.Observe, String> {

    private static int TAG_ID = R.id.lib_cache_tag_duration;

    private DurationCache(Context context) {
        super(context);
    }

    @UiThread
    public static DurationCache with(Context context) {
        return new DurationCache(context);
    }

    @Override
    public DurationCache.Observe load(String url) {
        mUri = url;
        return new Observe();
    }

    public class Observe extends AbsObserve<Observe,
            View, Long, RequestOptions<Long>> {

        @Override
        protected int TAG() {
            return TAG_ID;
        }

        Observe() {
            mRequestOptions = new RequestOptions<>();
        }

        @Override
        public void into(final View view) {
            if (isFinishing() || view == null) {
                return;
            }
            setTarget(view);
            if (!attached(mUri)) {
                return;
            }
            new DurationCacheManager(getContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .load(getContext(), mUri, new CacheListener<Long>() {
                        @Override
                        public void onLoading() {
                            if (isFinishing() || isDetached(mUri)) {
                                return;
                            }
                            setTarget(mRequestOptions.placeHolder);
                        }

                        @Override
                        public void onSuccess(Long result) {
                            if (isFinishing() || isDetached(mUri)) {
                                return;
                            }
                            setTarget(result);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (isFinishing() || isDetached(mUri)) {
                                return;
                            }
                            setTarget(mRequestOptions.error);
                        }

                        private void setTarget(Long value) {
                            if (value == null) {
                                return;
                            }
                            if (getTarget() instanceof IDuration) {
                                ((IDuration) getTarget()).setDuration(value);
                            } else if (getTarget() instanceof TextView) {
                                ((TextView) getTarget()).setText(Util.formatTime(value));
                            }
                        }
                    });
        }

        @Override
        public void listener(CacheListener<Long> l) {
            if (isFinishing()) {
                return;
            }
            new DurationCacheManager(getContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .load(getContext(), mUri, l);
        }
    }

    @SuppressWarnings("unused")
    @UiThread
    public static void clear(View view) {
        if (view == null) {
            return;
        }
        view.setTag(TAG_ID, "");
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
