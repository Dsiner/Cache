package com.d.lib.cache.base;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class DiskCacheStrategies {
    public static final int NONE = 0;
    public static final int RESOURCE = 1;

    @IntDef({NONE, RESOURCE})
    @Target({ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DiskCacheStrategy {

    }
}
