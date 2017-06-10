
![](http://upload-images.jianshu.io/upload_images/2022038-6a900d93d8acb091.jpg)

# Novate
   a  safety client by Https for Android,  (Android网路库，基于Retrofit和RxJava打造的链式网络库, 支持okhttp的调用风格，又兼容Retrofit注解方式，并支持rxJava链式操作。方便扩展，并能实现高速加载)
  
   
# Summary


- Join based API, reduce API redundancy
- Offline caching
- Support a variety of ways to access the network (a get, put, post, delete)
- Support file download and upload
- Unified support request header to join
- The unity of the support to return the result
- Support custom extensions API
- Support the unified request access to the network flow control
 


#dependencies

**Eclipse:**

    Download the laster JAR:( com.tamic.novate:novate:-1.x.aar)
    
    copy to libs dirPath!

**AS Gradle**:
   
- root：
     
       
         repositories {
            maven { url "https://jitpack.io" }
            jcenter()
        }
    
- app:
     
      
         dependencies {
            .....
             compile 'com.tamic.novate:novate:1.x.x(laster version)'
         
         }
  
Snapshots of the development version are available in Sonatype's snapshots repository.

Retrofit requires at minimum Java 7 or Android 2.3.

last vension: https://bintray.com/neglectedbyboss/maven/Novate


--------------------------


# 中文文档

  基于Retrofit和RxJava封装的链式网络库, 支持okhttp的调用分格，又兼容Retrofit注入方式，并支持rxJava调用的链式操作，
  不仅支持开发者自己扩展，还能实现高速网络加载！
  
  为何起名 Novate？ 
  
  Novate 的英文原意是用新事物代替
  我的目的是用新的东西来代替Retrofit的有些不易操作的地方，因此起名新奇的东西，所以结合了原来的Http用法习惯，又加入了Retrofit的特性，因此起名 ：Novate，LOGO也是加速的意思，本框架提供了一种封装架构思路，如果不喜欢本设计思路的朋友可以直接拿源码修改扩展。

功能
----
 
   - 加入基础API，减少Api冗余
   - 支持离线缓存
   - 支持多种方式访问网络（get,put,post ,delete）
   - 支持Json字符串，表单提交
   - 支持文件下载和上传，并有进度
   - 支持请求头统一加入
   - 支持对返回结果的统一处理
   - 支持自定义的扩展API
   - 支持统一请求访问网络的流程控制
   
   请求网络无需关心是否在主线程和非UI线程，操作UI直接可在回调处理, 保留了HttpClient的编码习惯，又加入了Builder模式编程！
   
用法
----

        Novate novate = new Novate.Builder(this)
                .baseUrl(baseUrl)
                .build();
  
  
# 更多API

```
         novate = new Novate.Builder(this)
                .addHeader(headers) //添加公共请求头
                .addParameters(parameters)//公共参数
                .connectTimeout(10)  //连接时间 可以忽略
                .addCookie(false)  //是否同步cooike 默认不同步
                .addCache(true)  //是否缓存 默认缓存
                .addCache(cache, cacheTime)   //自定义缓存
                .baseUrl("Url") //base URL
                .addLog(true) //是否开启log
                .cookieManager(new NovateCookieManager()) // 自定义cooike
                .addInterceptor() // 自定义Interceptor
                .addNetworkInterceptor() // 自定义NetworkInterceptor
                .proxy(proxy) //代理
                .client(client)  //clent 默认不需要
                .build(); 
                
   ```
   
   
   
  
# GET
        
        novate.executeGet("pathUrl", parameters（k-v）, new Novate.ResponseCallBack<NovateResponse<MyModel>>() {
        
            .....
        
        });
        
        
# POST        
        
        
        novate.executePost("pathUrl", parameters（k-v）, new Novate.ResponseCallBack<NovateResponse<MyModel>>() {
        
           .............
        
        });
        
# BODY

     novate.body(url, Object, new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        });

