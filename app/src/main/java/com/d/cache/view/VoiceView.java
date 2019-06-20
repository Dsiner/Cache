package com.d.cache.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.d.lib.cache.component.duration.DurationCache;
import com.d.lib.cache.component.duration.IDuration;
import com.d.lib.cache.utils.Util;

/**
 * Created by D on 2017/10/23.
 */
@SuppressLint("AppCompatCustomView")
public class VoiceView extends TextView implements IDuration {
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
        setText("Duration：" + DurationCache.formatTime(duration));
    }
}
