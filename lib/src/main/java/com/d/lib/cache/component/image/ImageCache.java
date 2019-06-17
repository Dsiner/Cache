package com.d.lib.cache.component.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheException;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.RequestOptions;

/**
 * ImageCache
 * Created by D on 2018/12/19.
 **/
public class ImageCache extends AbstractCache<ImageCache, View, String, RequestOptions<Bitmap>> {

    private ImageCache(Context context) {
        super(context);
    }

    private static int getTag() {
        return R.id.lib_cache_tag_image;
    }

    @UiThread
    public static ImageCache with(Context context) {
        return new ImageCache(context);
    }

    @Override
    public ImageCache load(String url) {
        return super.load(url);
    }

    public Observe apply(RequestOptions<Bitmap> options) {
        this.mRequestOptions = options;
        return new Observe(this);
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
        ImageCacheManager.release();
    }

    public static class Observe extends AbsObserve<Observe, View, Bitmap> {
        private ImageCache mCache;

        Observe(ImageCache cache) {
            mCache = cache;
        }

        @Override
        public void into(View view) {
            if (mCache.isFinishing() || view == null) {
                return;
            }
            if (TextUtils.isEmpty(mCache.mKey)) {
                // Just error
                if (view instanceof ImageView) {
                    ((ImageView) view).setImageBitmap(mCache.mRequestOptions.mError != null
                            ? mCache.mRequestOptions.mError : mCache.mRequestOptions.mPlaceHolder);
                }
                return;
            }
            mCache.setTarget(view);
            Object tag = view.getTag(getTag());
            if (tag != null && tag instanceof String
                    && TextUtils.equals((String) tag, mCache.mKey)) {
                // Not refresh
                return;
            }
            view.setTag(getTag(), mCache.mKey);
            new ImageCacheManager(mCache.getContext().getApplicationContext())
                    .load(mCache.getContext().getApplicationContext(), mCache.mKey, new CacheListener<Bitmap>() {
                        @Override
                        public void onLoading() {
                            if (isFinished()) {
                                return;
                            }
                            if (mCache.mRequestOptions.mPlaceHolder == null) {
                                return;
                            }
                            setTarget(mCache.mRequestOptions.mPlaceHolder);
                        }

                        @Override
                        public void onSuccess(Bitmap result) {
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
                            if (mCache.mRequestOptions.mError == null) {
                                return;
                            }
                            setTarget(mCache.mRequestOptions.mError);
                        }

                        private void setTarget(Bitmap result) {
                            if (mCache.getTarget() instanceof ImageView) {
                                ((ImageView) mCache.getTarget()).setImageBitmap(result);
                            }
                        }

                        private boolean isFinished() {
                            if (mCache.isFinishing() || mCache.getTarget() == null) {
                                return true;
                            }
                            Object tag = mCache.getTarget().getTag(getTag());
                            return tag == null || !(tag instanceof String)
                                    || !TextUtils.equals((String) tag, mCache.mKey);
                        }
                    });
        }

        @Override
        public void listener(CacheListener<Bitmap> l) {
            if (mCache.isFinishing()) {
                return;
            }
            if (TextUtils.isEmpty(mCache.mKey)) {
                // Just error
                if (l != null) {
                    l.onError(new CacheException("Url must not be empty!"));
                }
                return;
            }
            new ImageCacheManager(mCache.getContext()).load(mCache.getContext(), mCache.mKey, l);
        }
    }
}