#FORM#

        novate.form(url, new HashMap<K-V>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        });

# JSON #
        
        
          novate.json(url, jsonString, new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                
            }

            @Override
            public void onNext(ResponseBody responseBody) {

            }
        });        
        
# UpLoad

 **upLoadImage**

     RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpg"), new File(you file path));

      novate.upload(url, requestFile, new BaseSubscriber<ResponseBody>{
      
        '''''''''''''''
      });
      
      
   带进度：
   
   
       RequestBody requestFile = Utils.createFile(str);

        NovateRequestBody novateRequestBody = Utils.createNovateRequestBody(requestFile, new UpLoadCallback() {
            @Override
            public void onProgress(Object tag, int progress, long speed, boolean done) {

            }
        });

        novate.upload(url, novateRequestBody, new BaseSubscriber<ResponseBody>() {
           '''''''''''''''
           
           
        });
      
      
      
 **upLoadFile**  
 
     ```
         File file = new File(path);

        // 创建 RequestBody，用于封装 请求RequestBody
        RequestBody requestFile = Utils.createFile(file);
         // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

       String descriptionString = "hello, 这是文件描述";
        RequestBody description = Utils.createPartFromString(descriptionString);
        
        novate.uploadFlie(url, description,  body,new BaseSubscriber<ResponseBody>() {
        
        .......
         });
        
        ```
        
    
**upLoadFiles**  
       
      
     
        Map<String, RequestBody> fileMaps = new HashMap<>();
        maps.put("key1", requestFile1);
        maps.put("key2", requestFile2);
        
        novate.uploadFlies(url, fileMaps, new BaseSubscriber<ResponseBody>(Context) {
           ......
        } );
        
        
   带进度：
      
        File file = new File(path);
      
        RequestBody requestFile =  Utils.createFile(file);

        Map<String, RequestBody> maps = new HashMap<>();
        
        maps.put("file1", Utils.createNovateRequestBody(requestFile, callback));
        maps.put("file2", Utils.createNovateRequestBody(requestFile, callback));

        novate.uploadFlies(url, maps, new BaseSubscriber<ResponseBody>(ExempleActivity.this) {
            ......
        } );


  
# DownLoad   
     

**downLoad for MaxFile**


   
      novate.download(downUrl, new DownLoadCallBack() {
      
         ''''''''''''
      });


**downLoad for minFile**   


     novate.downloadMin(downUrl, new DownLoadCallBack() {
      
         ''''''''''''
      });
      
 
 
   
# Custom Api 


如果默认的BaseApiService无法满足你的需求时，novate同样支持你自己的ApiService。


 **MyApi**
    
       
     public interface MyApi {

      @GET("url")
      Observable<MyBean> getdata(@QueryMap Map<String, String> maps);
   
     }
     
 **Execute**

     MyApi myApi = novate.create(MyApi.class);

     novate.call(myAPI.getdata(parameters),
                new BaseSubscriber<MyBean>{
                '''''''
                });

    }
 




# 注意

 如果你觉得此框架的业务码和错误码定的太死，其实框架已提供定制化方案，比如可以在你的项目中Assets中修改config文件：

如果想用自带的成功状态码0，不成功为非零的情况，可忽略下面的配置，无需改动。
`
  {
  "isFormat": "false",
  "sucessCode": [
    "1",
    "0",
    "1001"
  ],
  "error": {
    "1001": "网络异常"
  }
  }`


如果不想对结果格式化检查，请将`isFormat`设置为：`false`

如果想修改sucessCode的成功业务码，请将你的成功的业务码加入到`sucessCode `节点中。

**错误码**

