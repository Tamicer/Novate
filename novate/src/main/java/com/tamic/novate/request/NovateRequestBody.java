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
package com.tamic.novate.request;


import com.tamic.novate.download.UpLoadCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class NovateRequestBody extends RequestBody {

    private Object Tag;
    private long previousTime;
    protected RequestBody requestBody;
    protected UpLoadCallback callback;
    protected CountingSink countingSink;

    public NovateRequestBody(RequestBody requestBody, UpLoadCallback callback) {
        this(requestBody, callback, null);
    }


    public NovateRequestBody(RequestBody requestBody, UpLoadCallback callback, Object tag) {
        this.requestBody = requestBody;
        this.callback = callback;
        this.Tag = tag;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        previousTime = System.currentTimeMillis();
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);

        requestBody.writeTo(bufferedSink);

        bufferedSink.flush();

    }

    public Object getTag() {
        return Tag;
    }

    public NovateRequestBody setTag(Object tag) {
        Tag = tag;
        return this;
    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;
        long contentLength = 0L;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            if (contentLength == 0) {
                contentLength = contentLength();
            }
            bytesWritten += byteCount;
            if (callback != null) {
                long totalTime = (System.currentTimeMillis() - previousTime) / 1000;
                if (totalTime == 0) {
                    totalTime += 1;
                }
                long networkSpeed = bytesWritten / totalTime;
                int progress = (int) (bytesWritten * 100 / contentLength);
                boolean  complete = bytesWritten == contentLength;

                callback.onProgress(Tag == null? "": Tag, progress, networkSpeed, complete);
            }
        }
    }
}
