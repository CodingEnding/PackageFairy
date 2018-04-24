package com.codingending.packagefairy.api;

import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.DeviceBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 提供设备相关的服务
 * Created by CodingEnding on 2018/4/23.
 */
public interface DeviceService {
    @FormUrlEncoded
    @POST("v1/device/backup")
    //更新套餐数据
    Call<BaseResponse> backup(@Field("device_type") String deviceType,
                              @Field("system_version") String systemVersion,
                              @Field("device_finger") String deviceFinger,
                              @Field("email") String email,
                              @Field("session_token") String sessionToken);

    @GET("v1/device/list")
    //获取套餐列表
    Call<DataResponse<List<DeviceBean>>> getDeviceList(@Query("email") String email,
                                     @Query("session_token") String sessionToken);

}
