package com.d.lib.cache.base;

import android.app.Activity;
import android.content.Context;

import com.d.lib.cache.listener.CacheListener;

import java.lang.ref.WeakReference;

/**
 * Created by D on 2018/6/8.
 */
public abstract class AbstractCache<R extends AbstractCache, Target, Url, Placeholder, Result> {
    protected WeakReference<Context> context;
    protected WeakReference<Target> target;
    protected Url url;
    protected Placeholder placeHolder;
    protected Placeholder error;

    protected AbstractCache(Context context) {
        this.context = new WeakReference<>(context instanceof Activity ? context : context.getApplicationContext());
    }

    public R load(Url url) {
        this.url = url;
        return (R) this;
    }

    public R placeholder(Placeholder placeHolder) {
        this.placeHolder = placeHolder;
        return (R) this;
    }

    public R error(Placeholder placeHolder) {
        this.placeHolder = placeHolder;
        return (R) this;
    }

    protected Context getContext() {
        return context != null ? context.get() : null;
    }

    protected Target getTarget() {
        return target != null ? target.get() : null;
    }

    protected boolean isFinish() {
        return context == null || context.get() == null
                || context.get() instanceof Activity && ((Activity) context.get()).isFinishing();
    }

    public abstract void into(final Target target);

    public abstract void listener(CacheListener<Result> l);
}
