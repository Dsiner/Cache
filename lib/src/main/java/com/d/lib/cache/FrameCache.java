package com.d.lib.cache;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheException;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.RequestOptions;
import com.d.lib.cache.component.frame.FrameBean;
import com.d.lib.cache.component.frame.FrameCacheManager;
import com.d.lib.cache.component.frame.IFrame;

/**
 * Cache - Get video first frame & duration
 * Created by D on 2017/10/19.
 */
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
public class FrameCache extends AbstractCache<FrameCache, View, String, RequestOptions<Drawable>> {

    private FrameCache(Context context) {
        super(context);
    }

    private static int getTag() {
        return R.id.lib_cache_tag_frame;
    }

    @UiThread
    public static FrameCache with(Context context) {
        return new FrameCache(context);
    }

    @Override
    public FrameCache load(String url) {
        return super.load(url);
    }

    public Observe apply(RequestOptions<Drawable> options) {
        this.mRequestOptions = options;
        return new Observe();
    }

    public class Observe extends AbsObserve<Observe, View, FrameBean> {
        Observe() {
        }

        @Override
        public void into(View view) {
            if (isFinishing() || view == null) {
                return;
            }
            if (TextUtils.isEmpty(mKey)) {
                // Just error
                if (view instanceof IFrame) {
                    ((IFrame) view).setFrame(mRequestOptions.mError != null
                            ? mRequestOptions.mError : mRequestOptions.mPlaceHolder, 0L);
                } else if (view instanceof ImageView) {
                    ((ImageView) view).setImageDrawable(mRequestOptions.mError != null
                            ? mRequestOptions.mError : mRequestOptions.mPlaceHolder);
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
            new FrameCacheManager(getContext().getApplicationContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .load(getContext().getApplicationContext(), mKey, new CacheListener<FrameBean>() {
                        @Override
                        public void onLoading() {
                            if (isFinished()) {
                                return;
                            }
                            if (mRequestOptions.mPlaceHolder == null) {
                                return;
                            }
                            setTarget(mRequestOptions.mPlaceHolder, 0L);
                        }

                        @Override
                        public void onSuccess(FrameBean result) {
                            if (isFinished()) {
                                return;
                            }
                            setTarget(result.drawable, result.duration);
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (isFinished()) {
                                return;
                            }
                            if (mRequestOptions.mError == null) {
                                return;
                            }
                            setTarget(mRequestOptions.mError, 0L);
                        }

                        private void setTarget(Drawable drawable, Long duration) {
                            if (getTarget() instanceof IFrame) {
                                ((IFrame) getTarget()).setFrame(drawable, duration);
                            } else if (getTarget() instanceof ImageView) {
                                ((ImageView) getTarget()).setImageDrawable(drawable);
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
        public void listener(CacheListener<FrameBean> l) {
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
            new FrameCacheManager(getContext())
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
        FrameCacheManager.release();
    }
}
