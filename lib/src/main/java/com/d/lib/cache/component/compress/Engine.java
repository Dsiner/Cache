package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Engine {
    final InputStreamProvider mProvider;
    final BitmapOptions mRequestOptions;
    final BitmapOptions mOptions;

    Engine(@NonNull InputStreamProvider provider, @NonNull BitmapOptions requestOptions)
            throws IOException {
        mProvider = provider;
        mRequestOptions = requestOptions;
        mOptions = new BitmapOptions();
        initOptions();
    }

    @Nullable
    public static BitmapFactory.Options decodeStream(final File file) {
        try {
            return decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BitmapFactory.Options decodeStream(final InputStream input) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            BitmapFactory.decodeStream(input, null, options);
            return options;
        } finally {
            ImageUtil.closeQuietly(input);
        }
    }

    public static ByteArrayOutputStream qualityCompress(Bitmap bitmap,
                                                        Bitmap.CompressFormat format,
                                                        int quality, int size) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, outputStream);
        if (Bitmap.CompressFormat.PNG == format || size <= 0) {
            return outputStream;
        }
        while (outputStream.size() / 1024f > size && quality > 0) {
            outputStream.reset();
            if (quality > 10) {
                quality -= 10;
            } else {
                quality -= 3;
            }
            quality = Math.max(0, quality);
            bitmap.compress(format, quality, outputStream);
        }
        Log.d("Compress", "Compress size: " + outputStream.size() + " quality: " + quality);
        return outputStream;
    }

    private void initOptions() throws IOException {
        BitmapFactory.Options options = decodeStream(mProvider.open());
        mOptions.width = options.outWidth;
        mOptions.height = options.outHeight;
        mOptions.format = BitmapOptions.format(options.outMimeType.replace("image/", "."));
        if (Bitmap.CompressFormat.JPEG == mOptions.format) {
            mOptions.degree = ImageUtil.getImageDegree(mProvider.getPath());
        }
    }

    ByteArrayOutputStream compress() throws IOException {
        InputStream input = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];
            if (mRequestOptions.config != null) {
                options.inPreferredConfig = mRequestOptions.config;
            }
            input = mProvider.open();
            Bitmap bitmap = mRequestOptions.strategy.decodeStream(input, mOptions, options);
            bitmap = mRequestOptions.strategy.matrix(bitmap, mOptions);
            ByteArrayOutputStream stream = mRequestOptions.strategy.qualityCompress(bitmap,
                    mOptions, mRequestOptions);
            bitmap.recycle();
            return stream;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            ImageUtil.closeQuietly(input);
        }
    }
}