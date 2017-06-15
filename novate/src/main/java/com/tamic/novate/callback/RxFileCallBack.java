/*
 *    Copyright (C) 2017 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.tamic.novate.callback;



import com.tamic.novate.Throwable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * RxFileCallBack File回调
 * Created by Tamic on 2017-05-02.
 */
public abstract class RxFileCallBack extends ResponseCallback<File, ResponseBody> {
    /*** 文件夹路径*/
    private String destFileDir;
    /*** 文件名*/
    private String destFileName;

    public RxFileCallBack() {
        this(null, null);
    }

    public RxFileCallBack(String destFileName) {
        this(null, destFileName);
    }

    public RxFileCallBack(String fileDir, String fileName) {
        this.destFileDir = fileDir;
        this.destFileName = fileName;
    }

    @Override
    public File onHandleResponse(ResponseBody response) throws Exception {
       return transform(response);
    }

    public File transform(ResponseBody response) throws Exception {
        return onNextFile(response);
    }

    public File onNextFile(ResponseBody response) throws Exception {

        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.byteStream();
            final long total = response.contentLength();
            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                onProgress(tag, finalSum * 1.0f / total, sum, total);

            }
            fos.flush();

            return file;

        } finally {
            try {
                response.close();
                if (is != null) is.close();
            } catch (IOException e) {

                onError(tag, new Throwable(e, -100, "file write io Exception"));
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {

                onError(tag, new Throwable(e, -100, "file write io Exception"));
            }
        }
    }

    @Override
    public void onNext(Object tag, okhttp3.Call call, File response) {
       onNext(tag, response);
    }

    public abstract void onNext(Object tag, File file);

    public abstract void onProgress(Object tag, float progress, long downloaded, long total);

}
