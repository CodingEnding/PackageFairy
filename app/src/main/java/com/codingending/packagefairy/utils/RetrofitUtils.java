package com.codingending.packagefairy.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit相关的工具类
 * Created by CodingEnding on 2018/4/6.
 */

public class RetrofitUtils {
    public static final String BASE_URL="http://120.79.250.210/PackageSystem/api/";//基本网址
    private volatile static Retrofit retrofit;

    private RetrofitUtils(){}

    /**
     * 获取一个普通的Retrofit实例
     * 使用双重检查机制实现单例
     */
    public static Retrofit getRetrofit(){
        if(retrofit==null){
            synchronized(Retrofit.class){
                if(retrofit==null){
                    retrofit=new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())//json转换器
                            .build();
                }
            }
        }
        return retrofit;
    }

}
