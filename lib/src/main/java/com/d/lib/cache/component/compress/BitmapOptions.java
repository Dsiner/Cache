package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

/**
 * BitmapOptions
 * Created by D on 2019/6/18.
 **/
public class BitmapOptions {
    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
    int width;
    int height;
    int degree;

    int quality = 85;
    int size;
    boolean average = false;

    public static String mimeType(Bitmap.CompressFormat format) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && format == Bitmap.CompressFormat.WEBP) {
            return ".webp";
        }
        if (format == Bitmap.CompressFormat.JPEG) {
            return ".jpg";
        } else if (format == Bitmap.CompressFormat.PNG) {
            return ".png";
        }
        return ".jpg";
    }

    public static Bitmap.CompressFormat format(String mimeType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && TextUtils.equals(mimeType, ".webp")) {
            return Bitmap.CompressFormat.WEBP;
        }
        if (TextUtils.equals(mimeType, ".jpg")) {
            return Bitmap.CompressFormat.JPEG;
        } else if (TextUtils.equals(mimeType, ".png")) {
            return Bitmap.CompressFormat.PNG;
        }
        return Bitmap.CompressFormat.JPEG;
    }

    @Override
    public String toString() {
        return "" + width + "*" + height + "-" + degree + "-" + quality + "-" + size + "-" + format;
    }
}
