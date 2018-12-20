package com.d.lib.cache.component.compress;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RequestOptions
 * Created by D on 2018/12/20.
 **/
public class RequestOptions {
    public InputStreamProvider provider;

    public int leastCompressSize;
    public boolean focusAlpha;
    public Drawable placeHolder;
    public Drawable error;

    /**
     * do not compress when the origin image file size less than one value
     *
     * @param size the value of file size, unit KB, default 100K
     */
    public RequestOptions ignoreBy(int size) {
        this.leastCompressSize = size;
        return this;
    }

    /**
     * Do I need to keep the image's alpha channel
     *
     * @param focusAlpha <p> true - to keep alpha channel, the compress speed will be slow. </p>
     *                   <p> false - don't keep alpha channel, it might have a black background.</p>
     */
    public RequestOptions setFocusAlpha(boolean focusAlpha) {
        this.focusAlpha = focusAlpha;
        return this;
    }

    /**
     * Returns a {@link RequestOptions} object with {@link #placeholder(Drawable)} set.
     */
    @NonNull
    @CheckResult
    public RequestOptions placeholder(@Nullable Drawable drawable) {
        this.placeHolder = drawable;
        return this;
    }

    @NonNull
    @CheckResult
    public RequestOptions error(@Nullable Drawable drawable) {
        this.error = drawable;
        return this;
    }

    public String load(@NonNull final InputStream inputStream) {
        provider = new InputStreamProvider() {
            @Override
            public InputStream open() throws IOException {
                return inputStream;
            }

            @Override
            public String getPath() {
                return "" + inputStream.hashCode();
            }
        };
        return "" + inputStream.hashCode();
    }

    public String load(@NonNull final String string) {
        provider = new InputStreamProvider() {
            @Override
            public InputStream open() throws IOException {
                return new FileInputStream(string);
            }

            @Override
            public String getPath() {
                return string;
            }
        };
        return string;
    }

    public String load(final Context context, @NonNull final Uri uri) {
        provider = new InputStreamProvider() {
            @Override
            public InputStream open() throws IOException {
                return context.getContentResolver().openInputStream(uri);
            }

            @Override
            public String getPath() {
                return uri.getPath();
            }
        };
        return uri.getPath();
    }

    public String load(@NonNull final File file) {
        provider = new InputStreamProvider() {
            @Override
            public InputStream open() throws IOException {
                return new FileInputStream(file);
            }

            @Override
            public String getPath() {
                return file.getAbsolutePath();
            }
        };
        return file.getAbsolutePath();
    }
}
