package com.d.lib.cache.component.compress.strategy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import com.d.lib.cache.component.compress.BitmapOptions;
import com.d.lib.cache.component.compress.Engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * LongPictureStrategy
 * Created by D on 2020/3/21.
 */
public class LongPictureStrategy extends CompressStrategy {
    public static final float LONG_PICTURE_SCALE = 0.14f;
    public static final float LONG_PICTURE_IN_SAMPLE_WIDTH = 600 * 2;

    private final int mMaxSize;

    public LongPictureStrategy() {
        this.mMaxSize = 4 * 1024;
    }

    public LongPictureStrategy(int size) {
        this.mMaxSize = size;
    }

    @Override
    public Bitmap decodeStream(@NonNull InputStream input, @NonNull BitmapOptions opts,
                               @NonNull BitmapFactory.Options setting) {
        final int width = opts.width % 2 == 1 ? opts.width + 1 : opts.width;
        final int height = opts.height % 2 == 1 ? opts.height + 1 : opts.height;
        final int longSide = Math.max(width, height);
        final int shortSide = Math.min(width, height);
        final float scale = ((float) shortSide / longSide);
        int inSampleSize = 1;
        if (scale < LONG_PICTURE_SCALE) {
            // Long picture
            while (shortSide / inSampleSize > LONG_PICTURE_IN_SAMPLE_WIDTH) {
                inSampleSize *= 2;
            }
        } else {
            while ((width / inSampleSize) * (height / inSampleSize) > mInSampleWidth * mInSampleHeight) {
                inSampleSize *= 2;
            }
        }

        setting.inSampleSize = inSampleSize;
        return BitmapFactory.decodeStream(input, null, setting);
    }

    @Override
    public Bitmap matrix(@NonNull Bitmap source, @NonNull BitmapOptions opts) {
        final int width = source.getWidth();
        final int height = source.getHeight();
        final int longSide = Math.max(width, height);
        final int shortSide = Math.min(width, height);
        final float scale = ((float) shortSide / longSide);
        if (width * height > mMaxWidth * mMaxHeight
                || Bitmap.CompressFormat.JPEG == opts.format && opts.degree != 0) {
            Bitmap bitmap = null;
            Matrix matrix = new Matrix();

            if (width * height > mMaxWidth * mMaxHeight) {
                // Scale
                if (scale < LONG_PICTURE_SCALE) {
                    // Long picture
                } else {
                    float sx = (float) Math.sqrt(mMaxWidth * mMaxHeight / (float) (width * height));
                    sx = getScale(sx, shortSide);

                    sx = Math.min(1, sx);
                    matrix.setScale(sx, sx);
                }
            }

            if (Bitmap.CompressFormat.JPEG == opts.format
                    && opts.degree != 0) {
                // Rotate
                matrix.postRotate(opts.degree);
            }

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
        return source;
    }

    @Override
    public ByteArrayOutputStream qualityCompress(@NonNull Bitmap source, @NonNull BitmapOptions opts,
                                                 @NonNull BitmapOptions requestOpts) {
        final int width = source.getWidth();
        final int height = source.getHeight();
        final int longSide = Math.max(width, height);
        final int shortSide = Math.min(width, height);
        final float scale = ((float) shortSide / longSide);
        Bitmap.CompressFormat format = requestOpts.format != null ? requestOpts.format : opts.format;
        if (scale < LONG_PICTURE_SCALE) {
            // Long picture
            int size = (int) (1f * width * height / (float) (mMaxWidth * mMaxHeight)
                    * requestOpts.size);
            size = Math.min(size, mMaxSize);
            return Engine.qualityCompress(source, format, requestOpts.quality, size);
        } else {
            return Engine.qualityCompress(source, format, requestOpts.quality, requestOpts.size);
        }
    }
}
