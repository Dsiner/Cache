package com.d.lib.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.component.compress.RequestOptions;
import com.d.lib.cache.exception.CacheException;
import com.d.lib.cache.listener.CacheListener;
import com.d.lib.cache.manager.CompressCacheManager;

import java.io.File;
import java.io.InputStream;

/**
 * ImageCache
 * Created by D on 2018/12/19.
 **/
public class CompressCache extends AbstractCache<CompressCache, View, String, Bitmap, Bitmap> {
    private RequestOptions requestOptions = new RequestOptions();

    private CompressCache(Context context) {
        super(context);
    }

    private static int getTag() {
        return R.id.lib_cache_tag_compress;
    }

    @UiThread
    public static CompressCache with(Context context) {
        return new CompressCache(context);
    }

    @Override
    public CompressCache load(String string) {
        requestOptions.load(string);
        return super.load(string);
    }

    public CompressCache load(@Nullable InputStream inputStream) {
        requestOptions.load(inputStream);
        return this;
    }

    public CompressCache load(@Nullable final Uri uri) {
        requestOptions.load(getContext(), uri);
        return this;
    }

    public CompressCache load(@Nullable File file) {
        requestOptions.load(file);
        return this;
    }

    @Override
    public CompressCache placeholder(Bitmap placeHolder) {
        return super.placeholder(placeHolder);
    }

    @Override
    public CompressCache error(Bitmap error) {
        return super.error(error);
    }

    @NonNull
    public CompressCache apply(@NonNull RequestOptions requestOptions) {
        requestOptions.provider = this.requestOptions.provider;
        this.requestOptions = requestOptions;
        return this;
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
        CompressCacheManager.getIns(getContext().getApplicationContext())
                .setRequestOptions(requestOptions)
                .load(getContext().getApplicationContext(), requestOptions.provider,
                        new CacheListener<Bitmap>() {
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
        CompressCacheManager.getIns(getContext()).load(getContext(), requestOptions.provider, l);
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
        CompressCacheManager.getIns(context.getApplicationContext()).release();
    }
}
