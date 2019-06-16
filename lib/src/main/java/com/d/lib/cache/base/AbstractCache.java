package com.d.lib.cache.base;

import android.app.Activity;
import android.content.Context;

import com.d.lib.cache.utils.threadpool.Schedulers;

import java.lang.ref.WeakReference;

/**
 * Created by D on 2018/6/8.
 */
public abstract class AbstractCache<R extends AbstractCache, Target, Key, RequestOptions> {
    protected WeakReference<Context> mContext;
    protected WeakReference<Target> mTarget;
    protected Key mKey;
    protected RequestOptions mRequestOptions;

    protected AbstractCache(Context context) {
        this.mContext = new WeakReference<>(context instanceof Activity
                ? context : context.getApplicationContext());
    }

    public R load(Key key) {
        this.mKey = key;
        return (R) this;
    }

    protected void setTarget(Target target) {
        mTarget = new WeakReference<>(target);
    }

    protected Target getTarget() {
        return mTarget != null ? mTarget.get() : null;
    }

    protected Context getContext() {
        return mContext != null ? mContext.get() : null;
    }

    protected boolean isFinishing() {
        return mContext == null || mContext.get() == null
                || mContext.get() instanceof Activity && ((Activity) mContext.get()).isFinishing();
    }

    public static abstract class AbsObserve<C extends AbsObserve, Target, Result> {
        protected int mScheduler = Schedulers.io();
        protected int mObserveOnScheduler = Schedulers.mainThread();

        public C subscribeOn(@Schedulers.Scheduler int scheduler) {
            this.mScheduler = scheduler;
            return (C) this;
        }

        public C observeOn(@Schedulers.Scheduler int scheduler) {
            this.mObserveOnScheduler = scheduler;
            return (C) this;
        }

        public abstract void into(final Target target);

        public abstract void listener(CacheListener<Result> l);
    }
}
