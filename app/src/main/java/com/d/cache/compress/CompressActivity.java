package com.d.cache.compress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.d.cache.R;
import com.d.lib.cache.component.compress.CompressCache;
import com.d.lib.cache.component.compress.CompressOptions;
import com.d.lib.cache.component.compress.UriUtil;

public class CompressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivOriginal;
    private ImageView ivCompress;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_image:
                final Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;

            case R.id.btn_compress:
                String path = (String) ivOriginal.getTag();
                if (TextUtils.isEmpty(path)) {
                    toast("Please choose image firstly!");
                    return;
                }
                ivOriginal.setVisibility(View.VISIBLE);
                ivCompress.setVisibility(View.VISIBLE);
                ivCompress.setImageDrawable(null);
                CompressCache.clear(ivCompress);
                CompressCache.with(getApplicationContext())
                        .load(path)
                        .apply(new CompressOptions<Bitmap>()
                                .setMaxWidth(1024)
                                .setMaxHeight(1024)
                                .setFormat(Bitmap.CompressFormat.JPEG)
                                .setMaxSize(30)
                                .setAverage(true))
                        .into(ivCompress);

//                CompressCache.with(getApplicationContext())
//                        .asFile()
//                        .load(path)
//                        .apply(new CompressOptions<File>()
//                                .setFormat(Bitmap.CompressFormat.JPEG)
//                                .ignoreBy(200))
//                        .listener(new CacheListener<File>() {
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
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress);
        bindView();
    }

    private void bindView() {
        ivOriginal = (ImageView) findViewById(R.id.iv_original);
        ivCompress = (ImageView) findViewById(R.id.iv_compress);

        findViewById(R.id.btn_choose_image).setOnClickListener(this);
        findViewById(R.id.btn_compress).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                toast("Failed to open picture!");
                return;
            }
            try {
                String path = UriUtil.getPath(this, data.getData());
                ivOriginal.setVisibility(View.VISIBLE);
                ivCompress.setVisibility(View.GONE);
                ivOriginal.setTag(path);
                ivOriginal.setImageBitmap(BitmapFactory.decodeFile(path));
            } catch (Throwable e) {
                toast("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
