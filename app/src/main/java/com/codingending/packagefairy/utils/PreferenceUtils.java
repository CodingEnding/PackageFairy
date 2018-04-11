package com.codingending.packagefairy.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 与首选项相关的工具类
 * Created by CodingEnding on 2018/4/6.
 */

public class PreferenceUtils {
    public static final String PREFERENCE_FAIRY="preference_fairy";//存储用户的一些基本信息

    public static final String KEY_DEVICE_FINGER="device_finger";//存储设备唯一识别码
    public static final String KEY_DEVICE_TYPE="device_type";//存储设备品牌（如华为 WAS-AL00）
    public static final String KEY_SYSTEM_VERSION="system_version";//存储设备版本（如Android 7.0）
    public static final String KEY_USER_ID="user_id";//存储用户Id
    public static final String KEY_USER_NAME="user_name";//存储用户名
    public static final String KEY_USER_EMAIL="user_email";//存储邮箱
    public static final String KEY_USER_PHONE="user_phone";//存储电话号码
    public static final String KEY_USER_SESSION_TOKEN="user_session_token";//存储SessionToken
    public static final String KEY_EMAIL_VERIFIED="email_verified";//邮箱是否已经验证

    private PreferenceUtils(){}

    //返回一个SharedPreferences实例
    public static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(PREFERENCE_FAIRY,Context.MODE_PRIVATE);
    }

    //存储String类型的数据
    public static void putString(Context context,String key,String value){
        getPreference(context).edit().putString(key,value).apply();
    }

    //获取String类型的数据
    public static String getString(Context context,String key){
        return getPreference(context).getString(key,"");
    }

    //获取String类型的数据（带默认值）
    public static String getString(Context context,String key,String defaultValue){
        return getPreference(context).getString(key,defaultValue);
    }

    //存储int类型的数据
    public static void putInt(Context context,String key,int value){
        getPreference(context).edit().putInt(key,value).apply();
    }

    //获取int类型的数据
    public static int getInt(Context context,String key){
        return getPreference(context).getInt(key,0);
    }

    //清除指定信息
    public static void remove(Context context,String key){
        getPreference(context).edit().remove(key).apply();
    }

}
