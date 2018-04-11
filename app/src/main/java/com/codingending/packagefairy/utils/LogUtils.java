package com.codingending.packagefairy.utils;

import android.util.Log;

/**
 * 日志工具
 * Created by CodingEnding on 2018/4/7.
 */

public class LogUtils {
    public static final int LEVEL_DEBUG=2;
    public static final int LEVEL_INFO=3;
    public static final int LEVEL_WARNING=4;
    public static final int LEVEL_ERROR=5;
    public static final int LEVEL_NOTHING=10;//什么都不打印

    public static int level=LEVEL_INFO;//当前打印级别

    private LogUtils(){}

    public static void d(String tag,String msg){
        if(LEVEL_DEBUG>=level){
            Log.d(tag,msg);
        }
    }

    public static void i(String tag,String msg){
        if(LEVEL_INFO>=level){
            Log.i(tag,msg);
        }
    }

    public static void w(String tag,String msg){
        if(LEVEL_WARNING>=level){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag,String msg){
        if(LEVEL_ERROR>=level){
            Log.e(tag,msg);
        }
    }

}
