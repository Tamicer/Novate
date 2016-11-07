# Novate
   a  safety client by Https for Android,  (Android线程安全http请求库)
   
![](http://upload-images.jianshu.io/upload_images/2022038-6a900d93d8acb091.jpg)


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

    Download the laster JAR:( com.tamic.novate:novate:-1.1.0.aar)

    compile(name: 'com.tamic.novate:novate-1.1.2', ext: 'aar')

**or Gradle**:
   
- root：
     
       
         repositories {
            maven { url "https://jitpack.io" }
            jcenter()
        }
    
- app:
     
      
         dependencies {
            .....
            compile 'com.tamic.novate:novate:1.1.2'
         
         }
  
Snapshots of the development version are available in Sonatype's snapshots repository.

Retrofit requires at minimum Java 7 or Android 2.3.


--------------------------


# 中文文档

  基于Retrofit和RxJava封装的链式请求库，为何起名 Novate？
  
  Novate的英文原意是用新事物代替
  我的目的是用新的东西来代替Retrofit的有些不易操作的地方，因此起名新奇的东西，所以结合了原来的Http用法习惯，又加入了Retrofit的特性，因此起名 ：Novate

功能
----
 
   - 加入基础API，减少Api冗余
   - 支持离线缓存
   - 支持多种方式访问网络（get,put,post ,delete）
   - 支持文件下载和上传
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
                
       
# Get
        
        novate.executeGet("pathUrl", parameters, new Novate.ResponseCallBack<NovateResponse<MyModel>>() {
        
            .....
        
        });
        
        
# Post        
        
        
        novate.executePost("pathUrl", parameters, new Novate.ResponseCallBack<NovateResponse<MyModel>>() {
        
           .............
        
        });
        
# UpLoad

    RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/jpg"), new File(you file path));

      novate.upload(url, requestFile, new BaseSubscriber<ResponseBody>{
      
        '''''''''''''''
      });

  
# DownLoad   
     
   
      novate.download(downUrl, new DownLoadCallBack() {
      
         ''''''''''''
      });
   
   
# Custom Api 


如果默认的BaseApiService无法满足你的需求时，novate同样支持你自己的ApiService。

 **MyAPI**
    
       
     public interface MyAPI {

       @GET("url")
      Observable<MyBean> getdata(@QueryMap Map<String, String> maps);
   
     }
     
 **Execute**

     MyAPI myAPI = novate.create(MyAPI.class);

     novate.call(myAPI.getdata(parameters),
                new BaseSubscriber<MyBean>{
                '''''''
                });

    }
   
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
更多介绍：http://www.jianshu.com/p/d7734390895e
