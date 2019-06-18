package com.d.lib.cache.component.compress;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.cache.base.CacheListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Compress
 * Created by D on 2018/12/20.
 **/
public class Compress {
    private static final String TAG = "Compress";
    private static final String PATH = Environment.getExternalStorageDirectory() + "/Cache/image/";
    private static final String DEFAULT_DISK_CACHE_DIR = "compress_disk_cache";

    private final Context mContext;
    private final RequestOptions mRequestOptions;
    private String mPath;

    Compress(@NonNull Context context, @NonNull RequestOptions requestOptions) {
        this.mContext = context;
        this.mRequestOptions = requestOptions;
        this.mPath = !TextUtils.isEmpty(requestOptions.path) ? requestOptions.path : PATH;
    }

    public void compress(@NonNull CacheListener<File> listener) {
        try {
            final File file;
            Engine engine = new Engine(mRequestOptions.provider, mRequestOptions.options);
            if (needCompress(mRequestOptions.leastCompressSize,
                    mRequestOptions.provider.getPath())) {
                ByteArrayOutputStream outputStream = engine.compress();
                file = !TextUtils.isEmpty(mRequestOptions.name)
                        ? getImageCustomFile(mContext, mRequestOptions.name, engine.mOptions.mimeType)
                        : getImageCacheFile(mContext, engine.mOptions.mimeType);
                FileOutputStream fos = new FileOutputStream(file);
                outputStream.writeTo(fos);
                fos.close();
            } else {
                file = new File(mRequestOptions.provider.getPath());
            }
            listener.onSuccess(file);
        } catch (Throwable e) {
            e.printStackTrace();
            listener.onError(e);
        }
    }

    private boolean needCompress(int leastCompressSize, String path) {
        if (leastCompressSize > 0) {
            File source = new File(path);
            return source.exists() && source.length() > (leastCompressSize << 10);
        }
        return true;
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context Context.
     */
    private File getImageCacheFile(Context context, String suffix) {
        if (TextUtils.isEmpty(getPath(mPath))) {
            mPath = getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR).getAbsolutePath();
        }
        String cacheBuilder = mPath + "/"
                + mRequestOptions.provider.getPath().hashCode()
                + (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        return new File(cacheBuilder);
    }

    private File getImageCustomFile(Context context, String filename, String suffix) {
        if (TextUtils.isEmpty(getPath(mPath))) {
            mPath = getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR).getAbsolutePath();
        }
        String cacheBuilder = mPath + "/" + filename
                + (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
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

    private static String getPath(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs() ? path : "";
    }
}
