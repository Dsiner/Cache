package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;

import com.d.lib.cache.R;
import com.d.lib.cache.base.AbsObserve;
import com.d.lib.cache.base.AbstractCache;
import com.d.lib.cache.base.CacheListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * CompressCache
 * Created by D on 2018/12/19.
 **/
public class CompressCache<T> extends AbstractCache<CompressCache<T>,
        CompressCache<T>.Observe<T>, String> {

    private static int TAG_ID = R.id.lib_cache_tag_compress;

    private final Class<T> mTranscodeClass;
    private InputStreamProvider mProvider;

    private CompressCache(Context context, Class<T> transcodeClass) {
        super(context);
        mTranscodeClass = transcodeClass;
    }

    @UiThread
    public static CompressCache<Bitmap> with(Context context) {
        return new CompressCache<>(context, Bitmap.class);
    }

    public CompressCache<Drawable> asDrawable() {
        return new CompressCache<>(getContext(), Drawable.class);
    }

    public CompressCache<Bitmap> asBitmap() {
        return new CompressCache<>(getContext(), Bitmap.class);
    }

    public CompressCache<File> asFile() {
        return new CompressCache<>(getContext(), File.class);
    }

    @Override
    public CompressCache<T>.Observe<T> load(@NonNull String path) {
        return load(new File(path));
    }

    public CompressCache<T>.Observe<T> load(@NonNull final File file) {
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
        mUri = mProvider.getPath();
        return new Observe<>();
    }

    public CompressCache<T>.Observe<T> load(@NonNull final Uri uri) {
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
        mUri = mProvider.getPath();
        return new Observe<>();
    }

    public class Observe<Type> extends AbsObserve<Observe<Type>,
            View, Type, RequestOptions<Type>> {

        @Override
        protected int TAG() {
            return TAG_ID;
        }

        Observe() {
            mRequestOptions = new RequestOptions<>();
            mRequestOptions.provider = mProvider;
        }

        @Override
        public Observe<Type> apply(@NonNull RequestOptions<Type> options) {
            mRequestOptions = options;
            mRequestOptions.provider = mProvider;
            mUri = mUri + "_" + mRequestOptions.options.toString();
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
            new CompressBitmapCacheFetcher(getContext(),
                    mScheduler, mObserveOnScheduler,
                    mRequestOptions)
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

                        private void setTarget(Object result) {
                            if (result == null) {
                                return;
                            }
                            if (getTarget() instanceof ImageView) {
                                if (result instanceof Bitmap) {
                                    ((ImageView) getTarget()).setImageBitmap((Bitmap) result);
                                } else if (result instanceof Drawable) {
                                    ((ImageView) getTarget()).setImageDrawable((Drawable) result);
                                }
                            }
                        }
                    });
        }

        @Override
        public void listener(CacheListener<Type> l) {
            if (isFinishing()) {
                return;
            }
            if (mTranscodeClass.equals(Drawable.class)) {
                new CompressDrawableCacheFetcher(getContext(), mScheduler, mObserveOnScheduler,
                        mRequestOptions)
                        .load(getContext().getApplicationContext(), mUri, (CacheListener<Drawable>) l);
            } else if (mTranscodeClass.equals(Bitmap.class)) {
                new CompressBitmapCacheFetcher(getContext(), mScheduler, mObserveOnScheduler,
                        mRequestOptions)
                        .load(getContext().getApplicationContext(), mUri, (CacheListener<Bitmap>) l);
            } else if (mTranscodeClass.equals(File.class)) {
                new CompressFileCacheFetcher(getContext(), mScheduler, mObserveOnScheduler,
                        mRequestOptions)
                        .load(getContext().getApplicationContext(), mUri, (CacheListener<File>) l);
            }
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
        CompressFileCacheFetcher.release();
        CompressBitmapCacheFetcher.release();
    }
}
