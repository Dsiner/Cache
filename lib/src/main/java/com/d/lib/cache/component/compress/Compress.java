package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.cache.listener.CacheListener;

import java.io.File;
import java.io.IOException;

/**
 * FileCache
 * Created by D on 2018/12/20.
 **/
public class Compress {
    private static final String TAG = "Compress";
    private static final String DEFAULT_DISK_CACHE_DIR = "compress_disk_cache";
    private final RequestOptions mRequestOptions;
    private final Context mContext;

    private String mTargetDir;

    private static String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Cache/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    public Compress(Context context, RequestOptions requestOptions) {
        this.mContext = context;
        this.mRequestOptions = requestOptions;
    }

    public void compress(@NonNull CacheListener<File> listener) {
        this.mTargetDir = getPath();
        File outFile = getImageCacheFile(mContext, Checker.SINGLE.extSuffix(mRequestOptions.provider));
        try {
            File result = Checker.SINGLE.needCompress(mRequestOptions.leastCompressSize,
                    mRequestOptions.provider.getPath()) ?
                    new Engine(mRequestOptions.provider, outFile, mRequestOptions.focusAlpha).compress() :
                    new File(mRequestOptions.provider.getPath());
            listener.onSuccess(result);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onError(e);
        }
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context Context.
     */
    private File getImageCacheFile(Context context, String suffix) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);

        return new File(cacheBuilder);
    }

    private File getImageCustomFile(Context context, String filename) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/" + filename;

        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     *
     * @param context A context.
     * @see #getImageCacheDir(Context, String)
     */
    private File getImageCacheDir(Context context) {
        return getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getImageCacheDir(Context)
     */
    private static File getImageCacheDir(Context context, String cacheName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }
}
