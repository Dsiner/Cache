package com.d.lib.cache.base;

/**
 * Created by D on 2017/10/19.
 */
public class CacheException extends Exception {
    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}
