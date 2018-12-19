package com.d.lib.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.exception.CacheException;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.manager.ImageCacheManager;

/**
 * ImageCache
 * Created by D on 2018/12/19.
 **/
public class ImageCache extends AbstractCache<ImageCache, View, String, Bitmap, Bitmap> {

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

    @Override
    public ImageCache placeholder(Bitmap placeHolder) {
        return super.placeholder(placeHolder);
    }

    @Override
    public ImageCache error(Bitmap error) {
        return super.error(error);
    }

    @Override
    public void into(View view) {
        if (isFinishing() || view == null) {
            return;
        }
        if (TextUtils.isEmpty(mKey)) {
            // Just error
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(mError != null ? mError : mPlaceHolder);
            }
            return;
        }
        setTarget(view);
        Object tag = view.getTag(getTag());
        if (tag != null && tag instanceof String && TextUtils.equals((String) tag, mKey)) {
            // Not refresh
            return;
        }
        view.setTag(getTag(), mKey);
        ImageCacheManager.getIns(getContext().getApplicationContext())
                .load(getContext().getApplicationContext(), mKey, new CacheListener<Bitmap>() {
                    @Override
                    public void onLoading() {
                        if (isFinished()) {
                            return;
                        }
                        if (mPlaceHolder == null) {
                            return;
                        }
                        setTarget(mPlaceHolder);
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
                        if (mError == null) {
                            return;
                        }
                        setTarget(mError);
                    }

                    private void setTarget(Bitmap result) {
                        if (getTarget() instanceof ImageView) {
                            ((ImageView) getTarget()).setImageBitmap(result);
                        }
                    }

                    private boolean isFinished() {
                        if (isFinishing() || getTarget() == null) {
                            return true;
                        }
                        Object tag = getTarget().getTag(getTag());
                        return tag == null || !(tag instanceof String) || !TextUtils.equals((String) tag, mKey);
                    }
                });
    }

    @Override
    public void listener(CacheListener<Bitmap> l) {
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
        ImageCacheManager.getIns(getContext()).load(getContext(), mKey, l);
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
        ImageCacheManager.getIns(context.getApplicationContext()).release();
    }
}
