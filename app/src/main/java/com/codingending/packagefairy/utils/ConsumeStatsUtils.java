package com.codingending.packagefairy.utils;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.codingending.packagefairy.bean.StartEnd;

/**
 * 与设备流量/通话时长消耗量数据相关的工具类
 * Created by CodingEnding on 2018/4/5.
 */

public class ConsumeStatsUtils {
    private ConsumeStatsUtils(){}

    /**
     * 获取指定时间段内的通话总时长（只统计呼出电话）
     * 需要权限android.permission.READ_CALL_LOG
     * @param context
     * @param start 开始时间（毫秒）
     * @param end 结束时间（毫秒）
     * @return 通话时长（秒）
     */
    @SuppressLint("MissingPermission")
    public static int getCallTime(Context context, long start, long end){
        ContentResolver resolver=context.getContentResolver();
        String[] columns={CallLog.Calls.DATE,CallLog.Calls.DURATION};//需要查询的列名（通话类型、日期、时长[秒]）

        String startStr=String.valueOf(start);
        String endStr=String.valueOf(end);

        String typeColumn=CallLog.Calls.TYPE;//Type对应的列名
        String dateColumn=CallLog.Calls.DATE;//Date对应的列名
        String selection=String.format("%s >? and %s <? and %s=?",dateColumn,dateColumn,typeColumn);//查询语句
        String outGoingType=CallLog.Calls.OUTGOING_TYPE+"";//呼出类型
        Cursor cursor=resolver.query(CallLog.Calls.CONTENT_URI,columns,
                selection,new String[]{startStr,endStr,outGoingType},null);

        int totalCallTime=0;
        if(cursor.moveToFirst()){//循环统计通话时长
            do {
                totalCallTime+=cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            }while(cursor.moveToNext());
        }
        cursor.close();

        return totalCallTime;
    }

    /**
     * 获取指定时间段内的通话总时长（只统计呼出电话）
     * @param context
     * @param startEnd 开始/结束时间段
     * @return
     */
    public static int getCallTime(Context context, StartEnd startEnd){
        return getCallTime(context,startEnd.start,startEnd.end);
    }

}
