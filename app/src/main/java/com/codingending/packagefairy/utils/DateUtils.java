package com.codingending.packagefairy.utils;

import com.codingending.packagefairy.bean.StartEnd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 与日期相关的工具类
 * Created by CodingEnding on 2018/4/5.
 */

public class DateUtils {
    private DateUtils(){}

    //获得本月第一天0点时间（long的形式）
    public static long getFirstMonthDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    //获得本月第一天0点时间（Calendar的形式）
    public static Calendar getFirstMonthDayInCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal;
    }

    //获取今天是本月第几天（从1开始）
    public static int getTodayOfMonth(){
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    //获取从本月第一天到现在的时间段列表
    public static List<StartEnd> getFirstMonthDayToNowTimeList(){
        List<StartEnd> timeList=new ArrayList<>();
        Calendar rootCalendar=getFirstMonthDayInCalendar();
        Calendar currentCalendar=Calendar.getInstance();//现在的时间
        int dayOfMonth=currentCalendar.get(Calendar.DAY_OF_MONTH);//获得今天是本月的第几天（从1开始）

        long start=0;
        long end=0;
        for(int i=0;i<dayOfMonth;i++){//循环获取每一天的时间段
            start=rootCalendar.getTimeInMillis();
            rootCalendar.add(Calendar.DAY_OF_MONTH,1);//每次向后跳转一天
            end=rootCalendar.getTimeInMillis();
            timeList.add(new StartEnd(start,end));
        }
        return timeList;
    }

}
