package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
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
            if (mRequestOptions.width > 0 && mRequestOptions.height > 0) {
                options.inSampleSize = calculateInSampleSize(mOptions.width, mOptions.height,
                        mRequestOptions.width, mRequestOptions.height, mRequestOptions.average);
            } else {
                options.inSampleSize = calculateInSampleSize(mOptions.width, mOptions.height);
            }
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];
            if (mRequestOptions.config != null) {
                options.inPreferredConfig = mRequestOptions.config;
            }
            input = mProvider.open();
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

            // Rotate
            if (Bitmap.CompressFormat.JPEG == mOptions.format
                    && mOptions.degree != 0) {
                bitmap = rotate(bitmap, mOptions.degree);
            }

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

    private ByteArrayOutputStream qualityCompress(Bitmap bitmap,
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

    private Bitmap rotate(Bitmap source, int degree) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError ignored) {
        }
        if (bitmap == null) {
            bitmap = source;
        }
        if (source != bitmap) {
            source.recycle();
        }
        return bitmap;
    }

    private int calculateInSampleSize(int width, int height, int maxWidth, int maxHeight, boolean average) {
        width = width % 2 == 1 ? width + 1 : width;
        height = height % 2 == 1 ? height + 1 : height;

        int inSampleSize = 1;
        if (average) {
            while ((width / inSampleSize) * (height / inSampleSize) > maxWidth * maxHeight) {
                inSampleSize *= 2;
            }
        } else {
            while (width / inSampleSize > maxWidth || height / inSampleSize > maxHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private int calculateInSampleSize(int width, int height) {
        width = width % 2 == 1 ? width + 1 : width;
        height = height % 2 == 1 ? height + 1 : height;

        int longSide = Math.max(width, height);
        int shortSide = Math.min(width, height);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }
}