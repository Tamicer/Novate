package com.tamic.novate.download;


public interface UpLoadCallback {
    void onProgress(Object tag, int progress, long speed, boolean done);
}
