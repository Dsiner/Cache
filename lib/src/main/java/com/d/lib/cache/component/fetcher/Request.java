package com.d.lib.cache.component.fetcher;

import android.graphics.Bitmap;

/**
 * Request
 * Created by D on 2018/12/19.
 **/
public class Request {
    /**
     * Target image width for resizing.
     */
    public final int targetWidth;
    /**
     * Target image height for resizing.
     */
    public final int targetHeight;
    /**
     * True if image should be decoded with inPurgeable and inInputShareable.
     */
    public final boolean purgeable;
    /**
     * Target image config for decoding.
     */
    public final Bitmap.Config config;
    public final boolean centerInside;

    public Request() {
        this(0, 0, false, false, Bitmap.Config.RGB_565);
    }

    public Request(int targetWidth, int targetHeight, boolean centerInside,
                   boolean purgeable, Bitmap.Config config) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.centerInside = centerInside;
        this.purgeable = purgeable;
        this.config = config;
    }

    public boolean hasSize() {
        return targetWidth != 0 || targetHeight != 0;
    }
}
