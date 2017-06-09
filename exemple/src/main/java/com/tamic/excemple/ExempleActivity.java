package com.tamic.excemple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tamic.excemple.model.MovieModel;
import com.tamic.excemple.model.ResultModel;
import com.tamic.excemple.model.SouguBean;
import com.tamic.novate.BaseApiService;
import com.tamic.novate.NovateResponse;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.tamic.novate.download.DownLoadCallBack;
import com.tamic.novate.download.UpLoadCallback;
import com.tamic.novate.request.NovateRequestBody;
import com.tamic.novate.util.FileUtil;
import com.tamic.novate.util.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


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
            btn_download_Min, btn_upload, btn_uploadfile, btn_myApi;



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

        parameters.put("ip", "21.22.11.33");
        headers.put("Accept", "application/json");


      /*
         如果想修改默认配置 可以参考下面格式，请修改assets的文件copy到你的项目中
         isFormat是否对后端数据进行格式化校验

         sucessCode:成功状态码，可以加多个。

         error：非成功状态码，可以支持多个配置


        {
            "isFormat": "false",
                "sucessCode": [
                    "0",
                    "1001"
            ],
            "error": {
                    "23": "网络异常2",
                    "24": "网络异常2",
                    "25": "网络异常3"
        }
        }*/

        novate = new Novate.Builder(this)
                //.addParameters(parameters)
                .connectTimeout(20)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .addHeader(headers)//.addApiManager(ApiManager.class)
                .addLog(true)
                .build();


        BaseApiService api = novate.create(BaseApiService.class);

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

        novate.test("https://apis.baidu.com/apistore/weatherservice/cityname?cityname=上海", null,
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

    }


    private void perform() {

        parameters = new HashMap<>();
        /*start=0&count=5*/
        parameters.put("start", "0");
        parameters.put("count", "1");

        novate = new Novate.Builder(this)
                .addParameters(parameters)
                .connectTimeout(5)
                .baseUrl("http://api.douban.com/")
                //.addApiManager(ApiManager.class)
                .addLog(true)
                .build();

        novate.get("v2/movie/top250", parameters, new MyBaseSubscriber<ResponseBody>(ExempleActivity.this) {
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
     */
    private void performGet() {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ip", "21.22.11.33");
        novate = new Novate.Builder(this)
                .addHeader(headers)
                //.addParameters(parameters)
                .connectTimeout(5)
                .baseUrl(baseUrl)
                .addLog(true)
                .build();

        /**
         * 如果不需要数据解析后返回 则调用novate.Get()
         * 参考 performPost()中的方式
         */
        novate.executeGet("service/getIpInfo.php", parameters, new Novate.ResponseCallBack<NovateResponse<ResultModel>>() {
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
                Toast.makeText(ExempleActivity.this, response.getData().toString(), Toast.LENGTH_SHORT).show();
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
                if (e.getMessage() != null) {
                    Log.e("OkHttp", e.getMessage());
                    Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNext(ResponseBody responseBody) {

                try {
                    String jstr = new String(responseBody.bytes());

                    Type type = new TypeToken<NovateResponse<ResultModel>>() {
                    }.getType();

                    NovateResponse<ResultModel> response = new Gson().fromJson(jstr, type);
                    Toast.makeText(ExempleActivity.this, response.getData().toString(), Toast.LENGTH_SHORT).show();
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
                .connectTimeout(10)
                .addCookie(false)
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


        String url = "";
        String str = FileUtil.loadFromAssets(this, "novate.png");

        RequestBody requestFile = Utils.createFile(str);

        NovateRequestBody novateRequestBody = Utils.createNovateRequestBody(requestFile, new UpLoadCallback() {
            @Override
            public void onProgress(Object tag, int progress, long speed, boolean done) {

            }
        });

        novate.upload(url, novateRequestBody, new MyBaseSubscriber<ResponseBody>(ExempleActivity.this) {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        });

        // or

        String mPath = "you File path ";
        novate.uploadImage(url, new File(mPath), new BaseSubscriber<ResponseBody>(ExempleActivity.this) {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        });

    }

    /**
     * upload
     */
    private void performUpLoadFlie() {

        // String mPath = "file:///android_asset/novate-config.json";
        String url = "";
        String str = FileUtil.loadFromAssets(this, "novate.png");
        File file = new File(str);

        // 创建 RequestBody，用于封装 请求RequestBody
        RequestBody requestFile = Utils.createImage(file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body  =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // 添加描述
        String descriptionString = "hello, 这是文件描述";


        RequestBody description = Utils.createPartFromString(descriptionString);

              /*  RequestBody.create(
                        MediaType.parse("multipart/form-data; charset=utf-8"), descriptionString);*/

        novate.uploadFlie(url, description, body, new MyBaseSubscriber<ResponseBody>(ExempleActivity.this) {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        });


    }

    private void performUpLoadFlies() {

        String path = FileUtil.loadFromAssets(this, "novate.png");
        String url = "";

        UpLoadCallback callback = new UpLoadCallback() {

            @Override
            public void onProgress(Object tag, int progress, long speed, boolean come) {

            }
        };

        File file = new File(path);
        // 创建 RequestBody，用于封装 请求RequestBody
        RequestBody requestFile =  Utils.createFile(file);

        Map<String, RequestBody> maps = new HashMap<>();
        //Tag可以不设置 批量时候用于区分对待
        maps.put("file1", Utils.createNovateRequestBody(requestFile, callback));
        maps.put("file2", Utils.createNovateRequestBody(requestFile, callback));

        novate.uploadFlies(url, maps, new MyBaseSubscriber<ResponseBody>(ExempleActivity.this) {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(ExempleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        } );

    }

    /**
     * performDown file
     * ex: apk , video...
     */
    private void performDown() {
        String downUrl = "http://apk.hiapk.com/web/api.do?qt=8051&id=723";
        novate.download(downUrl, new DownLoadCallBack() {


            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSucess(String key, String path, String name, long fileSize) {
                Toast.makeText(ExempleActivity.this, "download  onSucess", Toast.LENGTH_SHORT).show();
                btn_download.setText("DownLoad start");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                btn_download.setText("DownLoad start");
            }

            @Override
            public void onProgress(String key, long fileSizeDownloaded, long totalSize) {
                super.onProgress(key, fileSizeDownloaded, totalSize);
            }
        });
    }

    /**
     * performDown small file
     * ex: image txt
     */
    private void performDownMin() {

        String downUrl = "http://img06.tooopen.com/images/20161022/tooopen_sy_182719487645.jpg";
        novate.downloadMin(downUrl, new DownLoadCallBack() {

            @Override
            public void onStart(String key) {
                super.onStart(key);
                Toast.makeText(ExempleActivity.this, "download is start", Toast.LENGTH_SHORT).show();
                btn_download.setText("DownLoadMin cancel");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSucess(String key, String path, String name, long fileSize) {
                Toast.makeText(ExempleActivity.this, "download  onSucess", Toast.LENGTH_SHORT).show();
                btn_download.setText("DownLoadMin start");
           }

            @Override
            public void onCancel() {
                super.onCancel();
                btn_download.setText("DownLoadMin start");
            }

            @Override
            public void onProgress(String key, long fileSizeDownloaded, long totalSize) {
                super.onProgress(key, fileSizeDownloaded, totalSize);
            }
        });
    }

}
