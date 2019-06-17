package com.d.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.d.cache.compress.CompressHelper;
import com.d.cache.view.VideoPreView;
import com.d.cache.view.VoiceView;
import com.d.lib.cache.component.compress.CompressCache;
import com.d.lib.cache.component.duration.DurationCache;
import com.d.lib.cache.component.frame.FrameCache;
import com.d.lib.cache.component.image.ImageCache;
import com.d.lib.cache.base.RequestOptions;
import com.d.lib.cache.utils.threadpool.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final String videoUrl = "http://vpls.cdn.videojj.com/scene/video02_720p.mp4";
    private final String voiceUrl = "http://exploiter.oss-cn-beijing.aliyuncs.com/audio/298/5Y2W54Gr5p%2B055qE5bCP5aWz5a2p_chinesestory_1484589316_1.mp3";

    private Context mContext;
    private VideoPreView vpvPreview;
    private ImageView ivPreview;
    private VoiceView vvDuraion;
    private TextView tvDuraion;
    private ImageView ivImage;
    private ImageView ivCompress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
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
        ivCompress = (ImageView) findViewById(R.id.iv_compress);
    }

    private void initTest() {
        findViewById(R.id.btn_frame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameCache.with(getApplicationContext())
                        .load(videoUrl)
                        .apply(new RequestOptions<Drawable>()
                                .placeholder(ContextCompat.getDrawable(mContext, R.color.colorAccent)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.mainThread())
                        .into(vpvPreview);
                FrameCache.with(getApplicationContext())
                        .load(videoUrl)
                        .apply(new RequestOptions<Drawable>()
                                .placeholder(ContextCompat.getDrawable(mContext, R.color.colorAccent)))
                        .into(ivPreview);
            }
        });

        findViewById(R.id.btn_duration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DurationCache.with(getApplicationContext())
                        .load(voiceUrl)
                        .apply(new RequestOptions<Long>()
                                .placeholder(0L))
                        .into(vvDuraion);

                DurationCache.with(getApplicationContext())
                        .load(voiceUrl)
                        .apply(new RequestOptions<Long>()
                                .placeholder(0L))
                        .into(tvDuraion);
            }
        });

        findViewById(R.id.btn_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.baidu.com/img/bd_logo1.png";
                String url1 = "http://img2.imgtn.bdimg.com/it/u=764856423,3994964277&fm=26&gp=0.jpg";
                ImageCache.with(getApplicationContext())
                        .load(url)
                        .apply(new RequestOptions<Bitmap>())
                        .into(ivImage);
            }
        });

        findViewById(R.id.btn_compress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompressCache.with(getApplicationContext())
                        .load(CompressHelper.getPath(mContext))
                        .apply(new com.d.lib.cache.component.compress.RequestOptions()
                                .setFocusAlpha(false)
                                .ignoreBy(200))
                        .into(ivCompress);

//                CompressCache.with(getApplicationContext())
//                        .load(CompressHelper.getPath(mContext))
//                        .apply(new com.d.lib.cache.component.compress.RequestOptions()
//                                .setFocusAlpha(false)
//                                .ignoreBy(200))
//                        .file(new CacheListener<File>() {
//                            @Override
//                            public void onLoading() {
//
//                            }
//
//                            @Override
//                            public void onSuccess(File result) {
//                                Log.d("Cache", "CompressCache onSuccess--> " + result.getAbsolutePath());
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.d("Cache", "CompressCache onError--> " + e.toString());
//                            }
//                        });
            }
        });
    }
}
