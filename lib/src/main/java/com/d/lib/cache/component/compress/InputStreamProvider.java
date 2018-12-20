package com.d.lib.cache.component.compress;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 通过此接口获取输入流，以兼容文件、FileProvider方式获取到的图片
 * <p>
 * Get the input stream through this interface, and obtain the picture using compatible files and FileProvider
 */
public abstract class InputStreamProvider {

    public abstract InputStream open() throws IOException;

    public abstract String getPath();

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof InputStreamProvider) {
            return TextUtils.equals(getPath(), ((InputStreamProvider) obj).getPath());
        }
        return super.equals(obj);
    }
}
