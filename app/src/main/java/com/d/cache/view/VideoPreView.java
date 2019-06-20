package com.d.cache.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.cache.R;
import com.d.lib.cache.component.frame.IFrame;

/**
 * Created by D on 2017/10/23.
 */
public class VideoPreView extends FrameLayout implements IFrame {
    private ImageView ivPreview;
    private TextView tvDuration;

    public VideoPreView(Context context) {
        this(context, null);
    }

    public VideoPreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View root = LayoutInflater.from(context).inflate(R.layout.view_video_pre, this);
        ivPreview = (ImageView) root.findViewById(R.id.iv_pre_preview);
        tvDuration = (TextView) root.findViewById(R.id.tv_pre_duration);
    }

    @Override
    public void setFrame(Drawable drawable, Long duration) {
        ivPreview.setImageDrawable(drawable);
        tvDuration.setText("Duration：" + (duration != null ? formatTime(duration) : "--:--"));
    }

    /**
     * Format time, convert milliseconds into seconds: (00:00) format
     * String.format("%02d:%02d", time / 1000 / 60, time / 1000 % 60)
     */
    public static String formatTime(long time) {
        StringBuilder sb;
        long min = time / 1000 / 60;
        long sec = time / 1000 % 60;
        if (min / 10 < 1) {
            sb = new StringBuilder("0");
            sb.append(String.valueOf(min));
        } else {
            sb = new StringBuilder(String.valueOf(min));
        }
        sb.append(":");
        if (sec / 10 < 1) {
            sb.append("0");
        }
        sb.append(String.valueOf(sec));
        return sb.toString();
    }
}
