package com.tamic.novate;


import java.util.Map;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * ApiService
 * Created by Tamic on 2016-06-03.
 */
public interface BaseApiService {
    @POST()
    @FormUrlEncoded
    <T> Observable<ResponseBody> executePost(
            @Url() String url,
            @FieldMap Map<String, Object> maps);

    @POST("{url}")
    Observable<ResponseBody> executePostBody(
            @Path("url") String url,
            @Body Object object);

    @GET()
    <T> Observable<ResponseBody> executeGet(
            @Url String url,
            @QueryMap Map<String, Object> maps);

    @DELETE()
    <T> Observable<ResponseBody> executeDelete(
            @Url String url,
            @QueryMap Map<String, Object> maps);

    @PUT()
    <T> Observable<ResponseBody> executePut(
            @Url String url,
            @FieldMap Map<String, Object> maps);

    @Multipart
    @POST()
    Observable<ResponseBody> upLoadImage(
            @Url() String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlie(
            @Url String fileUrl,
            @Part("description") RequestBody description,
            @Part("files") MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(
            @Url() String url,
            @PartMap() Map<String, RequestBody> maps);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);


    @GET
    Observable<ResponseBody> downloadSmallFile(@Url String fileUrl);


    @GET
    <T> Observable<ResponseBody> getTest(@Url String fileUrl,
                                         @QueryMap Map<String, Object> maps);

    @FormUrlEncoded
    @POST()
    <T> Observable<ResponseBody> postForm(
            @Url() String url,
            @FieldMap Map<String, Object> maps);


    @POST()
    Observable<ResponseBody> postJson(
            @Url() String url,
            @Body RequestBody jsonBody);

}


