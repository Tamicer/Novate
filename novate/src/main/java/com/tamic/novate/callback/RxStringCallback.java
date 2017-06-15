package com.tamic.novate.callback;




import android.util.Log;

import okhttp3.ResponseBody;

/**
 * RxStringCallback  字符串解析器
 *
 * Created by Tamic on 2017-5-30.
 * ink :https://github.com/Tamicer/Novate
 *
 */
public abstract class RxStringCallback extends ResponseCallback<String, ResponseBody> {

    @Override public String onHandleResponse(ResponseBody response) throws Exception {
        String  responseString = new String(response.bytes());
        Log.d("Novate", responseString);
        return responseString;
    }
}
