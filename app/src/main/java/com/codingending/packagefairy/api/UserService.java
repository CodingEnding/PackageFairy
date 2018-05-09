package com.codingending.packagefairy.api;

import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.NotificationBean;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.entity.UserBean;

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
 * 提供用户相关的服务
 * Created by CodingEnding on 2018/4/9.
 */
public interface UserService {
    int CODE_MAIL_EXIST=5401;//邮箱已存在
    int CODE_REGISTER_ERROR=5400;//注册失败
    int CODE_LOGIN_ERROR=5500;//登录失败

    int TYPE_USERNAME=1;
    int TYPE_PHONE=2;

    int NOTIFICATION_COUNT=15;//通知信息的条数

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

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("v1/user/modifyInfo")
    //修改用户信息
    Call<BaseResponse> modifyInfo(@Field("type") int type,
                                  @Field("email") String email,
                                  @Field("content") String content,
                                  @Field("session_token") String sessionToken);

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("v1/user/modifyPwd")
    //修改用户密码
    Call<BaseResponse> modifyPassword(@Field("email") String email,
                                 @Field("old_pwd") String oldPwd,
                                 @Field("new_pwd") String newPwd);

    @GET("v1/user/forgetCode")
    //获取[忘记密码]的验证码
    Call<BaseResponse> getForgetCode(@Query("email") String email);

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("v1/user/forget")
    //重置密码[忘记密码]
    Call<BaseResponse> forget(@Field("email") String email,
                                 @Field("password") String password,
                                 @Field("code") String code);

    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=utf-8")
    @POST("v1/user/feedback")
     //反馈功能
    Call<BaseResponse> postFeedback(@Field("email") String email,
                              @Field("content") String content,
                              @Field("device_finger") String deviceFinger);

    @GET("v1/user/notification")
    //获取通知信息
    Call<DataResponse<List<NotificationBean>>> getNotification(@Query("limit") int limit,
                                                               @Query("page") int page);


}
