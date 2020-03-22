package com.d.cache.compress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d.cache.R;
import com.d.lib.cache.base.CacheListener;
import com.d.lib.cache.base.DiskCacheStrategies;
import com.d.lib.cache.component.compress.CompressCache;
import com.d.lib.cache.component.compress.CompressOptions;
import com.d.lib.cache.component.compress.Engine;
import com.d.lib.cache.component.compress.UriUtil;

import java.io.File;

public class CompressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView iv_original, iv_compress;
    private TextView tv_original_info, tv_compress_info;

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
                compress((String) iv_original.getTag());
                break;
        }
    }

    private void compress(String path) {
        if (TextUtils.isEmpty(path)) {
            toast("Please choose image firstly!");
            return;
        }
        iv_original.setVisibility(View.VISIBLE);
        iv_compress.setVisibility(View.VISIBLE);
        iv_compress.setImageDrawable(null);
        CompressCache.clear(iv_compress);
//        CompressCache.with(getApplicationContext())
//                .load(path)
//                .into(iv_compress);

        CompressCache.with(getApplicationContext())
                .asFile()
                .load(path)
                .apply(new CompressOptions<File>()
//                        .strategy(new LimitStrategy(1024, 1024, true))
                        .config(Bitmap.Config.ARGB_8888)
                        .format(Bitmap.CompressFormat.JPEG)
                        .quality(95)
                        .maxSize(375)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategies.NONE))
                .listener(new CacheListener<File>() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d("Cache", "CompressCache onSuccess--> " + file.getAbsolutePath());
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Log.d("Cache", "CompressCache bitmap info--> " + getInfo(bitmap));
                        }

                        BitmapFactory.Options options = Engine.decodeStream(file);
                        if (options != null && options.outWidth * options.outHeight < 15 * 1024 * 1024) {
                            iv_compress.setImageBitmap(bitmap);
                        }

                        tv_compress_info.setText(getInfo(bitmap));
                        if (file.exists() && file.isFile()) {
                            MediaScannerConnection.scanFile(getApplicationContext(),
                                    new String[]{file.getAbsolutePath()}, null, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Cache", "CompressCache onError--> " + e.toString());
                        tv_compress_info.setText("Failed to compress picture data!");
                    }
                });
    }

    private String getInfo(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "byteCount: " + bitmap.getByteCount()
                    + "\nAllocationByteCount: " + bitmap.getAllocationByteCount()
                    + "\nwidth: " + bitmap.getWidth()
                    + " height: " + bitmap.getHeight();
        }
        return "";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress);
        bindView();
    }

    private void bindView() {
        iv_original = (ImageView) findViewById(R.id.iv_original);
        iv_compress = (ImageView) findViewById(R.id.iv_compress);
        tv_original_info = (TextView) findViewById(R.id.tv_original_info);
        tv_compress_info = (TextView) findViewById(R.id.tv_compress_info);

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
                iv_original.setVisibility(View.VISIBLE);
                iv_compress.setVisibility(View.GONE);
                iv_original.setTag(path);

                if (new File(path).length() > 6 * 1024 * 1024) {
                    throw new Exception("Failed to read picture data!");
                }
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                tv_original_info.setText(getInfo(bitmap));
                tv_compress_info.setText("Ready to compress");
                iv_original.setImageBitmap(bitmap);
            } catch (Throwable e) {
                toast("Failed to read picture data!");
                e.printStackTrace();
                tv_original_info.setText("Failed to read picture data!");
                tv_compress_info.setText("Compress...");
                compress((String) iv_original.getTag());
            }
        }
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
