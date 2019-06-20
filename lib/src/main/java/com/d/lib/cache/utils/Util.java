package com.d.lib.cache.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by D on 2017/10/23.
 */
public class Util {

    @Nullable
    public static File getFileFromMediaUri(Context context, Uri uri) {
        if (uri.getScheme() == null) {
            return null;
        }
        if (uri.getScheme().compareTo("content") == 0) {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex("_data"));
                cursor.close();
                if (filePath != null) {
                    return new File(filePath);
                }
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            return new File(uri.toString().replace("file://", ""));
        }
        return null;
    }
}
