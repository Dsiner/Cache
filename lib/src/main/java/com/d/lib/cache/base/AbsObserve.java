package com.d.lib.cache.base;

import android.support.annotation.NonNull;
import android.view.View;

import com.d.lib.cache.utils.threadpool.Schedulers;

import java.lang.ref.WeakReference;

public abstract class AbsObserve<C extends AbsObserve,
        Target, Result, Options extends RequestOptions<Result>> {

    protected WeakReference<Target> mTarget;
    protected Options mRequestOptions;
    protected int mScheduler = Schedulers.io();
    protected int mObserveOnScheduler = Schedulers.mainThread();

    protected abstract int TAG();

    protected void setTarget(Target target) {
        mTarget = new WeakReference<>(target);
    }

    protected Target getTarget() {
        return mTarget != null ? mTarget.get() : null;
    }

    protected Object getTag() {
        Target target = getTarget();
        if (target instanceof View) {
            View view = (View) target;
            return view.getTag(TAG());
        }
        return null;
    }

    protected boolean attached(Object tag) {
        Target target = getTarget();
        if (target instanceof View) {
            View view = (View) target;
            Object viewTag = view.getTag(TAG());
            if (viewTag == null || !viewTag.equals(tag)) {
                view.setTag(TAG(), tag);
                return true;
            }
        }
        return false;
    }

    protected boolean isDetached(Object tag) {
        if (getTarget() == null) {
            return true;
        }
        Object viewTag = getTag();
        return viewTag == null || !viewTag.equals(tag);
    }

    public C apply(@NonNull Options options) {
        this.mRequestOptions = options;
        return (C) this;
    }

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
