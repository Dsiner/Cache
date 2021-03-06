package com.d.lib.cache.util.threadpool;

import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Schedulers
 * Created by D on 2018/5/15.
 */
public class Schedulers {
    static final int DEFAULT_THREAD = 0;
    static final int NEW_THREAD = 1;
    static final int IO = 2;
    static final int MAIN_THREAD = 3;

    @Scheduler
    public static int defaultThread() {
        return DEFAULT_THREAD;
    }

    @Scheduler
    public static int newThread() {
        return NEW_THREAD;
    }

    @Scheduler
    public static int io() {
        return IO;
    }

    @Scheduler
    public static int mainThread() {
        return MAIN_THREAD;
    }

    /**
     * Executes the given runnable at some time in the future.
     * The runnable may execute in a new thread, in a pooled thread, or in the calling thread
     */
    public static void switchThread(@Scheduler final int scheduler, @NonNull final Runnable runnable) {
        if (scheduler == NEW_THREAD) {
            TaskManager.getInstance().executeNew(runnable);
            return;
        } else if (scheduler == IO) {
            TaskManager.getInstance().executeTask(runnable);
            return;
        } else if (scheduler == MAIN_THREAD) {
            TaskManager.getInstance().executeMain(runnable);
            return;
        }
        runnable.run();
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @IntDef({DEFAULT_THREAD, NEW_THREAD, IO, MAIN_THREAD})
    @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Scheduler {

    }
}
