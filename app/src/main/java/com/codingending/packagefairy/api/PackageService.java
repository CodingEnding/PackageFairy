package com.codingending.packagefairy.api;

import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.entity.UserConsume;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 提供套餐相关的服务
 * Created by CodingEnding on 2018/4/6.
 */

public interface PackageService {
    int CATEGORY_PAGE_ITEM_COUNT=25;//在分类浏览页面中每页的套餐数目
    int HOT_PAGE_ITEM_COUNT=30;//在热门套餐列表中每页的套餐数目
    int SEARCH_PAGE_ITEM_COUNT=15;//在搜索界面每页的套餐数目
    String CATEGORY_NAME_SINGLE="SINGLE_CATEGORY";//单分类情况下的category_name（比如查询日租卡、免流特权卡...）
    String CATEGORY_DAY_RENT="DAY_RENT";//[分类]日租卡
    String CATEGORY_FREE_FLOW="FREE_FLOW";//[分类]免流特权卡
    String CATEGORY_INFINITE_FLOW="INFINITE_FLOW";//[分类]无限流量卡
    String CATEGORY_ALL="ALL";//[分类]全部种类

    String CATEGORY_NAME_OPERATOR="operator";//[分类名]运营商
    String CATEGORY_NAME_PARTNER="partner";//[分类名]合作方

    @Headers({"Content-Type:application/json"})
    @POST("v1/package/recommend")
    //获取推荐套餐列表
    Call<DataResponse<List<PackageBean>>> getRecommendPackage(@Body UserConsume userConsume);

    @GET("v1/package/hot")
    //获取热门套餐列表
    Call<DataResponse<List<SimplePackageBean>>> getHotPackage(@Query("limit") int limit,
                                                              @Query("page") int page);

    @GET("v1/package/get")
    //获取指定Id的套餐
    Call<DataResponse<PackageBean>> getPackageById(@Query("package_id") int packageId);

    @GET("v1/package/search")
    //根据关键词查询套餐
    Call<DataResponse<List<SimplePackageBean>>> getPackageByKey(@Query("key") String key,
                                                                @Query("limit") int limit,
                                                                @Query("page") int page);

    @GET("v1/package/list")
    //获取指定分类的套餐
    Call<DataResponse<List<SimplePackageBean>>> getPackageByCategory(@Query("category_name") String categoryName,
                                                                     @Query("category_value") String categoryValue,
                                                                     @Query("limit") int limit,
                                                                     @Query("page") int page);

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("v1/package/score")
    //对指定套餐评分
    Call<BaseResponse> score(@Field("package_id") int packageId,
                             @Field("user_id") int userId,
                             @Field("score") int score);

    @GET("v1/package/myScore")
    //获取[我的评分]
    Call<DataResponse<Integer>> getMyScore(@Query("package_id") int packageId,
                                           @Query("user_id") int userId);

    @GET("v1/package/scoreCount")
    //获取指定套餐的评分人数
    Call<DataResponse<Integer>> getScoreCount(@Query("package_id") int packageId);

}
