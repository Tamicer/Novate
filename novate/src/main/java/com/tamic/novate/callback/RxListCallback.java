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

    private String jsStr1 = "{\n" +
            "    \"data\": [\n" +
            "            {\n" +
            "                \"id\": 15,\n" +
            "                \"createDate\": 1496286231000,\n" +
            "                \"modifyDate\": 1496291243000,\n" +
            "                \"order\": 1,\n" +
            "                \"name\": \"推荐1\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 18,\n" +
            "                \"createDate\": 1496291263000,\n" +
            "                \"modifyDate\": 1496291263000,\n" +
            "                \"order\": 2,\n" +
            "                \"name\": \"新手2\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 19,\n" +
            "                \"createDate\": 1496291284000,\n" +
            "                \"modifyDate\": 1496291284000,\n" +
            "                \"order\": 3,\n" +
            "                \"name\": \"联系3\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 20,\n" +
            "                \"createDate\": 1496291300000,\n" +
            "                \"modifyDate\": 1496291300000,\n" +
            "                \"order\": 4,\n" +
            "                \"name\": \"热门4\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            }\n" +
            "        ],\n" +
            "    \"code\": 100,\n" +
            "    \"message\": \"success\"\n" +
            "}";

    private String jsStr2 = "{\n" +
            "    \"data\": {\n" +
            "        \"musicBookCategoryList\": [\n" +
            "            {\n" +
            "                \"id\": 15,\n" +
            "                \"createDate\": 1496286231000,\n" +
            "                \"modifyDate\": 1496291243000,\n" +
            "                \"order\": 1,\n" +
            "                \"name\": \"推荐1\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 18,\n" +
            "                \"createDate\": 1496291263000,\n" +
            "                \"modifyDate\": 1496291263000,\n" +
            "                \"order\": 2,\n" +
            "                \"name\": \"新手2\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 19,\n" +
            "                \"createDate\": 1496291284000,\n" +
            "                \"modifyDate\": 1496291284000,\n" +
            "                \"order\": 3,\n" +
            "                \"name\": \"联系3\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": 20,\n" +
            "                \"createDate\": 1496291300000,\n" +
            "                \"modifyDate\": 1496291300000,\n" +
            "                \"order\": 4,\n" +
            "                \"name\": \"热门4\",\n" +
            "                \"treePath\": \",13,\",\n" +
            "                \"grade\": 1\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"code\": 100,\n" +
            "    \"message\": \"success\"\n" +
            "}";

    @Override
    public T onHandleResponse(ResponseBody response) throws Exception {
        if (collectionType == null) {
            collectionType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        String jstring = new String(response.bytes());
        Log.d("Novate", jstring);
        return transform(jsStr1, null);
    }


    public T transform(String response, final Class classOfT) throws ClassCastException {
        JSONObject jsonObject = null;
        Log.e("xxx", response);
        try {
            jsonObject = new JSONObject(response);
            code = jsonObject.optInt("code");
            msg = jsonObject.optString("msg");
            /*if (dataStr.charAt(0) == '{') {
                dataStr = jsonObject.optJSONObject("data").toString();
                if (dataStr.isEmpty()) {
                    dataStr = jsonObject.optJSONObject("result").toString();
                }
                dataResponse = (T) new Gson().fromJson(dataStr, classOfT);
            } else if (dataStr.charAt(0) == '[') {

            }*/
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
