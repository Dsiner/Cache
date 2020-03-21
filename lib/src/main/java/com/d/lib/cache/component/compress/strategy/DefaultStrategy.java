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
 * DefaultStrategy
 * Created by D on 2020/3/21.
 */
public class DefaultStrategy extends CompressStrategy {
    public static final float LONG_PICTURE_SCALE = 0.14f;

    private final int mInSampleWidth = 1400;
    private final int mInSampleHeight = 1400;

    private final int mMaxWidth = 1024;
    private final int mMaxHeight = 1024;

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
            while (shortSide / inSampleSize > mInSampleWidth) {
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
                final float sx;
                if (scale < LONG_PICTURE_SCALE) {
                    // Long picture
                    sx = (float) Math.sqrt(mMaxWidth / (float) shortSide);
                } else {
                    sx = (float) Math.sqrt(mMaxWidth * mMaxHeight / (float) (width * height));
                }
                matrix.setScale(sx, sx);
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
            int size = (int) (requestOpts.size / scale * 0.6f);
            size = Math.min(7 * 1024 * 1024, size);
            return Engine.qualityCompress(source, format, requestOpts.quality, size);
        } else {
            return Engine.qualityCompress(source, format, requestOpts.quality, requestOpts.size);
        }
    }
}
