package com.d.lib.cache.component.duration;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbsObserve;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.RequestOptions;

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
        public Observe apply(@NonNull RequestOptions<Long> options) {
            mRequestOptions = options;
            return this;
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
            new DurationCacheFetcher(getContext(), mRequestOptions, mScheduler, mObserveOnScheduler)
                    .load(getContext().getApplicationContext(), mUri, new CacheListener<Long>() {
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
                                ((TextView) getTarget()).setText(formatTime(value));
                            }
                        }
                    });
        }

        @Override
        public void listener(CacheListener<Long> l) {
            if (isFinishing()) {
                return;
            }
            new DurationCacheFetcher(getContext(), mRequestOptions, mScheduler, mObserveOnScheduler)
                    .load(getContext().getApplicationContext(), mUri, l);
        }
    }

    /**
     * Format time, convert milliseconds into seconds: (00:00) format
     * String.format("%02d:%02d", time / 1000 / 60, time / 1000 % 60)
     */
    public static String formatTime(long time) {
        StringBuilder sb;
        long min = time / 1000 / 60;
        long sec = time / 1000 % 60;
        if (min / 10 < 1) {
            sb = new StringBuilder("0");
            sb.append(String.valueOf(min));
        } else {
            sb = new StringBuilder(String.valueOf(min));
        }
        sb.append(":");
        if (sec / 10 < 1) {
            sb.append("0");
        }
        sb.append(String.valueOf(sec));
        return sb.toString();
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
        DurationCacheFetcher.release();
    }
}
