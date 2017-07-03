/*
 *    Copyright (C) 2016 Tamic
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
package com.tamic.novate.response;


import com.tamic.novate.callback.ResponseCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * NovateResponseBody
 * Created by Tamic on 2017-06-14.
 */

public class NovateResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ResponseCallback progressListener;
    private BufferedSource bufferedSource;
    private long previousTime;
    private long frequency = (long) 0.5;

    public NovateResponseBody(ResponseBody responseBody, ResponseCallback progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }


    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * @param source
     * @return
     */
    private Source source(Source source) {
        previousTime = System.currentTimeMillis();
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            int updateCount = 0;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                final long fbytesRead = contentLength();
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                long totalTime = (System.currentTimeMillis() - previousTime) / 1000;
                if (totalTime == 0) {
                    totalTime += 1;
                }
                long networkSpeed =  totalBytesRead / totalTime;
                int progress = (int) (totalBytesRead * 100 / fbytesRead);
                final long fnetworkSpeed = networkSpeed;
                final int fprogress = progress;

                if (updateCount == 0 || progress >= updateCount) {
                    updateCount += frequency;
                    if (progressListener != null) {
                        progressListener.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                progressListener.onProgress(progressListener.getTag(), fprogress, fnetworkSpeed, totalBytesRead, fbytesRead);
                            }
                        });
                    }
                }
               // progressListener.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
