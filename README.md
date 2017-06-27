
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
     
```
      
          dependencies {
             compile 'com.tamic.novate:novate:1.5.2.3'
          }
```


        
Snapshots of the development version are available in Sonatype's snapshots repository.

Retrofit requires at minimum Java 7 or Android 2.3.

Laster vension: https://bintray.com/neglectedbyboss/maven/Novate  最新版本点击去查询

**加入权限**


```
<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
```
    
**混淆**   
 
     -keep class com.tamic.novate.** {*;}

--------------------------


# 中文文档

  基于Retrofit和RxJava封装的链式网络库, 支持okhttp的调用分格，又兼容Retrofit注入方式，并支持RxJava调用的链式操作，
  不仅支持开发者自己扩展，还沿用Okhttp的高效的网络加载！最重要的novate自带的异常驱动机制，帮开发者解决了绝大部分的异常
  错误处理，减少API或者业务代码出错的导致崩溃概率。
  
  为何起名 Novate？ 
  
  Novate 的英文原意是用新事物代替
  目的是用新的东西来代替Retrofit的有些不易操作的地方，因此起名新奇的东西，所以结合了原来的Http用法习惯，又加入了Retrofit的特性，因此起名 ：Novate，LOGO也是加速的意思，本框架提供了一种封装架构思路，如果不喜欢本设计思路的朋友可以直接拿源码修改扩展。

功能
----
  - 优化设计：加入基础API，减少Api冗余
 - 强大的缓存模式： 支持离线缓存， 无网络智能加载缓存，可配置是否需要缓存
 - cookie管理：自带cookie管理机制
 - 全方位请求模式：支持多种方式访问网络（get,put, post ,delete）
 - 轻送调用：支持表单,图文一起，json上传。
 - 文件传输：支持文件下载和上传，支持进度
 - 动态添加：支持请求头和参数统一添加，分别添加。
 - 结果处理：支持对返回结果的统一处理，自动帮你序列化复杂的数据。
 - 扩展性强：支持自定义的Retrofit的API，默认Api无法满足时可自定义自己的Service
 - 悠雅方便：支持统一请求访问网络的流程控制，以方便帮你完美加入Processbar进度。
 - RxJava结合： 结合RxJava，线程智能控制

   请求网络无需关心是否在主线程和非UI线程，操作UI直接可在回调处理, 保留了HttpClient的编码习惯，又加入了Builder模式编程！
   
   
**详细介绍 请看：Wiki, wiki, wiki!**
 
Update Log   
-----
版本历史: https://bintray.com/neglectedbyboss/maven/Novate
更新历史请看：https://bintray.com/neglectedbyboss/maven/Novate/view/release#release
  
   
#License
--------

    Copyright 2016 Tamic, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.   

**更多介绍：https://tamicer.github.io/2016/08/10/novate10/**


**技术交流QQ群： 458542940**

