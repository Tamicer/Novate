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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tamic.novate.Throwable;
import com.tamic.novate.util.FileUtil;
import com.tamic.novate.util.LogWraper;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by liuyongkui726 on 2017-06-22.
 */

public abstract  class RxDownCallback extends ResponseCallback<File, ResponseBody> {

    /*** 文件夹路径*/
    private String destFileDir;
    /*** 文件名*/
    private String destFileName;
    /*** Context */
    private Context context;
    /*** BufferedSink */
    protected BufferedSink sink;
    /** RANGE */
    private final String RANGE = "Range";
    /** TAG */
    private final String TAG = "DownLoadService";
    /**上次刷新UI时间*/
    protected long mLastRefreshTime;
    /**本次下载字节数*/
    protected long mBytesThistime;
    /**开始下载时间*/
    long mStarttime = 0;
    /**刷新UI时间间隔 */
    protected static final int REFRESH_INTEVAL = 1000;


    @Override
    public File onHandleResponse(ResponseBody response) throws Exception {
        return transform(response);
    }

    public File transform(ResponseBody response) throws Exception {
        return onNextFile(response, true);
    }


    public File onNextFile(ResponseBody response, boolean isMax) throws EOFException {

        if (TextUtils.isEmpty(destFileDir)) {
            destFileDir = FileUtil.getBasePath(context);
        }

        //if (TextUtils.isEmpty())

        try {
            long totalRead = 0;
            final long fileSize = response.contentLength();
            BufferedSource source = response.source();

            final File file =  FileUtil.createDownloadFile(destFileDir, destFileName);
            sink = Okio.buffer(Okio.sink(file));
            long read = 0;
            while ((read = source.read(sink.buffer(), 2048)) != -1) {
                totalRead += read;
                mBytesThistime += totalRead;
                long currenttime = System.currentTimeMillis();
                long speed = 0;
                if (currenttime > mStarttime) {
                    speed = totalRead * 1000 / (currenttime - mStarttime);
                }
                LogWraper.d(TAG, "fileSize: " + read);
                final int progress = (int) (read * 100 / fileSize);
                long currentTime = System.currentTimeMillis();
                if (currentTime - mLastRefreshTime >= REFRESH_INTEVAL) {
                    final long finalTotalRead = totalRead;
                    final long finalSpeed = speed;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(tag, progress, finalSpeed, finalTotalRead, fileSize);
                        }
                    });
                }
                if (totalRead > 0
                        && totalRead> fileSize * (1.5)) {
                    sink.writeAll(source);
                    sink.flush();
                    sink.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onError(tag, new Throwable(null, -100, "超过100%。总大小：" + fileSize + "，已下载：" + fileSize));
                        }
                    });
                    break;
                }

            }
            sink.writeAll(source);
            sink.flush();
            sink.close();
            if (totalRead == fileSize) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onNext(tag, file);
                    }
                });


            } else {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, new Throwable(null, -100, "超过100%。总大小：" + fileSize + "，已下载：" + fileSize));
                    }
                });

                return null;
            }

        } catch (final IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("No space left on device")) {
                // sd卡满
                LogWraper.d(TAG, "SD卡满了");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, new Throwable(e, -100, "SD卡满了"));
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, new Throwable(e, -100, e.getMessage()));
                    }
                });

            }
            return null;

        } finally {
            close();
        }

        return null;
    }

    @Override
    public void onRelease() {
        super.onRelease();
        close();
    }

    /**
     * close sink
     */
    protected void close() {
        if (sink != null) {
            try {
                sink.close();
                sink = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "sink  is already closed!");
            }
        }

    }


    @Override
    public void onNext(Object tag, okhttp3.Call call, File response) {
        onNext(tag, response);
    }

    public abstract void onNext(Object tag, File file);

    public abstract void onProgress(Object tag, int progress, long speed, long downloaded, long total);

}
