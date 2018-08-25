package com.d.lib.cache.utils.thread;

/**
 * Abstract thread pool, you can also implement it yourself,
 * the implementation here is TaskManager
 * Created by D on 2018/5/15.
 */
public class TaskScheduler {

    private TaskScheduler() {
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    public static boolean postMain(Runnable r) {
        return TaskManager.getIns().postMain(r);
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run in the main thread
     */
    public static boolean postMainDelayed(Runnable r, long delayMillis) {
        return TaskManager.getIns().postMainDelayed(r, delayMillis);
    }

    /**
     * Execute sync task in the main thread
     */
    public static void executeMain(Runnable r) {
        TaskManager.getIns().executeMain(r);
    }

    /**
     * Execute async task in the cached thread pool
     */
    public static void executeTask(Runnable r) {
        TaskManager.getIns().executeTask(r);
    }

    /**
     * Execute async task in the single thread pool
     */
    public static void executeSingle(Runnable r) {
        TaskManager.getIns().executeSingle(r);
    }

    /**
     * Execute async task in a new thread
     */
    public static void executeNew(Runnable r) {
        TaskManager.getIns().executeNew(r);
    }
}
