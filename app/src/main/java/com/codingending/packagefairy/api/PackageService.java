package com.codingending.packagefairy.api;

import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.entity.UserConsume;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * 提供套餐相关的服务
 * Created by CodingEnding on 2018/4/6.
 */

public interface PackageService {

    @Headers({"Content-Type:application/json"})
    @POST("v1/package/recommend")
    //获取推荐套餐列表
    Call<DataResponse<List<PackageBean>>> getRecommendPackage(@Body UserConsume userConsume);

    @GET("v1/package/hot")
    //获取热门套餐列表
    Call<DataResponse<List<SimplePackageBean>>> getHotPackage();

}
