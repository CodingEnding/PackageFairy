package com.codingending.packagefairy.api;

import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.entity.UserBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 提供用户相关的服务
 * Created by CodingEnding on 2018/4/9.
 */
public interface UserService {
    int CODE_MAIL_EXIST=5401;//邮箱已存在
    int CODE_REGISTER_ERROR=5400;//注册失败
    int CODE_LOGIN_ERROR=5500;//登录失败

    @Headers({"Content-Type:application/json"})
    @POST("v1/user/register")
    //注册新用户
    Call<DataResponse<Integer>> register(@Body UserBean userBean);

    @GET("v1/user/login")
    //登录
    Call<DataResponse<UserBean>> login(@Query("email") String email,
                                       @Query("password") String password,
                                       @Query("device_type") String device_type,
                                       @Query("system_version") String systemVersion,
                                       @Query("device_finger") String deviceFinger);


    @GET("v1/package/list")
    //获取分类浏览的套餐列表
    Call<DataResponse<List<SimplePackageBean>>> getHotPackage(@Query("category_name") String categoryName,
                                                              @Query("category_value") String categoryValue,
                                                              @Query("limit") int limit,
                                                              @Query("page") int page);


}
