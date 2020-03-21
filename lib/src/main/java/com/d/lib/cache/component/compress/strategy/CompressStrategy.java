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
 * CompressStrategy
 * Created by D on 2020/3/21.
 */
public abstract class CompressStrategy {

    public abstract Bitmap decodeStream(@NonNull InputStream input, @NonNull BitmapOptions opts,
                                        @NonNull BitmapFactory.Options setting);

    public Bitmap matrix(@NonNull Bitmap source, @NonNull BitmapOptions opts) {
        if (Bitmap.CompressFormat.JPEG == opts.format
                && opts.degree != 0) {
            // Rotate
            Bitmap bitmap = null;
            Matrix matrix = new Matrix();
            matrix.setRotate(opts.degree);
            try {
                bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                        matrix, true);
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

    public ByteArrayOutputStream qualityCompress(@NonNull Bitmap source, @NonNull BitmapOptions opts,
                                                 @NonNull BitmapOptions requestOpts) {
        Bitmap.CompressFormat format = requestOpts.format != null ? requestOpts.format : opts.format;
        return Engine.qualityCompress(source, format, requestOpts.quality, requestOpts.size);
    }
}
