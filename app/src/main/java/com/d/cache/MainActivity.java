package com.d.cache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.d.cache.view.VideoPreView;
import com.d.cache.view.VoiceView;
import com.d.lib.cache.DurationCache;
import com.d.lib.cache.FrameCache;

public class MainActivity extends AppCompatActivity {
    private String testVideo = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private String testVoice = "http://m10.music.126.net/20171023155603/55eb2b8bbb0da5aa2778b0703cb9c14c/ymusic/5dd8/399b/973f/faa6c66c735437cb9ce1dd1e4333ab0b.mp3";
    private VideoPreView vpvPreview;
    private VoiceView vvDuraion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTest();
    }

    private void initView() {
        vpvPreview = (VideoPreView) findViewById(R.id.vpv_pre);
        vvDuraion = (VoiceView) findViewById(R.id.vv_duration);
    }

    private void initTest() {
        findViewById(R.id.btn_frame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameCache.with(getApplicationContext()).load(testVideo).placeholder(null).into(vpvPreview);
            }
        });
        findViewById(R.id.btn_duration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DurationCache.with(getApplicationContext()).load(testVoice).placeholder(0).into(vvDuraion);
            }
        });
    }
}
