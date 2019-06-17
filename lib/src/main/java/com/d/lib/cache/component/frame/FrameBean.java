package com.d.lib.cache.component.frame;

import android.graphics.drawable.Drawable;

public class FrameBean {
    public String thumb;
    public Drawable drawable;
    public Long duration;

    public static FrameBean create(String thumb, Drawable drawable, Long duration) {
        FrameBean bean = new FrameBean();
        bean.thumb = thumb;
        bean.drawable = drawable;
        bean.duration = duration;
        return bean;
    }
}
