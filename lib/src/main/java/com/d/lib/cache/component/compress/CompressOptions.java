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

    @Override
    public CompressOptions<T> skipMemoryCache(boolean skip) {
        return (CompressOptions<T>) super.skipMemoryCache(skip);
    }

    @Override
    public CompressOptions<T> diskCacheStrategy(int strategy) {
        return (CompressOptions<T>) super.diskCacheStrategy(strategy);
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

    public CompressOptions<T> path(String path, String name) {
        this.path = path;
        this.name = name;
        return this;
    }

    public CompressOptions<T> config(Bitmap.Config config) {
        this.options.config = config;
        return this;
    }

    public CompressOptions<T> format(Bitmap.CompressFormat format) {
        this.options.format = format;
        return this;
    }

    public CompressOptions<T> maxWidth(int size) {
        this.options.width = size;
        return this;
    }

    public CompressOptions<T> maxHeight(int size) {
        this.options.height = size;
        return this;
    }

    public CompressOptions<T> quality(int quality) {
        this.options.quality = quality;
        return this;
    }

    /**
     * The value of file max size
     *
     * @param size The value of file size, unit KB
     */
    public CompressOptions<T> maxSize(int size) {
        this.options.size = size;
        return this;
    }

    public CompressOptions<T> average(boolean average) {
        this.options.average = average;
        return this;
    }
}
