package com.d.cache;

import android.app.Application;

import com.d.lib.cache.Cache;
import com.d.lib.cache.utils.threadpool.ThreadPool;
import com.d.lib.taskscheduler.TaskScheduler;

/**
 * App
 * Created by D on 2018/8/25.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // You can set the thread pool yourself here, otherwise the default will be used.
        Cache.setThreadPool(new ThreadPool() {
            @Override
            public void executeMain(Runnable r) {
                TaskScheduler.executeMain(r);
            }

            @Override
            public void executeTask(Runnable r) {
                TaskScheduler.executeTask(r);
            }

            @Override
            public void executeNew(Runnable r) {
                TaskScheduler.executeNew(r);
            }
        });
    }
}
