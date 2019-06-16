package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.d.lib.cache.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private InputStreamProvider mProvider;
    private File mFile;
    private int mSrcWidth;
    private int mSrcHeight;
    private boolean mFocusAlpha;

    Engine(InputStreamProvider provider, File file, boolean focusAlpha) throws IOException {
        this.mFile = file;
        this.mProvider = provider;
        this.mFocusAlpha = focusAlpha;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        InputStream input = null;
        try {
            input = provider.open();
            BitmapFactory.decodeStream(input, null, options);
        } finally {
            Util.closeQuietly(input);
        }
        this.mSrcWidth = options.outWidth;
        this.mSrcHeight = options.outHeight;
    }

    private int computeSize() {
        mSrcWidth = mSrcWidth % 2 == 1 ? mSrcWidth + 1 : mSrcWidth;
        mSrcHeight = mSrcHeight % 2 == 1 ? mSrcHeight + 1 : mSrcHeight;

        int longSide = Math.max(mSrcWidth, mSrcHeight);
        int shortSide = Math.min(mSrcWidth, mSrcHeight);

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

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    File compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();

        Bitmap bitmap = null;
        InputStream input = null;
        try {
            input = mProvider.open();
            bitmap = BitmapFactory.decodeStream(mProvider.open(), null, options);
        } finally {
            Util.closeQuietly(input);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            input = mProvider.open();
            if (Checker.SINGLE.isJPG(input)) {
                Util.closeQuietly(input);
                input = mProvider.open();
                bitmap = rotatingImage(bitmap, Checker.SINGLE.getOrientation(input));
            }
        } finally {
            Util.closeQuietly(input);
        }

        bitmap.compress(mFocusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 60, stream);
        bitmap.recycle();

        FileOutputStream fos = new FileOutputStream(mFile);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();

        return mFile;
    }
}