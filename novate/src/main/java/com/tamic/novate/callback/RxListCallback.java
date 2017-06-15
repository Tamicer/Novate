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

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tamic.novate.NovateResponse;
import com.tamic.novate.cookie.SerializableCookie;
import com.tamic.novate.util.ReflectionUtil;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;


/**
 * Created by liuyongkui726 on 2017-06-15.
 */

public abstract class RxListCallback<T> extends RxGenericsCallback<T, ResponseBody> {

    private Type collectionType;

    public RxListCallback() {
    }

    /**
     * you need Type listType = new TypeToken<List<T>>() {}.getType();
     * @param type
     */
    public RxListCallback(Type type) {
        this.collectionType =  type;
    }

    @Override
    public T onHandleResponse(ResponseBody response) throws Exception {
        if (collectionType == null) {
            collectionType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        String jstring = new String(response.bytes());
        Log.d("Novate", jstring);
        return transform(jstring, null);
    }


    public T transform(String response, final Class classOfT) throws ClassCastException {
        JSONObject jsonObject = null;
        Log.e("xxx", response);
        try {
            jsonObject = new JSONObject(response);
            code = jsonObject.optInt("code");
            msg = jsonObject.optString("msg");
            if (TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("error");
            }

            if(TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("message");
            }

            dataStr = jsonObject.optJSONArray("data").toString();
            if (dataStr.isEmpty()) {
                dataStr = jsonObject.optJSONArray("result").toString();
            }

            dataResponse = new Gson().fromJson(dataStr, collectionType);

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return dataResponse;
    }

}
