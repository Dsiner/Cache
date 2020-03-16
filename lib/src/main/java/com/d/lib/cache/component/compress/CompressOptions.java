package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.d.lib.cache.base.RequestOptions;

/**
 * RequestOptions
 * Created by D on 2018/12/20.
 **/
public class CompressOptions<T> extends RequestOptions<T> {
    InputStreamProvider provider;

    final BitmapOptions options = new BitmapOptions();
    int leastCompressSize;
    String path;
    String name;

    @Override
    public CompressOptions<T> placeholder(@Nullable T placeholder) {
        return (CompressOptions<T>) super.placeholder(placeholder);
    }

    @Override
    public CompressOptions<T> error(@Nullable T error) {
        return (CompressOptions<T>) super.error(error);
    }

    /**
     * Do not compress when the origin image file size less than one value
     *
     * @param size The value of file size, unit KB, default 100K
     */
    public CompressOptions<T> ignoreBy(int size) {
        this.leastCompressSize = size;
        return this;
    }

    public CompressOptions<T> setPath(String path, String name) {
        this.path = path;
        this.name = name;
        return this;
    }

    public CompressOptions<T> setFormat(Bitmap.CompressFormat format) {
        this.options.format = format;
        return this;
    }

    public CompressOptions<T> setMaxWidth(int size) {
        this.options.width = size;
        return this;
    }

    public CompressOptions<T> setMaxHeight(int size) {
        this.options.height = size;
        return this;
    }

    public CompressOptions<T> setQuality(int quality) {
        this.options.quality = quality;
        return this;
    }

    /**
     * The value of file max size
     *
     * @param size The value of file size, unit KB
     */
    public CompressOptions<T> setMaxSize(int size) {
        this.options.size = size;
        return this;
    }

    public CompressOptions<T> setAverage(boolean average) {
        this.options.average = average;
        return this;
    }
}
