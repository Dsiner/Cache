package com.d.cache;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.cache.compress.CompressActivity;
import com.d.cache.view.VideoPreView;
import com.d.cache.view.VoiceView;
import com.d.lib.cache.base.RequestOptions;
import com.d.lib.cache.component.duration.DurationCache;
import com.d.lib.cache.component.frame.FrameBean;
import com.d.lib.cache.component.frame.FrameCache;
import com.d.lib.cache.component.image.ImageCache;
import com.d.lib.cache.utils.threadpool.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String videoUrl = "http://vpls.cdn.videojj.com/scene/video02_720p.mp4";
    private final String voiceUrl = "http://exploiter.oss-cn-beijing.aliyuncs.com/audio/298/5Y2W54Gr5p%2B055qE5bCP5aWz5a2p_chinesestory_1484589316_1.mp3";

    private Context mContext;
    private VideoPreView vpvPreview;
    private ImageView ivPreview;
    private VoiceView vvDuration;
    private TextView tvDuration;
    private ImageView ivImage;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_frame:
                FrameCache.with(getApplicationContext())
                        .load(videoUrl)
                        .apply(new RequestOptions<FrameBean>()
                                .placeholder(new FrameBean(ContextCompat.getDrawable(mContext, R.color.colorAccent), null, null)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.mainThread())
                        .into(vpvPreview);
                FrameCache.with(getApplicationContext())
                        .load(videoUrl)
                        .apply(new RequestOptions<FrameBean>()
                                .placeholder(new FrameBean(ContextCompat.getDrawable(mContext, R.color.colorAccent), null, null)))
                        .into(ivPreview);
                break;

            case R.id.btn_duration:
                DurationCache.with(getApplicationContext())
                        .load(voiceUrl)
                        .apply(new RequestOptions<Long>()
                                .placeholder(0L))
                        .into(vvDuration);

                DurationCache.with(getApplicationContext())
                        .load(voiceUrl)
                        .apply(new RequestOptions<Long>()
                                .placeholder(0L))
                        .into(tvDuration);
                break;

            case R.id.btn_image:
                final String url1 = "https://www.baidu.com/img/bd_logo1.png";
                final String url2 = "http://img2.imgtn.bdimg.com/it/u=764856423,3994964277&fm=26&gp=0.jpg";
                ImageCache.with(getApplicationContext())
                        .load(url1)
                        .apply(new RequestOptions<Bitmap>())
                        .into(ivImage);
                break;

            case R.id.btn_compress:
                startActivity(new Intent(MainActivity.this, CompressActivity.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        bindView();
    }

    private void bindView() {
        vpvPreview = (VideoPreView) findViewById(R.id.vpv_preview);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
        vvDuration = (VoiceView) findViewById(R.id.vv_duration);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        ivImage = (ImageView) findViewById(R.id.iv_image);

        findViewById(R.id.btn_frame).setOnClickListener(this);
        findViewById(R.id.btn_duration).setOnClickListener(this);
        findViewById(R.id.btn_image).setOnClickListener(this);
        findViewById(R.id.btn_compress).setOnClickListener(this);
    }
}
