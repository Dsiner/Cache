package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.d.lib.cache.base.AbstractCacheFetcher;
import com.d.lib.cache.utils.threadpool.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by D on 2017/10/18.
 */
public abstract class CompressCacheFetcher<T>
        extends AbstractCacheFetcher<CompressCacheFetcher<T>, String, T> {
    private static final String TAG = "Compress";
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/Cache/image/";
    private static final String DEFAULT_DISK_CACHE_DIR = "compress_disk_cache";

    protected final Context mContext;
    protected final CompressOptions mCompressOptions;
    protected String mPath;

    public CompressCacheFetcher(@NonNull Context context,
                                @NonNull CompressOptions requestOptions,
                                @Schedulers.Scheduler int scheduler,
                                @Schedulers.Scheduler int observeOnScheduler) {
        super(context, requestOptions, scheduler, observeOnScheduler);
        mContext = context.getApplicationContext();
        mCompressOptions = requestOptions;
        mPath = !TextUtils.isEmpty(requestOptions.path) ? requestOptions.path : PATH;
    }

    public ByteArrayOutputStream compress() throws Exception {
        Engine engine = new Engine(mCompressOptions.provider, mCompressOptions.options);
        return engine.compress();
    }

    public File compressFile() throws Exception {
        Engine engine = new Engine(mCompressOptions.provider, mCompressOptions.options);
        ByteArrayOutputStream outputStream = engine.compress();
        return convert(engine.mRequestOptions.format != null ? engine.mRequestOptions.format : engine.mOptions.format,
                outputStream);
    }

    protected ByteArrayOutputStream convert(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream;
        outputStream = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int len;
        while ((len = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, len);
            outputStream.flush();
        }
        return outputStream;
    }

    protected File convert(Bitmap.CompressFormat format, ByteArrayOutputStream outputStream) throws IOException {
        String mimeType = BitmapOptions.mimeType(format);
        File file = getImageCacheFile(mContext, mCompressOptions.name, mimeType);
        FileOutputStream fos = new FileOutputStream(file);
        outputStream.writeTo(fos);
        fos.close();
        return file;
    }

    protected boolean needCompress() {
        int leastCompressSize = mCompressOptions.leastCompressSize;
        String path = mCompressOptions.provider.getPath();
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
    private File getImageCacheFile(Context context, String filename, String suffix) {
        if (TextUtils.isEmpty(getPath(mPath))) {
            mPath = getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR).getAbsolutePath();
        }
        String cacheBuilder = mPath + "/"
                + (TextUtils.isEmpty(filename)
                ? (mCompressOptions.provider.getPath() + "-" + mCompressOptions.options.toString()).hashCode()
                : filename)
                + (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   Context.
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

    @Override
    protected T getDisk(String url) {
        return null;
    }

    @Override
    protected void putDisk(String url, T value) {

    }
}
