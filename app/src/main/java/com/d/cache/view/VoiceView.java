package com.d.cache.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.d.lib.cache.listener.DurationView;

/**
 * Created by D on 2017/10/23.
 */
public class VoiceView extends TextView implements DurationView {
    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDuration(Long duration) {
        setText("时长：" + duration + "ms");
    }
}
