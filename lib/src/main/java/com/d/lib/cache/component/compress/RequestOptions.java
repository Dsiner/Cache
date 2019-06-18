package com.d.lib.cache.component.compress;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * RequestOptions
 * Created by D on 2018/12/20.
 **/
public class RequestOptions<T> extends com.d.lib.cache.base.RequestOptions<T> {
    InputStreamProvider provider;

    final BitmapOptions options = new BitmapOptions();
    int leastCompressSize;
    String path;
    String name;

    @Override
    public RequestOptions<T> placeholder(@Nullable T placeholder) {
        return (RequestOptions<T>) super.placeholder(placeholder);
    }

    @Override
    public RequestOptions<T> error(@Nullable T error) {
        return (RequestOptions<T>) super.error(error);
    }

    /**
     * Do not compress when the origin image file size less than one value
     *
     * @param size The value of file size, unit KB, default 100K
     */
    public RequestOptions<T> ignoreBy(int size) {
        this.leastCompressSize = size;
        return this;
    }

    public RequestOptions<T> setPath(String path, String name) {
        this.path = path;
        this.name = name;
        return this;
    }

    public RequestOptions<T> setFormat(Bitmap.CompressFormat format) {
        this.options.format = format;
        return this;
    }

    public RequestOptions<T> setMaxWidth(int size) {
        this.options.width = size;
        return this;
    }

    public RequestOptions<T> setMaxHeight(int size) {
        this.options.height = size;
        return this;
    }

    public RequestOptions<T> setQuality(int quality) {
        this.options.quality = quality;
        return this;
    }

    /**
     * The value of file max size
     *
     * @param size The value of file size, unit KB
     */
    public RequestOptions<T> setMaxSize(int size) {
        this.options.size = size;
        return this;
    }
}
