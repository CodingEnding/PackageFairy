package com.codingending.packagefairy.utils;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 获取各种OkHttpClient实例
 * Created by CodingEnding on 20178/4/6.
 */

public class OkHttpUtils {
    public static final int CACHE_SIZE=3*1024*1024;//缓存大小（3M）

    private OkHttpUtils(){}

    /**
     * 设置了通用Header拦截器的OkHttpClient
     */
    public static OkHttpClient commonHeaderClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(InterceptorUtils.commonHeaderInterceptor())
                .build();
    }

    /**
     * 设置了服务器响应拦截器的OkHttpClient
     */
    public static OkHttpClient serverClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(InterceptorUtils.serverInterceptor())
                .build();
    }

    /**
     * 设置了添加通用请求参数拦截器的OkHttpClient
     */
    public static OkHttpClient commonParamsClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(InterceptorUtils.commonParamsInterceptor())
                .build();
    }

    /**
     * 设置了缓存拦截器的OkHttpClient
     * TODO：修改缓存的文件目录
     */
    public static OkHttpClient cacheClient(Context context){
        File cache=new File(context.getExternalCacheDir(),"RetrofitCache");
        Interceptor cacheInterceptor=InterceptorUtils.cacheInterceptor();
        return new OkHttpClient.Builder()
                .cache(new Cache(cache,CACHE_SIZE))
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(cacheInterceptor)
                .build();
    }

}
