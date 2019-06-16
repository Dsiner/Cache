package com.d.lib.cache.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.Closeable;

/**
 * Created by D on 2017/10/23.
 */
public class Util {

    /**
     * Bitmap to Drawable
     */
    public static Drawable bitmapToDrawableByBD(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(bitmap);
    }

    /**
     * Format time, convert milliseconds into seconds: (00:00) format
     * String.format("%02d:%02d", time / 1000 / 60, time / 1000 % 60)
     */
    public static String formatTime(long time) {
        StringBuilder sb;
        long min = time / 1000 / 60;
        long sec = time / 1000 % 60;
        if (min / 10 < 1) {
            sb = new StringBuilder("0");
            sb.append(String.valueOf(min));
        } else {
            sb = new StringBuilder(String.valueOf(min));
        }
        sb.append(":");
        if (sec / 10 < 1) {
            sb.append("0");
        }
        sb.append(String.valueOf(sec));
        return sb.toString();
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing if {@code closeable} is
     * null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
