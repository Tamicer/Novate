package com.tamic.novate;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.tamic.novate.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by Tamic on 2016-07-11.
 */
public class NovateDownLoadManager {

    private DownLoadCallBack callBack;

    public static final String TAG = "Novate:DownLoadManager";

    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";

    private static String PNG_CONTENTTYPE = "image/png";

    private static String JPG_CONTENTTYPE = "image/jpg";

    private static String fileSuffix = "";

    private Handler handler;

    public static boolean isDownLoading = false;

    public static boolean isCancel = false;

    public NovateDownLoadManager(DownLoadCallBack callBack) {
        this.callBack = callBack;
        handler = new Handler(Looper.getMainLooper());
    }

    private static NovateDownLoadManager sInstance;

    /**
     * DownLoadManager getInstance
     */
    public static synchronized NovateDownLoadManager getInstance(DownLoadCallBack callBack) {
        if (sInstance == null) {
            sInstance = new NovateDownLoadManager(callBack);
        }
        return sInstance;
    }

    public boolean writeResponseBodyToDisk(final String key, String path, String name, Context context, ResponseBody body) {

        Log.d(TAG, "contentType:>>>>" + body.contentType().toString());

        if (!TextUtils.isEmpty(name)) {
            String type = "";
            if (!name.contains(".")) {
                type = body.contentType().toString();
                if (type.equals(APK_CONTENTTYPE)) {
                    fileSuffix = ".apk";
                } else if (type.equals(PNG_CONTENTTYPE)) {
                    fileSuffix = ".png";
                } else if (type.equals(JPG_CONTENTTYPE)) {
                    fileSuffix = ".jpg";
                } else {
                    fileSuffix = body.contentType().subtype();
                }
                name = name + fileSuffix;
            }
        } else {
            name = System.currentTimeMillis() + fileSuffix;
        }

        if (path == null) {
            path = context.getExternalFilesDir(null) + File.separator +"DownLoads" + File.separator + name;
        }
        Log.d(TAG, "path:-->" + path);
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                Log.d(TAG, "file length: " + fileSize);
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1 || isCancel) {
                        break;
                    }

                    isDownLoading = true;
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    if (callBack != null) {
                        if (callBack != null) {
                            final long finalFileSizeDownloaded = fileSizeDownloaded;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onProgress(key, finalFileSizeDownloaded, fileSize);
                                }
                            }, 200);
                        }
                    }
                }

                outputStream.flush();
                Log.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);

                isDownLoading = false;


                if (callBack != null) {
                    final String finalName = name;
                    final String finalPath = path;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            callBack.onSucess(key, finalPath, finalName, fileSize);

                        }
                    });
                    Log.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                    Log.d(TAG, "file downloaded: is sucess");
                }

                return true;
            } catch (IOException e) {
                finalonError(e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            finalonError(e);
            return false;
        }
    }

    private void finalonError(final Exception e) {

        if (callBack == null) {
            return;
        }

        if (Utils.checkMain()) {
            callBack.onError(new Throwable(e, 100));
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(new Throwable(e, 100));
                }
            });
        }
    }



}
