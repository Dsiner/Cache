package com.d.lib.cache.component.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbsObserve;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.RequestOptions;

/**
 * ImageCache
 * Created by D on 2018/12/19.
 **/
public class ImageCache extends AbstractCache<ImageCache,
        ImageCache.Observe, String> {

    private static int TAG_ID = R.id.lib_cache_tag_image;

    private ImageCache(Context context) {
        super(context);
    }

    @UiThread
    public static ImageCache with(Context context) {
        return new ImageCache(context);
    }

    @Override
    public ImageCache.Observe load(String url) {
        mUri = url;
        return new ImageCache.Observe();
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
        ImageCacheFetcher.release();
    }

    public class Observe extends AbsObserve<Observe,
            View, Bitmap, RequestOptions<Bitmap>> {

        @Override
        protected int TAG() {
            return TAG_ID;
        }

        Observe() {
            mRequestOptions = new RequestOptions<>();
        }

        @Override
        public Observe apply(@NonNull RequestOptions<Bitmap> options) {
            mRequestOptions = options;
            return this;
        }

        @Override
        public void into(View view) {
            if (isFinishing() || view == null) {
                return;
            }
            setTarget(view);
            if (!attached(mUri)) {
                return;
            }
            new ImageCacheFetcher(getContext(), mRequestOptions, mScheduler, mObserveOnScheduler)
                    .load(getContext().getApplicationContext(), mUri, new CacheListener<Bitmap>() {
                        @Override
                        public void onLoading() {
                            if (isFinishing() || isDetached(mUri)) {
                                return;
                            }
                            setTarget(mRequestOptions.placeHolder);
                        }

                        @Override
                        public void onSuccess(Bitmap result) {
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

                        private void setTarget(Bitmap result) {
                            if (result == null) {
                                return;
                            }
                            if (getTarget() instanceof ImageView) {
                                ((ImageView) getTarget()).setImageBitmap(result);
                            }
                        }
                    });
        }

        @Override
        public void listener(CacheListener<Bitmap> l) {
            if (isFinishing()) {
                return;
            }
            new ImageCacheFetcher(getContext(), mRequestOptions, mScheduler, mObserveOnScheduler)
                    .load(getContext().getApplicationContext(), mUri, l);
        }
    }
}
