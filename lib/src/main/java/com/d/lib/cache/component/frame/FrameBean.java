package com.d.lib.cache.component.frame;

import android.graphics.drawable.Drawable;

public class FrameBean {
    public Drawable drawable;
    public Long duration;
    public String thumb;

    public FrameBean(Drawable drawable, Long duration, String thumb) {
        this.drawable = drawable;
        this.duration = duration;
        this.thumb = thumb;
    }
}
