package com.d.lib.cache.listener;

import android.graphics.drawable.Drawable;

/**
 * Classes that implement DurationView must extends View
 * Created by D on 2017/10/19.
 */
public interface FrameView {
    void setFrame(Drawable drawable, Long duration);
}
