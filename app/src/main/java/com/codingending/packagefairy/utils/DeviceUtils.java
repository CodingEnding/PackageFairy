package com.codingending.packagefairy.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.UUID;

/**
 * 与设备相关的工具类
 * Created by CodingEnding on 2018/4/5.
 */

public class DeviceUtils {
    private DeviceUtils(){}

    /**
     * 获取手机型号（厂商名称+手机型号）
     */
    public static String getDeviceType(){
        return Build.BRAND+" "+Build.MODEL;
    }

    /**
     * 获取系统版本描述
     */
    public static String getSystemVersion(){
        return "Android "+Build.VERSION.RELEASE;
    }

    /**
     * 获取设备唯一识别码（androidId+Serial Number）
     */
    public static String getDeviceFinger(Context context){
        String androidId= Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);//获取设备初始Id（恢复出厂设置会重置）
        String deviceFinger=androidId+Build.SERIAL;//组合两种识别码
        deviceFinger=EncryptUtils.md5String(deviceFinger);//进行MD5哈希（结果更合理且不会暴露用户信息）
        return deviceFinger;
    }

}
