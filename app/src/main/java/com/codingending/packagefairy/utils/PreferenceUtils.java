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
    public static final String KEY_AUTO_BACKUP="auto_backup";//是否允许自动同步数据
    public static final String KEY_FEEDBACK="user_feedback";//存储反馈信息（临时）
    public static final String KEY_LATEST_NOTIFICATION="latest_notification";//存储最近一次系统通知的发送时间（long）
    public static final String KEY_LATEST_RECOMMEND_DATE="latest_recommend_date";//存储最近一次获得推荐套餐的日期（long）
    public static final String KEY_LATEST_STATISTICS_DATE="latest_statistics_date";//存储最近一次统计数据的时间

    private PreferenceUtils(){}

    //返回一个SharedPreferences实例
    private static SharedPreferences getPreference(Context context){
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

    //存储boolean类型的数据
    public static void putBoolean(Context context,String key,boolean value){
        getPreference(context).edit().putBoolean(key,value).apply();
    }

    //获取boolean类型的数据
    public static boolean getBoolean(Context context,String key){
        return getPreference(context).getBoolean(key,false);
    }

    //存储long类型的数据
    public static void putLong(Context context,String key,long value){
        getPreference(context).edit().putLong(key,value).apply();
    }

    //获取long类型的数据
    public static long getLong(Context context,String key){
        return getPreference(context).getLong(key,0);
    }

    //获取long类型的数据
    public static long getLong(Context context,String key,long defaultValue){
        return getPreference(context).getLong(key,defaultValue);
    }

    //清除指定信息
    public static void remove(Context context,String key){
        getPreference(context).edit().remove(key).apply();
    }

}