需要对错误码进行自定义翻译，请配置相关`error`节点信息，具体可配置成：

                 `{
               "isFormat": "false",
                  "sucessCode": [
                    "1",
                 "0",
                  "1001"
                ],
                "error": {
                  "1001": "网络异常"，
                  "1002": "加入你的异常信息"
                         }
                 }
 


 **统一网络和Loading**
 
   继承Novate自带的的`BaseSubscriber<T>`,复写`onStart()`和`onCompleted()` 前者显示loading,后者结束loading.
   
   
   
  ``` 
    @Override
    public void onStart() {
        super.onStart();
        Log.v("Novate", "-->http is start");
        // todo some common as show loadding  and check netWork is NetworkAvailable
        // if  NetworkAvailable no !   must to call onCompleted
    }

    @Override
    public void onCompleted() {
        Log.v("Novate", "-->http is Complete");
        // todo some common as  dismiss loadding
    }
 ```
 
Q&A
---
 
1 Q：为什么服务器改变了数据，本地测试接口数据还是以前的旧数据？
  
  A： 在开发测试阶段，联调频繁的API时候，请将缓存关闭
  
2 Q: 为什么 我退出或杀进程 cookie就无效了？
 
 A： 由于有些机型在退出时候novate实例被回收了，请在application判断是存在novate真实实例 ，如果请重新初始化。或者建议用BaseActivity的context去初始化novate
  
3 Q: 为什么出现数据为空错误和API异常？

 
 A：由于Novate自动的异常驱动会捕获开发上层的异常，为了防止app闪退有一定的容错，遇到错误时请先检查业务上层的任何实例是否初始化过，不然被novate处理
  
4 Q 我不想使用系统限制的数据格式，怎么办？
 
 A：请将你业务下的Assets中修改config.json文件中`isFormat`设置为false。
  
5 Q:我想Novate默认的成功码，因为我的后端返回的100是成功的，怎么办？
  
  A:请将你业务工程下下的Assets中修改config.json 文件中`sucessCode`节点加入自己的成功码即可，也可以支持加入多个。

  
5 Q:我不想用Novate默认的错误码，怎么办？
  A:请将你业务工程下的Assets中修改config.json文件中`error`加入自己的结果码和msg信息。

 
Update Log   
-----
版本历史: https://bintray.com/neglectedbyboss/maven/Novate


- **V1.3.1**: 提供文件上传进度功能。`2017.6`。


- **V1.3.0**: 修复下载API在某些机型上文件夹创建失败情况。`2017.5`。

- **V1.2.9***: 强化取消请求API.`2017.1`。

- **V1.2.8***: 修复数据被备份的安全漏洞。 `2017.1`

- **V1.2.7***: 优化相关下载代码。优化cookie同步时对某些网站不兼容问题，`2016.12`。


- **V1.2.6.x**: 优化相关下载代码，并提交遗漏的put和delete方法，并将Http默认结果码回调到错误结果码中，并增加对请求参数的泛型支持
。`2016.12`

- **V1.2.5-bata**: 提供只对Response真实数据（T data）处理的功能，简化上层调用方式，但是不灵活，可选择使用，。`2016.11`

- **V1.2.3**:		增加对缓存功能的配置开关，可选择的对api进行缓存。`2016.11`
- **V1.2.2**:		解决对Response一些转换异常。`2016.11`
- **V1.2.1**:		增加对json的提交的支持。`2016.11`
- **V1.2.0**:		增加对Response数据结构格式，业务码，错误码的配置功能。提供自定义配置成功码和错误码功能。`2016.11`
- **V 1.1.1** :   更新对返回结果异常的判断分发出处理 `2016.10`
- **V1.1.0:**	  增加Response异常处理和容错处理。`2016.10`
- **V 1.0.2** :  增加body提交方式，增加小文件下载，增加表单方式提交功能，并新增设置是否同步cookie接口 . `2016.9`
- **V1.0.1:**	  扩展下载接口，可以制定下载路径和文件名，包括修复下载抛异常问题。`2016.8`
- **V 1.0 :**    基于retrofit和Rxjava完成以泛型基础的get, put, Post delete, upLoad, downLoad功能 `2016.6`
  
  
   
#License
--------

    Copyright 2013 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.   
更多介绍：https://tamicer.github.io/2016/08/10/novate10/
