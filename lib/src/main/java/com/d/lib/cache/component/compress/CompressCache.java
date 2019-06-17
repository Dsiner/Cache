package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheException;
import com.d.lib.cache.base.CacheListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ImageCache
 * Created by D on 2018/12/19.
 **/
public class CompressCache extends AbstractCache<CompressCache, View, String, RequestOptions> {
    private InputStreamProvider mProvider;

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
    public CompressCache load(@NonNull String path) {
        return load(new File(path));
    }

    public CompressCache load(@NonNull final File file) {
        mProvider = new InputStreamProvider() {
            @Override
            public String getPath() {
                return file.getAbsolutePath();
            }

            @Override
            public InputStream open() throws IOException {
                return new FileInputStream(file);
            }
        };
        return super.load(mProvider.getPath());
    }

    public CompressCache load(@Nullable final Uri uri) {
        final Context appContext = getContext().getApplicationContext();
        mProvider = new InputStreamProvider() {
            @Override
            public String getPath() {
                return uri.getPath();
            }

            @Override
            public InputStream open() throws IOException {
                return appContext.getContentResolver().openInputStream(uri);
            }
        };
        return super.load(mProvider.getPath());
    }

    public Observe apply(RequestOptions options) {
        this.mRequestOptions = options;
        this.mRequestOptions.mProvider = mProvider;
        return new Observe();
    }

    public class Observe extends AbsObserve<Observe, View, Bitmap> {
        Observe() {
        }

        @Override
        public void into(View view) {
            if (isFinishing() || view == null) {
                return;
            }
            if (TextUtils.isEmpty(mKey)) {
                // Just error
                if (view instanceof ImageView) {
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
            new CompressBitmapCacheManager(getContext().getApplicationContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .setRequestOptions(mRequestOptions)
                    .load(getContext().getApplicationContext(), mRequestOptions.mProvider,
                            new CacheListener<Bitmap>() {
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
                                    if (mRequestOptions.mError == null) {
                                        return;
                                    }
                                    setTarget(mRequestOptions.mError);
                                }

                                private void setTarget(Object result) {
                                    if (getTarget() instanceof ImageView) {
                                        if (result instanceof Bitmap) {
                                            ((ImageView) getTarget()).setImageBitmap((Bitmap) result);
                                        } else if (result instanceof Drawable) {
                                            ((ImageView) getTarget()).setImageDrawable((Drawable) result);
                                        }
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
            new CompressBitmapCacheManager(getContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .setRequestOptions(mRequestOptions)
                    .load(getContext(), mRequestOptions.mProvider, l);
        }

        public void file(CacheListener<File> l) {
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
            new CompressFileCacheManager(getContext())
                    .subscribeOn(mScheduler)
                    .observeOn(mObserveOnScheduler)
                    .setRequestOptions(mRequestOptions)
                    .load(getContext(), mRequestOptions.mProvider, l);
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
        CompressFileCacheManager.release();
        CompressBitmapCacheManager.release();
    }
}
