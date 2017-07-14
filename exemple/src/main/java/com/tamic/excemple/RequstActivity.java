package com.tamic.excemple;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.tamic.novate.util.LogWraper;

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
public class RequstActivity extends BaseActivity {

    private Novate novate;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private Button btn_get, btn_getSting, btn_bitmap, btn_file,
            btn_getList, btn_post_Bean, btn_post_Json ,btn_upFile, btn_down_file;

    String uploadPath = "";

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
        btn_post_Bean = (Button) findViewById(R.id.post_Bean);
        btn_post_Json =(Button) findViewById(R.id.post_Json);
        btn_upFile =(Button) findViewById(R.id.up_File);
        btn_down_file = (Button) findViewById(R.id.down_File);


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

        btn_post_Bean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postBean();

            }
        });
        btn_post_Json.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postJson();

            }
        });
        btn_upFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rxUpLoad();

            }
        });

        btn_down_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDown();
            }
        });

    }

    private void postJson() {

        String url = "http://workflow.tjcclz.com/GWWorkPlatform/NoticeServlet?GWType=wifiUploadFile";

        novate.rxJson(url, "jsonString....", new RxStringCallback() {

            @Override
            public void onProgress(Object tag, float progress, long transfered, long total) {
                super.onProgress(tag, progress, transfered, total);
                updateProgressDialog((int) progress);
            }

            @Override
            public void onStart(Object tag) {
                super.onStart(tag);
                showPressDialog();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(RequstActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, String response) {
                LogWraper.d(response);
                Toast.makeText(RequstActivity.this, "成功", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }


        });


    }

    private void postBean() {

        String url = "http://workflow.tjcclz.com/GWWorkPlatform/NoticeServlet?GWType=wifiUploadFile";

        novate.rxBody(url, new ResultModel(), new RxStringCallback() {

            @Override
            public void onProgress(Object tag, float progress, long transfered, long total) {
                super.onProgress(tag, progress, transfered, total);
                updateProgressDialog((int) progress);
            }

            @Override
            public void onStart(Object tag) {
                super.onStart(tag);
                showPressDialog();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(RequstActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, String response) {
                LogWraper.d(response);
                Toast.makeText(RequstActivity.this, "成功", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }


        });

    }

    /**
     *
     */
    private void rxDown() {
        String downUrl = "http://wap.dl.pinyin.sogou.com/wapdl/hole/201512/03/SogouInput_android_v7.11_sweb.apk";
        new Novate.Builder(this)
                .connectTimeout(20)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .build()
                .rxDownload(downUrl, new RxFileCallBack() {
                    @Override
                    public void onStart(Object tag) {
                        super.onStart(tag);
                        showPressDialog();
                    }

                    @Override
                    public void onNext(Object tag, File file) {
                        dismissProgressDialog();
                        Toast.makeText(RequstActivity.this, "下载成功!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(Object tag, float progress, long downloaded, long total) {
                        updateProgressDialog((int) progress);
                    }

                    @Override
                    public void onProgress(Object tag, int progress, long speed, long transfered, long total) {
                        super.onProgress(tag, progress, speed, transfered, total);
                        updateProgressDialog((int) progress);
                    }

                    @Override
                    public void onError(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCancel(Object tag, Throwable e) {

                    }

                    @Override
                    public void onCompleted(Object tag) {
                        super.onCompleted(tag);
                        dismissProgressDialog();
                    }
                });
    }

    private void rxUpLoad() {


        String mPath = "you File path ";
        String upalodurl = "http://workflow.tjcclz.com/GWWorkPlatform/NoticeServlet?GWType=wifiUploadFile";
        novate.rxBody(upalodurl, new File(mPath), new RxStringCallback() {

            @Override
            public void onProgress(Object tag, float progress, long transfered, long total) {
                super.onProgress(tag, progress, transfered, total);
                updateProgressDialog((int) progress);
            }

            @Override
            public void onStart(Object tag) {
                super.onStart(tag);
                showPressDialog();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(RequstActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, String response) {
                LogWraper.d(response);
                Toast.makeText(RequstActivity.this, "成功", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }


        });


    }


    /**
     * 下载字符串
     */
    private void performString() {
        new Novate.Builder(this)
                .baseUrl("http://ip.taobao.com/")
                .build()
                .rxGet("service/getIpInfo.php", parameters, new RxStringCallback() {

                    @Override
                    public void onStart(Object tag) {
                        super.onStart(tag);
                        Toast.makeText(RequstActivity.this, tag.toString() + "开始了", Toast.LENGTH_SHORT).show();
                    }

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
                        Toast.makeText(RequstActivity.this, tag.toString() + "取消了", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 下载集合数组
     */
    private void performList() {
        //简单数组
        new Novate.Builder(this)
                .baseUrl("http://xxx.com/")
                .build()
                .rxGet("service/getList", parameters, new RxListCallback<List<MusicBookCategory>>() {
                    @Override
                    public void onNext(Object tag, int code, String message, List<MusicBookCategory> response) {
                        Toast.makeText(RequstActivity.this, response.size() + "" + ":" + response.get(0).toString(), Toast.LENGTH_SHORT).show();
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
     * /复杂数组 构造自己的type，加入到RxListCallback中
     */
    private void getListBean(){

        //复杂数组 构造自己的type，加入到RxListCallback中
        Type listType = new TypeToken<List<MusicBookCategory>>() {
        }.getType();

        novate.rxGet("service/getIpInfo.php", parameters, new RxListCallback<List<MusicBookCategory>>(listType) {


            @Override
            public void onNext(Object tag, int code, String message, List<MusicBookCategory> response) {
                Toast.makeText(RequstActivity.this, response.size() + "" + ":" + response.get(0).toString(), Toast.LENGTH_SHORT).show();
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

        novate.rxGet(downUrl, new RxFileCallBack(path, "my.jpg") {


            @Override
            public void onNext(Object tag, File file) {
                Toast.makeText(RequstActivity.this, " file is downloaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(Object tag, float progress, long downloaded, long total) {
                Toast.makeText(RequstActivity.this, progress + "" + " downloaded:" + downloaded, Toast.LENGTH_SHORT).show();
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
