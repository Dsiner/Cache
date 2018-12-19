package com.d.cache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.cache.view.VideoPreView;
import com.d.cache.view.VoiceView;
import com.d.lib.cache.DurationCache;
import com.d.lib.cache.FrameCache;
import com.d.lib.cache.ImageCache;

public class MainActivity extends AppCompatActivity {
    private final String videoUrl = "http://vpls.cdn.videojj.com/scene/video02_720p.mp4";
    private final String voiceUrl = "http://exploiter.oss-cn-beijing.aliyuncs.com/audio/298/5Y2W54Gr5p%2B055qE5bCP5aWz5a2p_chinesestory_1484589316_1.mp3";

    private VideoPreView vpvPreview;
    private ImageView ivPreview;
    private VoiceView vvDuraion;
    private TextView tvDuraion;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTest();
    }

    private void initView() {
        vpvPreview = (VideoPreView) findViewById(R.id.vpv_preview);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
        vvDuraion = (VoiceView) findViewById(R.id.vv_duration);
        tvDuraion = (TextView) findViewById(R.id.tv_duration);
        ivImage = (ImageView) findViewById(R.id.iv_image);
    }

    private void initTest() {
        findViewById(R.id.btn_frame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameCache.with(getApplicationContext()).load(videoUrl).placeholder(R.color.colorAccent).into(vpvPreview);
                FrameCache.with(getApplicationContext()).load(videoUrl).placeholder(R.color.colorAccent).into(ivPreview);
            }
        });

        findViewById(R.id.btn_duration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DurationCache.with(getApplicationContext()).load(voiceUrl).placeholder(0L).into(vvDuraion);
                DurationCache.with(getApplicationContext()).load(voiceUrl).placeholder(0L).into(tvDuraion);
            }
        });

        findViewById(R.id.btn_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.baidu.com/img/bd_logo1.png";
                String url1 = "http://img2.imgtn.bdimg.com/it/u=764856423,3994964277&fm=26&gp=0.jpg";
                ImageCache.with(getApplicationContext()).load(url).into(ivImage);
            }
        });
    }
}
