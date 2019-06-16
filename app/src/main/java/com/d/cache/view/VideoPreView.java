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
import com.d.lib.cache.utils.Util;

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
        tvDuration.setText("Duration：" + Util.formatTime(duration));
    }
}
