# Novate
   a  safety client by Https for Android,  (Android线程安全http请求库)

# Summary



#Download

Download the latest JAR:( com.tamic.novate-1.0.0.aar)

    compile(name: 'com.tamic.novate-1.0.0', ext: 'aar')

or Gradle:

     compile project(':novate')
  
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
   
   
用法
----

        Novate novate = new Novate.Builder(this)
                .baseUrl(baseUrl)
                .build();
                
       
# Get
        
        novate.executeGet("pathUrl", parameters, new Novate.ResponseCallBack<NovateResponse<ResultModel>>() {
        
            .....
        
        });
        
        
# Post        
        
        
        novate.executePost("pathUrl", parameters, new Novate.ResponseCallBack<NovateResponse<ResultModel>>() {
        
           .............
        
        });
        
# Upload

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

 **MyAPI**
    
       
     public interface MyAPI {

       @GET("url")
      Observable<MyBean> getdata(@QueryMap Map<String, String> maps);
   
     }
     
 **Execute**

     MyAPI myAPI = novate.create(MyAPI.class);

    novate.subscribe(myAPI.getdata(parameters),
                new BaseSubscriber<MyBean>{
                '''''''
                });

    }
   
