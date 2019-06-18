package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;

/**
 * BitmapOptions
 * Created by D on 2019/6/18.
 **/
public class BitmapOptions {
    String mimeType = ".jpg";
    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
    int width;
    int height;
    int degree;

    int quality = 60;
    int size;

    @Override
    public String toString() {
        return "" + width + "*" + height + degree + quality + size + format;
    }
}
