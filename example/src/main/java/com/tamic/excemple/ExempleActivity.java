package com.tamic.excemple;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tamic.excemple.model.MovieModel;
import com.tamic.excemple.model.ResultModel;
import com.tamic.excemple.model.SouguBean;
import com.tamic.novate.NovateResponse;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.RxApiManager;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;
import com.tamic.novate.download.DownLoadCallBack;
import com.tamic.novate.download.UpLoadCallback;
import com.tamic.novate.request.NovateRequestBody;
import com.tamic.novate.util.FileUtil;
import com.tamic.novate.util.LogWraper;
import com.tamic.novate.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscription;

/**
 * Created by Tamic on 2016-06-15.
 * {@link # https://github.com/NeglectedByBoss/Novate
 *
 * @link # http://blog.csdn.net/sk719887916
 * }
 */
public class ExempleActivity extends AppCompatActivity {

    String baseUrl = "http://ip.taobao.com/";
    private Novate novate;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private Button btn, btn_test, btn_get, btn_post, btn_download,
            btn_download_Min, btn_upload, btn_uploadfile, btn_myApi, btn_more;

    private ProgressDialog mProgressDialog;
    String uploadPath = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exemple);
        // UI referen
        btn = (Button) findViewById(R.id.bt_simple);
        btn_test = (Button) findViewById(R.id.bt_test);
        btn_get = (Button) findViewById(R.id.bt_get);
        btn_post = (Button) findViewById(R.id.bt_post);
        btn_download = (Button) findViewById(R.id.bt_download);
        btn_upload = (Button) findViewById(R.id.bt_upload);
        btn_download_Min = (Button) findViewById(R.id.bt_download_min);
        btn_uploadfile = (Button) findViewById(R.id.bt_uploadflie);
        btn_myApi = (Button) findViewById(R.id.bt_my_api);
        btn_more = (Button) findViewById(R.id.bt_more);

        initProgress(this);

        parameters.put("ip", "21.22.11.33");
        headers.put("Accept", "application/json");

        novate = new Novate.Builder(this)
                //.addParameters(parameters)
                .connectTimeout(20)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .addHeader(headers)
                .addCache(true)
                .addLog(true)
                .build();

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performTest();

            }


        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                perform();
            }


        });

        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGet();
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPost();

            }
        });
        btn_myApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                perform_Api();
            }


        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDown();
            }
        });
        btn_download_Min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDownMin();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUpLoadImage();
            }
        });

        btn_uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performUpLoadFlie();
            }
        });

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExempleActivity.this, RequstActivity.class));
            }
        });
    }

    /**
     * 初始化进度条
     */
    private void initProgress(Context aContext) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(aContext);
        }

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("温馨提示");
        mProgressDialog.setMax(100);
        mProgressDialog.setMessage("正在上传中...");
        mProgressDialog.setCancelable(true);

    }


    private void showPressDialog() {

        if (mProgressDialog == null || this.isFinishing()) {
            return;
        }
        mProgressDialog.show();
    }


    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void updateProgressDialog(int progress) {

        if (mProgressDialog != null) {
            if (!mProgressDialog.isShowing()) {
                showPressDialog();
            }
            mProgressDialog.setProgress(progress);
        }
    }


    /**
     * test
     */
    private void performTest() {

        //http://apis.baidu.com/apistore/weatherservice/cityname?cityname=上海
        Map<String, String> headers = new HashMap<>();
        headers.put("apikey", "27b6fb21f2b42e9d70cd722b2ed038a9");
        headers.put("Accept", "application/json");
        novate = new Novate.Builder(this)
                .addHeader(headers)
                .addParameters(parameters)
                .connectTimeout(5)
                .baseUrl("https://apis.baidu.com/")
                .addHeader(headers)
                .addLog(true)
                .build();


        Subscription subscription = novate.test("https://apis.baidu.com/apistore/weatherservice/cityname?cityname=上海", null,
                new MyBaseSubscriber<ResponseBody>(ExempleActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        Log.e("OkHttp", e.getMessage());
                        Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Toast.makeText(ExempleActivity.this, new String(responseBody.bytes()), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        RxApiManager.get().add("test", subscription);
        //cancel   RxApiManager.get().cancel("my");

    }


    @Override
    protected void onPause() {
        super.onPause();
        //RxApiManager.get().cancel("my");
    }

    /**
     * http://www.dianpingmedia.com/framework/web/user/unauth/login
     */
    private void perform() {

        parameters = new HashMap<>();
        /*start=0&count=5*/
        parameters.put("start", "0");
        parameters.put("count", "1");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mobileNumber", "18826412577");
        parameters.put("loginPassword", "123456");




        novate = new Novate.Builder(this)
                .addParameters(parameters)
                .connectTimeout(10)
                .baseUrl("http://api.douban.com/")
                //.addApiManager(ApiManager.class)
                .addLog(true)
                .build();

        novate.get("v2/movie/top250", parameters, new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {

                    String jstr = new String(responseBody.bytes());

                    Type type = new TypeToken<MovieModel>() {
                    }.getType();

                    MovieModel response = new Gson().fromJson(jstr, type);

                    Toast.makeText(ExempleActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


    }

    /**
     * performGet
     * 已废弃
     */
    private void performGet() {

        /**
         * 如果不需要数据解析后返回 则调用novate.Get()
         * 参考 performPost()中的方式
         */
        novate.executeGet("service/getIpInfo.php", parameters, new Novate.ResponseCallBack<ResultModel>() {
            @Override
            public void onStart() {

                // todo onStart

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccee(NovateResponse<ResultModel> response) {

            }


            @Override
            public void onsuccess(int code, String msg, ResultModel  response, String originalResponse) {
                Toast.makeText(ExempleActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    /**
     * performPost
     */
    private void performPost() {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ip", "21.22.11.33");
        novate = new Novate.Builder(this)
                .connectTimeout(8)
                .baseUrl(baseUrl)
                //.addApiManager(ApiManager.class)
                .addLog(true)
                .build();


        /**
         *
         *
         * 调用post需要你自己解析数据
         *
         * 如果需要解析后返回 则调用novate.executeGet()
         * 参考 performGet()中的方式
         */
        novate.post("service/getIpInfo.php", parameters, new MyBaseSubscriber<ResponseBody>(ExempleActivity.this) {
            @Override
            public void onError(Throwable e) {
                if (!TextUtils.isEmpty(e.getMessage())) {
                    Log.e("OkHttp", e.getMessage());
                    Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNext(ResponseBody responseBody) {

                try {
                    String jstr = new String(responseBody.bytes());

                    if (jstr.trim().isEmpty()) {
                        return;
                    }

                    Type type = new TypeToken<NovateResponse<ResultModel>>() {
                    }.getType();

                    NovateResponse<ResultModel> response = new Gson().fromJson(jstr, type);

                    if (response.getData() != null) {
                        Toast.makeText(ExempleActivity.this,    response.getData().toString(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ExempleActivity.this, jstr , Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * perform_myApi
     * HostUrl = "http://lbs.sougu.net.cn/";
     */
    private void perform_Api() {

        parameters.clear();
        parameters.put("m", "souguapp");
        parameters.put("c", "appusers");
        parameters.put("a", "network");

        novate = new Novate.Builder(this)
                .addHeader(headers)
                .addParameters(parameters)
                .baseUrl("http://lbs.sougu.net.cn/")
                .addLog(true)
                .build();


        MyAPI myAPI = novate.create(MyAPI.class);

        novate.call(myAPI.getSougu(parameters),
                new MyBaseSubscriber<SouguBean>(ExempleActivity.this) {


                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(SouguBean souguBean) {

                        Toast.makeText(ExempleActivity.this, souguBean.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    /**
     * upload
     */
    private void performUpLoadImage() {

        String mPath = uploadPath; //"you File path ";
        String url = "";
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/jpg"), new File(mPath));

        final NovateRequestBody requestBody = Utils.createNovateRequestBody(requestFile, new UpLoadCallback() {

            @Override
            public void onProgress(Object tag, int progress, long speed, boolean done) {

                LogWraper.d("uplaod", "tag:" + tag.toString() + "progress:"+ progress);
                updateProgressDialog(progress);


            }
        });

        novate.rxUploadWithBody(url, requestBody, new RxStringCallback() {

            @Override
            public void onStart(Object tag) {
                super.onStart(tag);
                showPressDialog();
            }

            @Override
            public void onNext(Object tag, String response) {
                LogWraper.d("novate", response);
                Toast.makeText(ExempleActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                dismissProgressDialog();
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

    /**
     * upload
     */
    private void performUpLoadFlie() {

        String mPath = uploadPath; //"you File path ";
        String url = "";
        File file = new File(mPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data; charset=utf-8"), file);

        final NovateRequestBody requestBody = Utils.createNovateRequestBody(requestFile, new UpLoadCallback() {

            @Override
            public void onProgress(Object tag, int progress, long speed, boolean done) {

                LogWraper.d("uplaod", "tag:" + tag.toString() + "progress:"+ progress);
                updateProgressDialog(progress);
            }
        });


        MultipartBody.Part body2 =
                MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        novate.rxUploadWithPart(url, body2, new RxStringCallback() {

            @Override
            public void onStart(Object tag) {
                super.onStart(tag);
                showPressDialog();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }

            @Override
            public void onNext(Object tag, String response) {
                LogWraper.d(response);
                Toast.makeText(ExempleActivity.this, "成功", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }


        });

    }

    /**
     * upload
     */
    private void performUpLoadFlies(){

        String path = uploadPath;//"you File path ";
        String url = "http://workflow.tjcclz.com/GWWorkPlatform/NoticeServlet?GWType=wifiUploadFile";

        File file = new File(path);
        // 创建 RequestBody，用于封装 请求RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        final NovateRequestBody requestBody = Utils.createNovateRequestBody(requestFile, new UpLoadCallback() {

            @Override
            public void onProgress(Object tag, int progress, long speed, boolean done) {

                LogWraper.d("uplaod", "tag:" + tag.toString() + "progress:"+ progress);

                updateProgressDialog(progress);


            }
        });

        MultipartBody.Part part =
                MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        Map<String,  MultipartBody.Part> maps = new HashMap<>();
        maps.put("image", part);

        novate.rxUploadWithPartMap(url, maps, new RxStringCallback() {

            @Override
            public void onStart(Object tag) {
                super.onStart(tag);
                showPressDialog();
            }

            @Override
            public void onNext(Object tag, String response) {
                LogWraper.d("novate", response);
                Toast.makeText(ExempleActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Object tag, Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                dismissProgressDialog();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {


            }
        });

    }

    /**
     * performDown file
     * ex: apk , video...
     */
    private void performDown() {
        String downUrl = "http://wifiapi02.51y5.net/wifiapi/rd.do?f=wk00003&b=gwanz02&rurl=http%3A%2F%2Fdl.lianwifi.com%2Fdownload%2Fandroid%2FWifiKey-3091-guanwang.apk";
        novate.download(downUrl, "test.mei",  new DownLoadCallBack() {

            @Override
            public void onStart(String s) {
                super.onStart(s);

                btn_download.setText("DownLoad cancel");
                showPressDialog();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this,  "onError:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(String key, int progress, long fileSizeDownloaded, long totalSize) {
                super.onProgress(key, progress, fileSizeDownloaded, totalSize);
                Log.v("test", fileSizeDownloaded+"");
                //Toast.makeText(ExempleActivity.this, "progress: " + progress + "   download: " + fileSizeDownloaded, Toast.LENGTH_SHORT).show();
                updateProgressDialog(progress);

            }

            @Override
            public void onSucess(String key, String path, String name, long fileSize) {
                Toast.makeText(ExempleActivity.this, "download  onSucess", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                dismissProgressDialog();
            }

        });
    }

    /**
     * performDown small file
     * ex: image txt
     */
    private void performDownMin() {

        String downUrl = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
        novate.downloadMin("tag", downUrl, "my.jpg",new DownLoadCallBack() {

            @Override
            public void onStart(String s) {
                super.onStart(s);
                btn_download.setText("DownLoadMin cancel");
                showPressDialog();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this,  "onError:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSucess(String key, String path, String name, long fileSize) {
                btn_download.setText("DownLoad start");
                uploadPath = path + name;
                dismissProgressDialog();
            }


            @Override
            public void onProgress(String key, int progress, long fileSizeDownloaded, long totalSize) {
                super.onProgress(key, progress, fileSizeDownloaded, totalSize);
                updateProgressDialog(progress);
                Toast.makeText(ExempleActivity.this, "progress: " + progress + "  download: " + fileSizeDownloaded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                btn_download.setText("DownLoadMin start");
            }

        });
    }



}
