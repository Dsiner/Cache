package com.d.lib.cache.base;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by D on 2018/6/8.
 */
public abstract class AbstractCache<R extends AbstractCache,
        Observe, Uri> {
    protected WeakReference<Context> mContext;
    protected Uri mUri;

    protected AbstractCache(Context context) {
        mContext = new WeakReference<>(context instanceof Activity
                ? context : context.getApplicationContext());
    }

    protected abstract Observe load(Uri uri);

    protected Context getContext() {
        return mContext != null ? mContext.get() : null;
    }

    protected boolean isFinishing() {
        return getContext() == null
                || getContext() instanceof Activity && ((Activity) getContext()).isFinishing();
    }
}
