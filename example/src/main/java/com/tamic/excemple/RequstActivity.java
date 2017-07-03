package com.tamic.excemple;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tamic.excemple.model.MusicBookCategory;
import com.tamic.excemple.model.ResultModel;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxBitmapCallback;
import com.tamic.novate.callback.RxFileCallBack;
import com.tamic.novate.callback.RxListCallback;
import com.tamic.novate.callback.RxResultCallback;
import com.tamic.novate.callback.RxStringCallback;
import com.tamic.novate.util.FileUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tamic on 2016-06-15.
 * {@link # https://github.com/NeglectedByBoss/Novate
 *
 * @link # http://blog.csdn.net/sk719887916
 * }
 */
public class RequstActivity extends AppCompatActivity {

    String baseUrl = "http://ip.taobao.com/";
    private Novate novate;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private Button btn_get, btn_getSting, btn_bitmap, btn_file,
            btn_getList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requst);
        // UI referen
        btn_get = (Button) findViewById(R.id.get_Entity);
        btn_getSting = (Button) findViewById(R.id.get_String);
        btn_bitmap = (Button) findViewById(R.id.get_Bitmap);
        btn_file = (Button) findViewById(R.id.get_File);
        btn_getList = (Button) findViewById(R.id.get_ListEntity);


        parameters.put("ip", "21.22.11.33");
        headers.put("Accept", "application/json");

        novate = new Novate.Builder(this)
                .connectTimeout(20)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .addHeader(headers)
                .addLog(true)
                .build();


        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performGet();

            }


        });

        btn_getSting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performString();
            }


        });

        btn_bitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBitmap();
            }
        });

        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFile();

            }
        });
        btn_getList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performList();
            }


        });

    }


    /**
     *  下载字符串
     */
    private void performString() {

        novate.rxGet("service/getIpInfo.php", parameters, new RxStringCallback() {

            @Override
            public void onNext(Object tag, String response) {
                Toast.makeText(RequstActivity.this, response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(RequstActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

        });

    }

    /**
     * 下载集合数组
     * List<MusicBookCategory> 数组
     *
     *
     *  对应返回数据 ；
     *
     *  {
     "data":  [
     {
     "id": 15,
     "createDate": 1496286231000,
     "modifyDate": 1496291243000,
     "order": 1,
     "name": "推荐1",
     "treePath": ",13,",
     "grade": 1
     },
     {
     "id": 18,
     "createDate": 1496291263000,
     "modifyDate": 1496291263000,
     "order": 2,
     "name": "新手2",
     "treePath": ",13,",
     "grade": 1
     },
     {
     "id": 19,
     "createDate": 1496291284000,
     "modifyDate": 1496291284000,
     "order": 3,
     "name": "联系3",
     "treePath": ",13,",
     "grade": 1
     },
     {
     "id": 20,
     "createDate": 1496291300000,
     "modifyDate": 1496291300000,
     "order": 4,
     "name": "热门4",
     "treePath": ",13,",
     "grade": 1
     }

     ],
     "code": 100,
     "message": "success"
     }
     *
     */
    private void performList() {
        //简单数组
        novate.rxGet("service/getIpInfo.php", parameters, new RxListCallback<List<MusicBookCategory>>() {


            @Override
            public void onNext(Object tag, int code, String message, List<MusicBookCategory> response) {
                Toast.makeText(RequstActivity.this, response.size() +"" + ":" + response.get(0).toString(), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(RequstActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }


        });

        //复杂数组 构造自己的type，加入到RxListCallback中
        Type listType = new TypeToken<List<MusicBookCategory>>() {}.getType();

        novate.rxGet("service/getIpInfo.php", parameters, new RxListCallback<List<MusicBookCategory>>(listType) {


            @Override
            public void onNext(Object tag, int code, String message, List<MusicBookCategory> response) {
                Toast.makeText(RequstActivity.this, response.size() +"" + ":" + response.get(0).toString(), Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(RequstActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }


        });

    }


    /**
     * 下载File
     */
    private void performFile() {

        String downUrl = "http://img06.tooopen.com/images/20161022/tooopen_sy_182719487645.jpg";
        String path = FileUtil.getBasePath(this);

        novate.rxGet(downUrl, null, new RxFileCallBack(path, "my.jpg") {


            @Override
            public void onNext(Object tag, File file) {
                Toast.makeText(RequstActivity.this, " file is downloaded",  Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(Object tag, float progress, long downloaded, long total) {
                Toast.makeText(RequstActivity.this, progress +"" + " downloaded:" + downloaded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }


        });
    }



    /**
     * 下载对象
     * performGet
     */
    private void performGet() {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ip", "21.22.11.33");
        novate = new Novate.Builder(this)
                .addHeader(headers)
                //.addParameters(parameters)
                .connectTimeout(5)
                .baseUrl(baseUrl)
                .addCache(false)
                .addLog(true)
                .build();

        novate.rxGet("service/getIpInfo.php", parameters, new RxResultCallback<ResultModel>() {


            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, int code, String message, ResultModel response) {
                Toast.makeText(RequstActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 下载图谱
     */
    private void performBitmap() {

        novate.rxGet("you path url", parameters, new RxBitmapCallback() {


            @Override
            public void onNext(Object tag, Bitmap response) {

            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

        });
    }


}
