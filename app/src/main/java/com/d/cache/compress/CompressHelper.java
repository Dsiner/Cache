package com.d.cache.compress;

import android.content.Context;

import com.d.cache.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * CompressHelper
 * Created by D on 2018/12/21.
 **/
public class CompressHelper {

    public static String getPath(Context context) {
        checkFile(context, App.PIC_NAME);
        return getFile(App.PIC_NAME).getAbsolutePath();
    }

    private static boolean checkFile(Context context, String name) {
        InputStream in = null;
        FileOutputStream out = null;
        File file = getFile(name);
        boolean success = false;
        if (!file.exists()) {
            try {
                // Copy from the assets directory
                in = context.getAssets().open(name);
                out = new FileOutputStream(file);
                int length;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) out.close();
                    if (in != null) in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    private static File getFile(String name) {
        File dir = new File(App.FILE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir.getPath() + File.separator + name);
    }
}
