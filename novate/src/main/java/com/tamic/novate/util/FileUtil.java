package com.tamic.novate.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final String PATH_DOCUMENT = "document";
    private static final String PATH_TREE = "tree";


    private static String getFilePathFromUri(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * get SDPath, if no sdcard, return null
     * 
     * @return
     */
    public static String getSDPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            Log.e("SDCard", "no sdcard found!");
            return null;
        }
    }

    public static String getBasePath(Context context) {
        if (null == getSDPath()) {
            return context.getCacheDir().getAbsolutePath();
        }

        File file = new File(getSDPath() + File.separator + context.getPackageName());
        if (!file.exists()) {
            file.mkdir();
        }

        return file.getAbsolutePath();
    }



    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /** loadFromAssets
     * @param context
     * @param fileName
     * @return
     */
    public static String loadFromAssets(Context context, String fileName) {
        BufferedReader reader = null;
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(in));

            char[] buf = new char[1024];
            int count = 0;
            StringBuffer sb = new StringBuffer(in.available());
            while ((count = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, count);
                sb.append(readData);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(reader);
        }

        return "";
    }

    /**
     * 删除文件
     * 
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleteFiles(ArrayList<String> filePaths) {
        for (int i = 0; i < filePaths.size(); i++) {
            deleteFile(filePaths.get(i));
        }
    }
}
