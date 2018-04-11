package com.codingending.packagefairy.utils;

import android.util.Log;


import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 提供多种拦截器
 * Created by CodingEnding on 2018/4/6.
 */

public class InterceptorUtils {
    public static final String TAG="InterceptorUtils";

    private InterceptorUtils(){}

    /**
     * 拦截器：设置通用Header
     */
    public static Interceptor commonHeaderInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originRequest=chain.request();//获取源Request

                Request.Builder builder=originRequest.newBuilder();//利用Builder设置通用Header
//                builder.header("name","value");//设置通用请求头
                builder.method(originRequest.method(),originRequest.body());//可以省略
                Request newRequest=builder.build();

                return chain.proceed(newRequest);
            }
        };
    }

    /**
     * 拦截器：拦截服务器响应
     */
    public static Interceptor serverInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response=chain.proceed(chain.request());//获取源Response

                String serverDate=response.header("Date","default value");//提取服务器时间
                Log.i(TAG,serverDate);
                return response;
            }
        };
    }

    /**
     * 拦截器：添加通用请求参数
     */
    public static Interceptor commonParamsInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originRequest=chain.request();//获取源Response
                HttpUrl.Builder builder=originRequest.url().newBuilder();//Url的构建器
//                builder.addQueryParameter("name","value");//添加通用请求参数
                HttpUrl newUrl=builder.build();
                Request newRequest=originRequest.newBuilder().url(newUrl).build();
                return chain.proceed(newRequest);
            }
        };
    }

    /**
     * 缓存设置
     */
    public static Interceptor cacheInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request=chain.request();
                Response response=chain.proceed(request);
                return response.newBuilder().
                        addHeader("Cache-Control","public,max-age=120").build();
            }
        };
    }

}
