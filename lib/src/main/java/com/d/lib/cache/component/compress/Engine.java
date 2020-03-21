package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
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

    private void initOptions() throws IOException {
        InputStream input = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;
            input = mProvider.open();
            BitmapFactory.decodeStream(input, null, options);
            mOptions.width = options.outWidth;
            mOptions.height = options.outHeight;
            mOptions.format = BitmapOptions.format(options.outMimeType.replace("image/", "."));
            if (Bitmap.CompressFormat.JPEG == mOptions.format) {
                mOptions.degree = ImageUtil.getImageDegree(mProvider.getPath());
            }
        } finally {
            ImageUtil.closeQuietly(input);
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

            ByteArrayOutputStream stream = qualityCompress(bitmap,
                    mRequestOptions.format != null ? mRequestOptions.format : mOptions.format,
                    mRequestOptions.quality, mRequestOptions.size);
            bitmap.recycle();
            return stream;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
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
        while (outputStream.size() / 1024 > size && quality > 0) {
            outputStream.reset();
            if (quality > 10) {
                quality -= 10;
            } else {
                quality -= 3;
            }
            quality = Math.max(0, quality);
            bitmap.compress(format, quality, outputStream);
        }
        return outputStream;
    }
}